package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.City;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CityMapper extends AbstractMapper<City> {

    protected static final Map<Integer, City> identityMap = new HashMap<>();

    @Override
    protected Map<Integer, City> getIdentityMap() {
        return identityMap;
    }

    @Override
    public City findById(int id) {
        if (!isCacheEmpty() && identityMap.containsKey(id)) {
            logger.debug("City {} trouvée dans le cache.", id);
            return identityMap.get(id);
        }

        String sql = "SELECT * FROM VILLES WHERE NUMERO = ?";
        Connection c = ConnectionUtils.getConnection();

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, id);
            try (ResultSet rs = s.executeQuery()) {
                if (!rs.next()) return null;

                City city = new City();
                city.setId(rs.getInt("NUMERO"));
                city.setZipCode(rs.getString("CODE_POSTAL"));
                city.setCityName(rs.getString("NOM_VILLE"));

                addToCache(city);
                logger.debug("City {} ajoutée au cache.", id);
                return city;
            }
        } catch (SQLException e) {
            logger.error("SQLException in findById({}): {}", id, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Set<City> findAll() {
        Set<City> cities = new HashSet<>();

        if (!isCacheEmpty()) {
            logger.debug("findAll() : données retournées depuis le cache ({} éléments).", identityMap.size());
            return new HashSet<>(identityMap.values());
        }

        String sql = "SELECT * FROM VILLES";
        Connection c = ConnectionUtils.getConnection();

        try (PreparedStatement s = c.prepareStatement(sql);
             ResultSet rs = s.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("NUMERO");

                if (identityMap.containsKey(id)) {
                    cities.add(identityMap.get(id));
                    continue;
                }

                City city = new City();
                city.setId(id);
                city.setZipCode(rs.getString("CODE_POSTAL"));
                city.setCityName(rs.getString("NOM_VILLE"));

                addToCache(city);
                cities.add(city);
            }

            logger.debug("findAll() : {} villes chargées depuis la DB.", cities.size());

        } catch (SQLException e) {
            logger.error("SQLException in findAll(): {}", e.getMessage());
        }

        return cities;
    }

    @Override
    public City create(City object) {
        Connection c = ConnectionUtils.getConnection();
        try {
            int nextId = getSequenceValue();
            object.setId(nextId);

            String sql = "INSERT INTO VILLES (NUMERO, CODE_POSTAL, NOM_VILLE) VALUES (?, ?, ?)";
            try (PreparedStatement s = c.prepareStatement(sql)) {
                s.setInt(1, object.getId());
                s.setString(2, object.getZipCode());
                s.setString(3, object.getCityName());

                s.executeUpdate();
                c.commit();
            }

            addToCache(object);
            logger.debug("City {} ajoutée au cache après création.", object.getId());
            return object;

        } catch (SQLException e) {
            logger.error("SQLException in create(): {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean update(City object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "UPDATE VILLES SET CODE_POSTAL = ?, NOM_VILLE = ? WHERE NUMERO = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, object.getZipCode());
            s.setString(2, object.getCityName());
            s.setInt(3, object.getId());

            s.executeUpdate();
            c.commit();

            addToCache(object);
            logger.debug("City {} mise à jour dans le cache.", object.getId());
            return true;

        } catch (SQLException e) {
            logger.error("SQLException in update(): {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(City object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "DELETE FROM VILLES WHERE NUMERO = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, object.getId());
            s.executeUpdate();
            c.commit();

            removeFromCache(object.getId());
            logger.debug("City {} supprimée du cache et de la DB.", object.getId());
            return true;

        } catch (SQLException e) {
            logger.error("SQLException in delete(): {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        City city = findById(id);
        if (city == null) return false;
        return delete(city);
    }

    @Override
    protected String getSequenceQuery() {
        return "SELECT SEQ_VILLES.NEXTVAL FROM DUAL";
    }

    @Override
    protected String getExistsQuery() {
        return "SELECT 1 FROM VILLES WHERE NUMERO = ?";
    }

    @Override
    protected String getCountQuery() {
        return "SELECT COUNT(*) FROM VILLES";
    }
}
