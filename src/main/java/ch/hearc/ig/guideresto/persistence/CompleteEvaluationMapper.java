package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.Restaurant;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

public class CompleteEvaluationMapper extends AbstractMapper<CompleteEvaluation> {
    @Override
    public CompleteEvaluation findById(int id) {
        return null;
    }

    @Override
    public Set<CompleteEvaluation> findAll() {
        return Set.of();
    }

    @Override
    public CompleteEvaluation create(CompleteEvaluation object) {
        Connection c = ConnectionUtils.getConnection();
        try (
                PreparedStatement s = c.prepareStatement("INSERT INTO COMMENTAIRES (DATE_EVAL,COMMENTAIRE,NOM_UTILISATEUR, FK_REST) VALUES (? ,?, ?, ?)")
        ) {


            s.setDate(1, new java.sql.Date(object.getVisitDate().getTime()));
            s.setString(2, object.getComment());
            s.setString(3,object.getUsername());
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
    public boolean update(CompleteEvaluation object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "UPDATE COMMENTAIRES " +
                "SET DATE_EVAL = ?, COMMENTAIRE = ?, NOM_UTILISATEUR = ?, FK_REST = ? " +
                "WHERE ID = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setDate(1, new java.sql.Date(object.getVisitDate().getTime()));
            s.setString(2, object.getComment());
            s.setString(3, object.getUsername());
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
    public boolean delete(CompleteEvaluation object) {
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
        return "";
    }

    @Override
    protected String getCountQuery() {
        return "";
    }
}
