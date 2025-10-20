package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import ch.hearc.ig.guideresto.business.Grade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GradeMapper extends AbstractMapper<Grade> {

    protected static final Map<Integer, Grade> identityMap = new HashMap<>();

    @Override
    protected Map<Integer, Grade> getIdentityMap() {
        return identityMap;
    }

    @Override
    public Grade findById(int id) {
        if (!isCacheEmpty() && identityMap.containsKey(id)) {
            logger.debug("Grade {} trouvé dans le cache.", id);
            return identityMap.get(id);
        }

        String sql = "SELECT * FROM NOTES WHERE NUMERO = ?";
        Connection c = ConnectionUtils.getConnection();

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, id);

            try (ResultSet rs = s.executeQuery()) {
                if (!rs.next()) return null;

                Grade grade = new Grade();
                grade.setId(rs.getInt("NUMERO"));
                grade.setGrade(rs.getInt("NOTE"));

                CompleteEvaluation evaluation = new CompleteEvaluation();
                evaluation.setId(rs.getInt("FK_COMM"));
                grade.setEvaluation(evaluation);

                EvaluationCriteria criteria = new EvaluationCriteria();
                criteria.setId(rs.getInt("FK_CRIT"));
                grade.setCriteria(criteria);

                addToCache(grade);
                logger.debug("Grade {} ajouté au cache.", id);
                return grade;
            }

        } catch (SQLException ex) {
            logger.error("SQLException in findById({}): {}", id, ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public Set<Grade> findAll() {
        Set<Grade> grades = new HashSet<>();

        if (!isCacheEmpty()) {
            logger.debug("findAll() : données retournées depuis le cache ({} éléments).", identityMap.size());
            return new HashSet<>(identityMap.values());
        }

        String sql = "SELECT * FROM NOTES";
        Connection c = ConnectionUtils.getConnection();

        try (PreparedStatement s = c.prepareStatement(sql);
             ResultSet rs = s.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("NUMERO");

                if (identityMap.containsKey(id)) {
                    grades.add(identityMap.get(id));
                    continue;
                }

                Grade grade = new Grade();
                grade.setId(id);
                grade.setGrade(rs.getInt("NOTE"));

                CompleteEvaluation evaluation = new CompleteEvaluation();
                evaluation.setId(rs.getInt("FK_COMM"));
                grade.setEvaluation(evaluation);

                EvaluationCriteria criteria = new EvaluationCriteria();
                criteria.setId(rs.getInt("FK_CRIT"));
                grade.setCriteria(criteria);

                addToCache(grade);
                grades.add(grade);
            }

            logger.debug("findAll() : {} Grades chargés depuis la DB.", grades.size());

        } catch (SQLException ex) {
            logger.error("SQLException in findAll(): {}", ex.getMessage(), ex);
        }

        return grades;
    }

    @Override
    public Grade create(Grade object) {
        Connection c = ConnectionUtils.getConnection();
        try {
            int nextId = getSequenceValue();
            object.setId(nextId);

            String sql = "INSERT INTO NOTES (NUMERO, NOTE, FK_COMM, FK_CRIT) VALUES (?, ?, ?, ?)";

            try (PreparedStatement s = c.prepareStatement(sql)) {
                s.setInt(1, object.getId());
                s.setInt(2, object.getGrade());
                s.setInt(3, object.getEvaluation().getId());
                s.setInt(4, object.getCriteria().getId());

                s.executeUpdate();
                c.commit();
            }

            addToCache(object);
            logger.debug("Grade {} ajouté au cache après création.", object.getId());

            return object;

        } catch (SQLException e) {
            logger.error("SQLException in create(): {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean update(Grade object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "UPDATE NOTES " +
                "SET NOTE = ?, FK_COMM = ?, FK_CRIT = ? " +
                "WHERE NUMERO = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, object.getGrade());
            s.setInt(2, object.getEvaluation().getId());
            s.setInt(3, object.getCriteria().getId());
            s.setInt(4, object.getId());

            s.executeUpdate();
            c.commit();

            addToCache(object);
            logger.debug("Grade {} mis à jour dans le cache.", object.getId());
            return true;

        } catch (SQLException e) {
            logger.error("SQLException in update(): {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Grade object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "DELETE FROM NOTES WHERE NUMERO = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, object.getId());
            s.executeUpdate();
            c.commit();

            removeFromCache(object.getId());
            logger.debug("Grade {} supprimé du cache et de la DB.", object.getId());
            return true;

        } catch (SQLException e) {
            logger.error("SQLException in delete(): {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        Grade grade = findById(id);
        if (grade == null) return false;
        return delete(grade);
    }

    @Override
    protected String getSequenceQuery() {
        return "SELECT SEQ_NOTES.NEXTVAL FROM DUAL";
    }

    @Override
    protected String getExistsQuery() {
        return "SELECT 1 FROM NOTES WHERE NUMERO = ?";
    }

    @Override
    protected String getCountQuery() {
        return "SELECT COUNT(*) FROM NOTES";
    }

    public Set<Grade> findByEvaluation(CompleteEvaluation evaluation) {
        Set<Grade> grades = new HashSet<>();
        String sql = "SELECT * FROM NOTES WHERE FK_COMM = ?";
        Connection c = ConnectionUtils.getConnection();

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, evaluation.getId());
            try (ResultSet rs = s.executeQuery()) {
                while (rs.next()) {
                    Grade grade = new Grade();
                    grade.setId(rs.getInt("NUMERO"));
                    grade.setGrade(rs.getInt("NOTE"));
                    grade.setEvaluation(evaluation);
                    // ⚠ ici tu peux recharger le critère via EvaluationCriteriaMapper si besoin
                    grades.add(grade);
                }
            }
        } catch (SQLException e) {
            logger.error("SQLException in findByEvaluation(): {}", e.getMessage());
        }

        return grades;
    }
}
