package ch.hearc.ig.guideresto.persistence;
import ch.hearc.ig.guideresto.business.IBusinessObject;
import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.business.RestaurantType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class RestaurantMapper extends AbstractMapper<Restaurant> {

    @Override
    public Restaurant findById(int id){
        String sql = "SELECT numero, nom, adresse FROM restaurants WHERE numero = ?";
        return null;
    }

    @Override
    public Set findAll() {
        return Set.of();
    }

    @Override
    public Restaurant create(Restaurant object) {
        Connection c = ConnectionUtils.getConnection();
        try (
                PreparedStatement s = c.prepareStatement("INSERT INTO RESTAURANTS (NOM, ADRESSE,DESCRIPTION,FK_TYPE,FK_VILL) VALUES (?, ?, ?, ?, ?)")
        ) {

            s.setString(1, object.getName());
            s.setString(2, object.getAddress().getStreet());
            s.setString(3, object.getDescription());
            s.setInt(4, object.getType().getId());
            s.setInt(5, object.getAddress().getCity().getId());

            s.executeUpdate();
            c.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }

        return null;
    }

    @Override
    public boolean update(Restaurant object) {
        return false;
    }

    @Override
    public boolean delete(Restaurant object) {
        return false;
    }


    @Override
    public boolean deleteById(int id) {
        return false;
    }

    @Override
    protected String getSequenceQuery() {
        return "";
    }

    @Override
    protected String getExistsQuery() {
        return "SELECT 1 FROM restaurants WHERE id = ?";
    }

    @Override
    protected String getCountQuery() {
        return "";
    }
}
