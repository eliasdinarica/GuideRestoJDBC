package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.Grade;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

public class GradeMapper extends AbstractMapper<Grade> {
    @Override
    public Grade findById(int id) {
        return null;
    }

    @Override
    public Set<Grade> findAll() {
        return Set.of();
    }

    @Override
    public Grade create(Grade object) {
        Connection c = ConnectionUtils.getConnection();
        try (
                PreparedStatement s = c.prepareStatement(
                        "INSERT INTO NOTES (NOTE, FK_COMM, FK_CRIT) VALUES (?, ?, ?)"
                )
        ) {
            s.setInt(1, object.getGrade());
            s.setInt(2, object.getEvaluation().getId());
            s.setInt(3, object.getCriteria().getId());

            s.executeUpdate();
            c.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return object;
    }

    @Override
    public boolean update(Grade object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "UPDATE NOTES " +
                "SET NOTE = ?, FK_COMM = ?, FK_CRIT = ? " +
                "WHERE ID = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, object.getGrade());
            s.setInt(2, object.getEvaluation().getId());
            s.setInt(3, object.getCriteria().getId());
            s.setInt(4, object.getId());

            s.executeUpdate();
            c.commit();

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }



    @Override
    public boolean delete(Grade object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "DELETE FROM NOTES WHERE ID = ?";

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
