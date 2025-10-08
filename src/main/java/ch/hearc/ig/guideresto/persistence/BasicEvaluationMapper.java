package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.BasicEvaluation;
import ch.hearc.ig.guideresto.business.Restaurant;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class BasicEvaluationMapper extends AbstractMapper<BasicEvaluation> {

    @Override
    public BasicEvaluation findById(int id) {
        // 🔹 Vérifie dans le cache
        if (!isCacheEmpty() && identityMap.containsKey(id)) {
            logger.debug("BasicEvaluation {} trouvée dans le cache.", id);
            return identityMap.get(id);
        }

        String sql = "SELECT * FROM LIKES WHERE NUMERO = ?";
        Connection c = ConnectionUtils.getConnection();

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, id);

            try (ResultSet rs = s.executeQuery()) {
                if (!rs.next()) return null;

                BasicEvaluation evaluation = new BasicEvaluation();
                evaluation.setId(rs.getInt("NUMERO"));
                evaluation.setLikeRestaurant("T".equalsIgnoreCase(rs.getString("APPRECIATION")));
                evaluation.setVisitDate(rs.getDate("DATE_EVAL"));
                evaluation.setIpAddress(rs.getString("ADRESSE_IP"));

                Restaurant restaurant = new Restaurant();
                restaurant.setId(rs.getInt("FK_REST"));
                evaluation.setRestaurant(restaurant);

                addToCache(evaluation);
                logger.debug("BasicEvaluation {} ajoutée au cache.", id);

                return evaluation;
            }

        } catch (SQLException e) {
            logger.error("SQLException in findById({}): {}", id, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Set<BasicEvaluation> findAll() {
        Set<BasicEvaluation> evaluations = new HashSet<>();

        // 🔹 Retourne le cache s’il est déjà rempli
        if (!isCacheEmpty()) {
            logger.debug("findAll() : données de BasicEvaluation retournées depuis le cache ({} éléments).", identityMap.size());
            return new HashSet<>(identityMap.values());
        }

        String sql = "SELECT * FROM LIKES";
        Connection c = ConnectionUtils.getConnection();

        try (PreparedStatement s = c.prepareStatement(sql);
             ResultSet rs = s.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("NUMERO");

                if (identityMap.containsKey(id)) {
                    evaluations.add(identityMap.get(id));
                    continue;
                }

                BasicEvaluation evaluation = new BasicEvaluation();
                evaluation.setId(id);
                evaluation.setLikeRestaurant("T".equalsIgnoreCase(rs.getString("APPRECIATION")));
                evaluation.setVisitDate(rs.getDate("DATE_EVAL"));
                evaluation.setIpAddress(rs.getString("ADRESSE_IP"));

                Restaurant restaurant = new Restaurant();
                restaurant.setId(rs.getInt("FK_REST"));
                evaluation.setRestaurant(restaurant);

                addToCache(evaluation);
                evaluations.add(evaluation);
            }

            logger.debug("findAll() : {} BasicEvaluations chargées depuis la DB.", evaluations.size());

        } catch (SQLException e) {
            logger.error("SQLException in findAll(): {}", e.getMessage());
        }

        return evaluations;
    }

    @Override
    public BasicEvaluation create(BasicEvaluation object) {
        Connection c = ConnectionUtils.getConnection();

        try {
            // 🔹 Récupère la prochaine valeur de la séquence Oracle
            int nextId = getSequenceValue();
            object.setId(nextId);

            String sql = "INSERT INTO LIKES (NUMERO, APPRECIATION, DATE_EVAL, ADRESSE_IP, FK_REST) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement s = c.prepareStatement(sql)) {
                s.setInt(1, object.getId());
                s.setString(2, object.getLikeRestaurant() ? "T" : "F");
                s.setDate(3, new java.sql.Date(object.getVisitDate().getTime()));
                s.setString(4, object.getIpAddress());
                s.setInt(5, object.getRestaurant().getId());

                s.executeUpdate();
                c.commit();
            }

            addToCache(object);
            logger.debug("BasicEvaluation {} ajoutée au cache après création.", object.getId());
            return object;

        } catch (SQLException e) {
            logger.error("SQLException in create(): {}", e.getMessage());
            return null;
        }
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

            addToCache(object);
            logger.debug("BasicEvaluation {} mise à jour dans le cache.", object.getId());
            return true;

        } catch (SQLException e) {
            logger.error("SQLException in update(): {}", e.getMessage());
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

            removeFromCache(object.getId());
            logger.debug("BasicEvaluation {} supprimée du cache et de la DB.", object.getId());
            return true;

        } catch (SQLException e) {
            logger.error("SQLException in delete(): {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        BasicEvaluation eval = findById(id);
        if (eval == null) return false;
        return delete(eval);
    }


    @Override
    protected String getSequenceQuery() {

        return "SELECT SEQ_EVAL.NEXTVAL FROM DUAL";
    }

    @Override
    protected String getExistsQuery() {
        return "SELECT 1 FROM LIKES WHERE NUMERO = ?";
    }

    @Override
    protected String getCountQuery() {
        return "SELECT COUNT(*) FROM LIKES";
    }
}
