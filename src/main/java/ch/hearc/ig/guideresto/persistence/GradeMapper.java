package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import ch.hearc.ig.guideresto.business.Grade;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class GradeMapper extends AbstractMapper<Grade> {
    @Override
    public Grade findById(int id) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "SELECT * FROM NOTES WHERE ID = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, id);

            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                Grade grade = new Grade();
                grade.setId(rs.getInt("ID"));
                grade.setGrade(rs.getInt("NOTE"));

                CompleteEvaluation evaluation = new CompleteEvaluation();
                evaluation.setId(rs.getInt("FK_COMM"));
                grade.setEvaluation(evaluation);

                EvaluationCriteria criteria = new EvaluationCriteria();
                criteria.setId(rs.getInt("FK_CRIT"));
                grade.setCriteria(criteria);

                return grade;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Override
    public Set<Grade> findAll() {
        Connection c = ConnectionUtils.getConnection();
        String sql = "SELECT * FROM NOTES";
        Set<Grade> grades = new HashSet<>();

        try (PreparedStatement s = c.prepareStatement(sql)) {
            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                Grade grade = new Grade();
                grade.setId(rs.getInt("ID"));
                grade.setGrade(rs.getInt("NOTE"));

                CompleteEvaluation evaluation = new CompleteEvaluation();
                evaluation.setId(rs.getInt("FK_COMM"));
                grade.setEvaluation(evaluation);

                EvaluationCriteria criteria = new EvaluationCriteria();
                criteria.setId(rs.getInt("FK_CRIT"));
                grade.setCriteria(criteria);

                grades.add(grade);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return grades;
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
