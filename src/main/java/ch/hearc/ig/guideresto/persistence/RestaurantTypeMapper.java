package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.BasicEvaluation;
import ch.hearc.ig.guideresto.business.RestaurantType;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class RestaurantTypeMapper extends AbstractMapper<RestaurantType> {
    @Override
    public RestaurantType findById(int id) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "SELECT * FROM TYPES_GASTRONOMIQUES WHERE ID = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, id);

            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                RestaurantType type = new RestaurantType();
                type.setId(rs.getInt("ID"));
                type.setLabel(rs.getString("LIBELLE"));
                type.setDescription(rs.getString("DESCRIPTION"));

                return type;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Override
    public Set<RestaurantType> findAll() {
        Connection c = ConnectionUtils.getConnection();
        String sql = "SELECT * FROM TYPES_GASTRONOMIQUES";
        Set<RestaurantType> types = new HashSet<>();

        try (PreparedStatement s = c.prepareStatement(sql)) {
            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                RestaurantType type = new RestaurantType();
                type.setId(rs.getInt("ID"));
                type.setLabel(rs.getString("LIBELLE"));
                type.setDescription(rs.getString("DESCRIPTION"));

                types.add(type);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return types;
    }

    @Override
    public RestaurantType create(RestaurantType object) {
        Connection c = ConnectionUtils.getConnection();
        try (
                PreparedStatement s = c.prepareStatement(
                        "INSERT INTO TYPES_GASTRONOMIQUES (LIBELLE, DESCRIPTION) VALUES (?, ?)"
                )
        ) {
            s.setString(1, object.getLabel());
            s.setString(2, object.getDescription());

            s.executeUpdate();
            c.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return object;
    }


    @Override
    public boolean update(RestaurantType object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "UPDATE TYPES_GASTRONOMIQUES " +
                "SET LIBELLE = ?, DESCRIPTION = ? " +
                "WHERE ID = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, object.getLabel());
            s.setString(2, object.getDescription());
            s.setInt(3, object.getId());

            s.executeUpdate();
            c.commit();

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }




    @Override
    public boolean delete(RestaurantType object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "DELETE FROM TYPES_GASTRONOMIQUES WHERE ID = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, object.getId());

            s.executeUpdate();
            c.commit();

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    @Override
    public boolean deleteById(int id) {
        RestaurantType type = findById(id);
        return this.delete(type);
    }


    @Override
    protected String getSequenceQuery() {
        return "";
    }

    @Override
    protected String getExistsQuery() {
        return "";
    }

    @Override
    protected String getCountQuery() {
        return "";
    }
}
