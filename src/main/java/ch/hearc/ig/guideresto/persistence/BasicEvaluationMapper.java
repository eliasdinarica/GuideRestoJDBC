package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.BasicEvaluation;
import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.Restaurant;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class BasicEvaluationMapper extends AbstractMapper<BasicEvaluation> {
    @Override
    public BasicEvaluation findById(int id) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "SELECT * FROM LIKES WHERE NUMERO = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, id);

            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                BasicEvaluation evaluation = new BasicEvaluation();
                evaluation.setId(rs.getInt("NUMERO"));
                evaluation.setLikeRestaurant("T".equalsIgnoreCase(rs.getString("APPRECIATION")));
                evaluation.setVisitDate(rs.getDate("DATE_EVAL"));
                evaluation.setIpAddress(rs.getString("ADRESSE_IP"));

                Restaurant restaurant = new Restaurant();
                restaurant.setId(rs.getInt("FK_REST"));
                evaluation.setRestaurant(restaurant);

                return evaluation;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
    @Override
    public Set<BasicEvaluation> findAll() {
        Connection c = ConnectionUtils.getConnection();
        String sql = "SELECT * FROM LIKES";
        Set<BasicEvaluation> evaluations = new HashSet<>();

        try (PreparedStatement s = c.prepareStatement(sql)) {
            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                BasicEvaluation evaluation = new BasicEvaluation();
                evaluation.setId(rs.getInt("NUMERO"));
                evaluation.setLikeRestaurant("T".equalsIgnoreCase(rs.getString("APPRECIATION")));
                evaluation.setVisitDate(rs.getDate("DATE_EVAL"));
                evaluation.setIpAddress(rs.getString("ADRESSE_IP"));

                Restaurant restaurant = new Restaurant();
                restaurant.setId(rs.getInt("FK_REST"));
                evaluation.setRestaurant(restaurant);

                evaluations.add(evaluation);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return evaluations;
    }


    @Override
    public BasicEvaluation create(BasicEvaluation object) {
        Connection c = ConnectionUtils.getConnection();
        try (
                PreparedStatement s = c.prepareStatement(
                        "INSERT INTO LIKES (APPRECIATION, DATE_EVAL, ADRESSE_IP, FK_REST) VALUES (?, ?, ?, ?)"
                )
        ) {
            s.setString(1, object.getLikeRestaurant() ? "T" : "F");
            s.setDate(2, new java.sql.Date(object.getVisitDate().getTime()));
            s.setString(3, object.getIpAddress());
            s.setInt(4, object.getRestaurant().getId());

            s.executeUpdate();
            c.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return object;
    }

    @Override
    public boolean update(BasicEvaluation object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "UPDATE LIKES " +
                "SET APPRECIATION = ?, DATE_EVAL = ?, ADRESSE_IP = ?, FK_REST = ? " +
                "WHERE NUMERO = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, object.getLikeRestaurant() ? "T" : "F");
            s.setDate(2, new java.sql.Date(object.getVisitDate().getTime()));
            s.setString(3, object.getIpAddress());
            s.setInt(4, object.getRestaurant().getId());
            s.setInt(5, object.getId());

            s.executeUpdate();
            c.commit();

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }




    @Override
    public boolean delete(BasicEvaluation object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "DELETE FROM LIKES WHERE NUMERO = ?";

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
        BasicEvaluation eval = findById(id);
        return this.delete(eval);
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
