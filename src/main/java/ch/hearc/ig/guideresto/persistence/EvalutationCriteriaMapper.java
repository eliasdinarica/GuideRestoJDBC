package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.EvaluationCriteria;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

public class EvalutationCriteriaMapper extends AbstractMapper<EvaluationCriteria> {
    @Override
    public EvaluationCriteria findById(int id) {
        return null;
    }

    @Override
    public Set<EvaluationCriteria> findAll() {
        return Set.of();
    }

    @Override
    public EvaluationCriteria create(EvaluationCriteria object) {
        Connection c = ConnectionUtils.getConnection();
        try (
                PreparedStatement s = c.prepareStatement(
                        "INSERT INTO CRITERES_EVALUATION (NOM, DESCRIPTION) VALUES (?, ?)"
                )
        ) {
            s.setString(1, object.getName());
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
    public boolean update(EvaluationCriteria object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "UPDATE CRITERES_EVALUATION " +
                "SET NOM = ?, DESCRIPTION = ? " +
                "WHERE NUMERO = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, object.getName());
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
    public boolean delete(EvaluationCriteria object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "DELETE FROM CRITERES_EVALUATION WHERE NUMERO = ?";

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
        Connection c = ConnectionUtils.getConnection();
        String sql = "DELETE FROM CRITERES_EVALUATION WHERE NUMERO = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, id);

            s.executeUpdate();
            c.commit();

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
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
