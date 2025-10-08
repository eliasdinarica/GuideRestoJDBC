package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.RestaurantType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class RestaurantTypeMapper extends AbstractMapper<RestaurantType> {

    @Override
    public RestaurantType findById(int id) {
        // 1️⃣ Vérifie dans le cache
        if (!isCacheEmpty() && identityMap.containsKey(id)) {
            logger.debug("RestaurantType {} trouvé dans le cache.", id);
            return identityMap.get(id);
        }

        // 2️⃣ Requête SQL
        String sql = "SELECT * FROM TYPES_GASTRONOMIQUES WHERE NUMERO = ?";
        Connection c = ConnectionUtils.getConnection();

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, id);

            try (ResultSet rs = s.executeQuery()) {
                if (!rs.next()) return null;

                RestaurantType type = new RestaurantType();
                type.setId(rs.getInt("NUMERO"));
                type.setLabel(rs.getString("LIBELLE"));
                type.setDescription(rs.getString("DESCRIPTION"));

                // 3️⃣ Ajouter au cache
                addToCache(type);
                logger.debug("RestaurantType {} ajouté au cache.", id);
                return type;
            }

        } catch (SQLException ex) {
            logger.error("SQLException in findById({}): {}", id, ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public Set<RestaurantType> findAll() {
        Set<RestaurantType> types = new HashSet<>();

        if (!isCacheEmpty()) {
            logger.debug("findAll() : données retournées depuis le cache ({} éléments).", identityMap.size());
            return new HashSet<>(identityMap.values());
        }

        String sql = "SELECT * FROM TYPES_GASTRONOMIQUES";
        Connection c = ConnectionUtils.getConnection();

        try (PreparedStatement s = c.prepareStatement(sql);
             ResultSet rs = s.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("NUMERO");

                if (identityMap.containsKey(id)) {
                    types.add(identityMap.get(id));
                    continue;
                }

                RestaurantType type = new RestaurantType();
                type.setId(id);
                type.setLabel(rs.getString("LIBELLE"));
                type.setDescription(rs.getString("DESCRIPTION"));

                addToCache(type);
                types.add(type);
            }

            logger.debug("findAll() : {} RestaurantTypes chargés depuis la DB.", types.size());

        } catch (SQLException ex) {
            logger.error("SQLException in findAll(): {}", ex.getMessage(), ex);
        }

        return types;
    }

    @Override
    public RestaurantType create(RestaurantType object) {
        Connection c = ConnectionUtils.getConnection();
        try {
            // 1️⃣ Récupérer la prochaine valeur de la séquence
            int nextId = getSequenceValue();
            object.setId(nextId);

            // 2️⃣ Insérer en base
            String sql = "INSERT INTO TYPES_GASTRONOMIQUES (NUMERO, LIBELLE, DESCRIPTION) VALUES (?, ?, ?)";

            try (PreparedStatement s = c.prepareStatement(sql)) {
                s.setInt(1, object.getId());
                s.setString(2, object.getLabel());
                s.setString(3, object.getDescription());

                s.executeUpdate();
                c.commit();
            }

            // 3️⃣ Ajouter au cache
            addToCache(object);
            logger.debug("RestaurantType {} ajouté au cache après création.", object.getId());

            return object;

        } catch (SQLException e) {
            logger.error("SQLException in create(): {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean update(RestaurantType object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "UPDATE TYPES_GASTRONOMIQUES " +
                "SET LIBELLE = ?, DESCRIPTION = ? " +
                "WHERE NUMERO = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, object.getLabel());
            s.setString(2, object.getDescription());
            s.setInt(3, object.getId());

            s.executeUpdate();
            c.commit();

            addToCache(object);
            logger.debug("RestaurantType {} mis à jour dans le cache.", object.getId());
            return true;

        } catch (SQLException e) {
            logger.error("SQLException in update(): {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(RestaurantType object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "DELETE FROM TYPES_GASTRONOMIQUES WHERE NUMERO = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, object.getId());
            s.executeUpdate();
            c.commit();

            removeFromCache(object.getId());
            logger.debug("RestaurantType {} supprimé du cache et de la DB.", object.getId());
            return true;

        } catch (SQLException e) {
            logger.error("SQLException in delete(): {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        RestaurantType type = findById(id);
        if (type == null) return false;
        return delete(type);
    }

    @Override
    protected String getSequenceQuery() {
        // Oracle : renvoie la prochaine valeur de la séquence SEQ_TYPES_GASTRONOMIQUES
        return "SELECT SEQ_TYPES_GASTRONOMIQUES.NEXTVAL FROM DUAL";
    }

    @Override
    protected String getExistsQuery() {
        return "SELECT 1 FROM TYPES_GASTRONOMIQUES WHERE NUMERO = ?";
    }

    @Override
    protected String getCountQuery() {
        return "SELECT COUNT(*) FROM TYPES_GASTRONOMIQUES";
    }
}
