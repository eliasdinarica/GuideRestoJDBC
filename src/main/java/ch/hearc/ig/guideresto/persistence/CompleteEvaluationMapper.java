package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.Restaurant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Mapper pour la gestion des évaluations complètes (commentaires).
 * Fait le lien entre la table COMMENTAIRES et les objets {@link CompleteEvaluation}.
 *
 * Utilise une Identity Map pour garantir l’unicité des instances.
 */
public class CompleteEvaluationMapper extends AbstractMapper<CompleteEvaluation> {

    /** Cache local des instances d’évaluations chargées. */
    protected static final Map<Integer, CompleteEvaluation> identityMap = new HashMap<>();

    @Override
    protected Map<Integer, CompleteEvaluation> getIdentityMap() {
        return identityMap;
    }

    /**
     * Recherche une évaluation complète par son identifiant.
     * Si elle est déjà en cache, elle est renvoyée directement.
     *
     * @param id identifiant unique de l’évaluation
     * @return l’évaluation trouvée ou {@code null} si absente
     */
    @Override
    public CompleteEvaluation findById(int id) {
        if (!isCacheEmpty() && identityMap.containsKey(id)) {
            logger.debug("CompleteEvaluation {} trouvée dans le cache.", id);
            return identityMap.get(id);
        }

        String sql = "SELECT * FROM COMMENTAIRES WHERE NUMERO = ?";
        Connection c = ConnectionUtils.getConnection();

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, id);

            try (ResultSet rs = s.executeQuery()) {
                if (!rs.next()) return null;

                CompleteEvaluation evaluation = new CompleteEvaluation();
                evaluation.setId(rs.getInt("NUMERO"));
                evaluation.setVisitDate(rs.getDate("DATE_EVAL"));
                evaluation.setComment(rs.getString("COMMENTAIRE"));
                evaluation.setUsername(rs.getString("NOM_UTILISATEUR"));

                // 🔹 Proxy minimal du restaurant
                Restaurant restaurant = new Restaurant();
                restaurant.setId(rs.getInt("FK_REST"));
                evaluation.setRestaurant(restaurant);

                addToCache(evaluation);
                logger.debug("CompleteEvaluation {} ajoutée au cache.", id);
                return evaluation;
            }

        } catch (SQLException ex) {
            logger.error("SQLException in findById({}): {}", id, ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Récupère toutes les évaluations complètes présentes dans la base.
     *
     * @return un ensemble de toutes les évaluations
     */
    @Override
    public Set<CompleteEvaluation> findAll() {
        Set<CompleteEvaluation> evaluations = new HashSet<>();
        resetCache();

        String sql = "SELECT * FROM COMMENTAIRES";
        Connection c = ConnectionUtils.getConnection();

        try (PreparedStatement s = c.prepareStatement(sql);
             ResultSet rs = s.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("NUMERO");

                // ✅ Réutilisation de l’instance si déjà présente
                CompleteEvaluation evaluation = identityMap.get(id);
                if (evaluation == null) {
                    evaluation = new CompleteEvaluation();
                    evaluation.setId(id);
                    evaluation.setVisitDate(rs.getDate("DATE_EVAL"));
                    evaluation.setComment(rs.getString("COMMENTAIRE"));
                    evaluation.setUsername(rs.getString("NOM_UTILISATEUR"));

                    Restaurant restaurant = new Restaurant();
                    restaurant.setId(rs.getInt("FK_REST"));
                    evaluation.setRestaurant(restaurant);

                    addToCache(evaluation);
                }

                evaluations.add(evaluation);
            }

            logger.debug("findAll() : {} CompleteEvaluations chargées depuis la DB.", evaluations.size());

        } catch (SQLException ex) {
            logger.error("SQLException in findAll(): {}", ex.getMessage(), ex);
        }

        return evaluations;
    }

    /**
     * Crée une nouvelle évaluation complète et la persiste dans la base.
     *
     * @param object l’objet {@link CompleteEvaluation} à insérer
     * @return l’évaluation créée et ajoutée au cache, ou {@code null} en cas d’erreur
     */
    @Override
    public CompleteEvaluation create(CompleteEvaluation object) {
        Connection c = ConnectionUtils.getConnection();
        try {
            int nextId = getSequenceValue();
            object.setId(nextId);

            String sql = """
                    INSERT INTO COMMENTAIRES (NUMERO, DATE_EVAL, COMMENTAIRE, NOM_UTILISATEUR, FK_REST)
                    VALUES (?, ?, ?, ?, ?)
                    """;

            try (PreparedStatement s = c.prepareStatement(sql)) {
                s.setInt(1, object.getId());
                s.setDate(2, new java.sql.Date(object.getVisitDate().getTime()));
                s.setString(3, object.getComment());
                s.setString(4, object.getUsername());
                s.setInt(5, object.getRestaurant().getId());
                s.executeUpdate();
                c.commit();
            }

            addToCache(object);
            logger.debug("CompleteEvaluation {} ajoutée au cache après création.", object.getId());
            return object;

        } catch (SQLException e) {
            logger.error("SQLException in create(): {}", e.getMessage());
            return null;
        }
    }

    /**
     * Met à jour une évaluation complète existante.
     *
     * @param object l’évaluation à mettre à jour
     * @return {@code true} si la mise à jour a réussi
     */
    @Override
    public boolean update(CompleteEvaluation object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = """
                UPDATE COMMENTAIRES
                SET DATE_EVAL = ?, COMMENTAIRE = ?, NOM_UTILISATEUR = ?, FK_REST = ?
                WHERE NUMERO = ?
                """;

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setDate(1, new java.sql.Date(object.getVisitDate().getTime()));
            s.setString(2, object.getComment());
            s.setString(3, object.getUsername());
            s.setInt(4, object.getRestaurant().getId());
            s.setInt(5, object.getId());

            s.executeUpdate();
            c.commit();

            addToCache(object);
            logger.debug("CompleteEvaluation {} mise à jour dans le cache.", object.getId());
            return true;

        } catch (SQLException e) {
            logger.error("SQLException in update(): {}", e.getMessage());
            return false;
        }
    }

    /**
     * Supprime une évaluation complète de la base.
     *
     * @param object l’évaluation à supprimer
     * @return {@code true} si la suppression a réussi
     */
    @Override
    public boolean delete(CompleteEvaluation object) {
        Connection c = ConnectionUtils.getConnection();
        String sql = "DELETE FROM COMMENTAIRES WHERE NUMERO = ?";

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, object.getId());
            s.executeUpdate();
            c.commit();

            removeFromCache(object.getId());
            logger.debug("CompleteEvaluation {} supprimée du cache et de la DB.", object.getId());
            return true;

        } catch (SQLException ex) {
            logger.error("SQLException in delete(): {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Supprime une évaluation complète à partir de son identifiant.
     *
     * @param id identifiant de l’évaluation à supprimer
     * @return {@code true} si la suppression a réussi
     */
    @Override
    public boolean deleteById(int id) {
        CompleteEvaluation eval = findById(id);
        if (eval == null) return false;
        return delete(eval);
    }

    @Override
    protected String getSequenceQuery() {
        return "SELECT SEQ_EVAL.NEXTVAL FROM DUAL";
    }

    @Override
    protected String getExistsQuery() {
        return "SELECT 1 FROM COMMENTAIRES WHERE NUMERO = ?";
    }

    @Override
    protected String getCountQuery() {
        return "SELECT COUNT(*) FROM COMMENTAIRES";
    }

    /**
     * Recherche toutes les évaluations complètes associées à un restaurant.
     *
     * @param restaurant le restaurant concerné
     * @return un ensemble d’évaluations complètes liées à ce restaurant
     */
    public Set<CompleteEvaluation> findByRestaurant(Restaurant restaurant) {
        Set<CompleteEvaluation> evaluations = new HashSet<>();
        String sql = "SELECT * FROM COMMENTAIRES WHERE FK_REST = ?";
        Connection c = ConnectionUtils.getConnection();

        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, restaurant.getId());

            try (ResultSet rs = s.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("NUMERO");

                    CompleteEvaluation evaluation = identityMap.get(id);
                    if (evaluation == null) {
                        evaluation = new CompleteEvaluation();
                        evaluation.setId(id);
                        evaluation.setVisitDate(rs.getDate("DATE_EVAL"));
                        evaluation.setComment(rs.getString("COMMENTAIRE"));
                        evaluation.setUsername(rs.getString("NOM_UTILISATEUR"));
                        evaluation.setRestaurant(restaurant);
                        addToCache(evaluation);
                    }

                    evaluations.add(evaluation);
                }
            }

        } catch (SQLException e) {
            logger.error("SQLException in findByRestaurant(): {}", e.getMessage());
        }

        return evaluations;
    }
}
