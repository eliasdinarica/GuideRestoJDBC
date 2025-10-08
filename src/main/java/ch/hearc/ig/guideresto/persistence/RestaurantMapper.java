package ch.hearc.ig.guideresto.persistence;
import ch.hearc.ig.guideresto.business.City;
import ch.hearc.ig.guideresto.business.IBusinessObject;
import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.business.RestaurantType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class RestaurantMapper extends AbstractMapper<Restaurant> {

    public Restaurant findById(int id) {
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

                // création de la ville
                City city = new City(
                        rs.getInt("v_id"),
                        rs.getString("v_name"),
                        rs.getString("v_zip")
                );

                // création du type restaurant
                RestaurantType type = new RestaurantType(
                        rs.getInt("t_id"),
                        rs.getString("t_label"),
                        rs.getString("t_desc")
                );

                // création du restaurant
                return new Restaurant(
                        rs.getInt("r_id"),
                        rs.getString("r_nom"),
                        rs.getString("r_desc"),
                        rs.getString("r_site"),
                        rs.getString("r_street"),
                        city,
                        type
                );
            }
        } catch (SQLException ex) {
            logger.error("SQLException in findById({}): {}", id, ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public Set<Restaurant> findAll() {
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

        Set<Restaurant> restaurants = new HashSet<>();

        Connection connection = ConnectionUtils.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // création de la ville
                City city = new City(
                        rs.getInt("v_id"),
                        rs.getString("v_name"),
                        rs.getString("v_zip")
                );

                // création du type restaurant
                RestaurantType type = new RestaurantType(
                        rs.getInt("t_id"),
                        rs.getString("t_label"),
                        rs.getString("t_desc")
                );

                // création du restaurant
                Restaurant restaurant = new Restaurant(
                        rs.getInt("r_id"),
                        rs.getString("r_nom"),
                        rs.getString("r_desc"),
                        rs.getString("r_site"),
                        rs.getString("r_street"),
                        city,
                        type
                );

                restaurants.add(restaurant);
            }

        } catch (SQLException ex) {
            logger.error("SQLException in findAll(): {}", ex.getMessage(), ex);
        }

        return restaurants;
    }


    @Override
    public Restaurant create(Restaurant object) {
        Connection c = ConnectionUtils.getConnection();
        try (
                PreparedStatement s = c.prepareStatement("INSERT INTO RESTAURANTS (NOM, ADRESSE,DESCRIPTION,SITE_WEB,FK_TYPE,FK_VILL) VALUES (? ,?, ?, ?, ?, ?)")
        ) {

            s.setString(1, object.getName());
            s.setString(2, object.getAddress().getStreet());
            s.setString(3, object.getDescription());
            s.setString(4, object.getWebsite());
            s.setInt(5, object.getType().getId());
            s.setInt(6, object.getAddress().getCity().getId());

            s.executeUpdate();
            c.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }

        return null;
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

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Restaurant object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "DELETE FROM RESTAURANTS WHERE NUMERO = ?";


        //Faut encore aller suppr les likes d'abord zgegos
        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, object.getId());

            s.executeUpdate();
            c.commit();
            return true;

    }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
        return false;
        }
    }


    @Override
    public boolean deleteById(int id) {
        Restaurant rest = findById(id);
        return this.delete(rest);
    }

    @Override
    protected String getSequenceQuery() {
        return "";
    }

    @Override
    protected String getExistsQuery() {
        return "SELECT 1 FROM restaurants WHERE numero = ?";
    }

    @Override
    protected String getCountQuery() {
        return "";
    }
}
