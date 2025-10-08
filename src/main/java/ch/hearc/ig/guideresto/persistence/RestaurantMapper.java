package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.City;
import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.business.RestaurantType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class RestaurantMapper extends AbstractMapper<Restaurant> {

    @Override
    public Restaurant findById(int id) {
        // 1️⃣ Vérifie dans le cache
        if (!isCacheEmpty() && identityMap.containsKey(id)) {
            logger.debug("Restaurant {} trouvé dans le cache.", id);
            return identityMap.get(id);
        }

        // 2️⃣ Requête SQL
        String sql =
                "SELECT r.numero          AS r_id, " +
                        "       r.nom             AS r_nom, " +
                        "       r.description     AS r_desc, " +
                        "       r.site_web        AS r_site, " +
                        "       r.adresse         AS r_street, " +
                        "       v.numero          AS v_id, " +
                        "       v.nom_ville       AS v_name, " +
                        "       v.code_postal     AS v_zip, " +
                        "       t.numero          AS t_id, " +
                        "       t.libelle         AS t_label, " +
                        "       t.description     AS t_desc " +
                        "FROM   restaurants r " +
                        "JOIN   villes v ON v.numero = r.fk_vill " +
                        "JOIN   types_gastronomiques t ON t.numero = r.fk_type " +
                        "WHERE  r.numero = ?";

        Connection connection = ConnectionUtils.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                City city = new City(
                        rs.getInt("v_id"),
                        rs.getString("v_name"),
                        rs.getString("v_zip")
                );

                RestaurantType type = new RestaurantType(
                        rs.getInt("t_id"),
                        rs.getString("t_label"),
                        rs.getString("t_desc")
                );

                Restaurant rest = new Restaurant(
                        rs.getInt("r_id"),
                        rs.getString("r_nom"),
                        rs.getString("r_desc"),
                        rs.getString("r_site"),
                        rs.getString("r_street"),
                        city,
                        type
                );

                addToCache(rest);
                logger.debug("Restaurant {} ajouté au cache.", id);
                return rest;
            }

        } catch (SQLException ex) {
            logger.error("SQLException in findById({}): {}", id, ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public Set<Restaurant> findAll() {
        Set<Restaurant> restaurants = new HashSet<>();

        if (!isCacheEmpty()) {
            logger.debug("findAll() : données retournées depuis le cache ({} éléments).", identityMap.size());
            return new HashSet<>(identityMap.values());
        }

        String sql =
                "SELECT r.numero          AS r_id, " +
                        "       r.nom             AS r_nom, " +
                        "       r.description     AS r_desc, " +
                        "       r.site_web        AS r_site, " +
                        "       r.adresse         AS r_street, " +
                        "       v.numero          AS v_id, " +
                        "       v.nom_ville       AS v_name, " +
                        "       v.code_postal     AS v_zip, " +
                        "       t.numero          AS t_id, " +
                        "       t.libelle         AS t_label, " +
                        "       t.description     AS t_desc " +
                        "FROM   restaurants r " +
                        "JOIN   villes v ON v.numero = r.fk_vill " +
                        "JOIN   types_gastronomiques t ON t.numero = r.fk_type";

        Connection connection = ConnectionUtils.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("r_id");

                if (identityMap.containsKey(id)) {
                    restaurants.add(identityMap.get(id));
                    continue;
                }

                City city = new City(
                        rs.getInt("v_id"),
                        rs.getString("v_name"),
                        rs.getString("v_zip")
                );

                RestaurantType type = new RestaurantType(
                        rs.getInt("t_id"),
                        rs.getString("t_label"),
                        rs.getString("t_desc")
                );

                Restaurant restaurant = new Restaurant(
                        id,
                        rs.getString("r_nom"),
                        rs.getString("r_desc"),
                        rs.getString("r_site"),
                        rs.getString("r_street"),
                        city,
                        type
                );

                addToCache(restaurant);
                restaurants.add(restaurant);
            }

            logger.debug("findAll() : {} restaurants chargés depuis la DB.", restaurants.size());

        } catch (SQLException ex) {
            logger.error("SQLException in findAll(): {}", ex.getMessage(), ex);
        }

        return restaurants;
    }

    @Override
    public Restaurant create(Restaurant object) {
        Connection c = ConnectionUtils.getConnection();
        try {
            // 1️⃣ Récupérer la prochaine valeur de la séquence
            int nextId = getSequenceValue();
            object.setId(nextId);

            // 2️⃣ Insérer en base
            String sql = "INSERT INTO RESTAURANTS (NUMERO, NOM, ADRESSE, DESCRIPTION, SITE_WEB, FK_TYPE, FK_VILL) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement s = c.prepareStatement(sql)) {
                s.setInt(1, object.getId());
                s.setString(2, object.getName());
                s.setString(3, object.getAddress().getStreet());
                s.setString(4, object.getDescription());
                s.setString(5, object.getWebsite());
                s.setInt(6, object.getType().getId());
                s.setInt(7, object.getAddress().getCity().getId());

                s.executeUpdate();
                c.commit();
            }

            // 3️⃣ Ajouter au cache
            addToCache(object);
            logger.debug("Restaurant {} ajouté au cache après création.", object.getId());

            return object;

        } catch (SQLException e) {
            logger.error("SQLException in create(): {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean update(Restaurant object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "UPDATE RESTAURANTS " +
                "SET NOM = ?, ADRESSE = ?, DESCRIPTION = ?, SITE_WEB = ?, FK_TYPE = ?, FK_VILL = ? " +
                "WHERE NUMERO = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, object.getName());
            s.setString(2, object.getAddress().getStreet());
            s.setString(3, object.getDescription());
            s.setString(4, object.getWebsite());
            s.setInt(5, object.getType().getId());
            s.setInt(6, object.getAddress().getCity().getId());
            s.setInt(7, object.getId());

            s.executeUpdate();
            c.commit();

            addToCache(object);
            logger.debug("Restaurant {} mis à jour dans le cache.", object.getId());
            return true;

        } catch (SQLException e) {
            logger.error("SQLException in update(): {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Restaurant object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "DELETE FROM RESTAURANTS WHERE NUMERO = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, object.getId());
            s.executeUpdate();
            c.commit();

            removeFromCache(object.getId());
            logger.debug("Restaurant {} supprimé du cache et de la DB.", object.getId());
            return true;

        } catch (SQLException ex) {
            logger.error("SQLException in delete(): {}", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        Restaurant rest = findById(id);
        if (rest == null) return false;
        return delete(rest);
    }


    @Override
    protected String getSequenceQuery() {
        // Oracle : renvoie la prochaine valeur de la séquence SEQ_RESTAURANTS
        return "SELECT SEQ_RESTAURANTS.NEXTVAL FROM DUAL";
    }

    @Override
    protected String getExistsQuery() {
        return "SELECT 1 FROM RESTAURANTS WHERE NUMERO = ?";
    }

    @Override
    protected String getCountQuery() {
        return "SELECT COUNT(*) FROM RESTAURANTS";
    }
}
