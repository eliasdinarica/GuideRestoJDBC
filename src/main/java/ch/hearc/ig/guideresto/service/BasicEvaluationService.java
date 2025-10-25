package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.BasicEvaluation;
import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.persistence.BasicEvaluationMapper;

import java.util.Date;
import java.util.Set;

/**
 * Service pour la gestion des évaluations basiques (likes / dislikes).
 * Sert d’intermédiaire entre la couche présentation et la persistance.
 * Aucune relation complexe n’est gérée ici.
 */
public class BasicEvaluationService {

    private final BasicEvaluationMapper basicEvaluationMapper = new BasicEvaluationMapper();

    /**
     * Crée une nouvelle évaluation basique (like ou dislike) pour un restaurant donné.
     *
     * @param restaurant le restaurant concerné
     * @param like       true si l’utilisateur aime le restaurant, false sinon
     * @param ipAddress  l’adresse IP de l’utilisateur
     * @return l’évaluation créée et persistée
     */
    public BasicEvaluation createBasicEvaluation(Restaurant restaurant, boolean like, String ipAddress) {
        BasicEvaluation eval = new BasicEvaluation();
        eval.setVisitDate(new Date());
        eval.setRestaurant(restaurant);
        eval.setLikeRestaurant(like);
        eval.setIpAddress(ipAddress);

        return basicEvaluationMapper.create(eval);
    }

    /**
     * Récupère toutes les évaluations basiques enregistrées.
     *
     * @return un ensemble d’évaluations basiques
     */
    public Set<BasicEvaluation> findAll() {
        return basicEvaluationMapper.findAll();
    }

    /**
     * Récupère toutes les évaluations basiques associées à un restaurant donné.
     *
     * @param restaurant le restaurant concerné
     * @return un ensemble d’évaluations basiques liées à ce restaurant
     */
    public Set<BasicEvaluation> findByRestaurant(Restaurant restaurant) {
        return basicEvaluationMapper.findByRestaurant(restaurant);
    }

    /**
     * Supprime une évaluation basique.
     *
     * @param evaluation l’évaluation à supprimer
     */
    public void deleteBasicEvaluation(BasicEvaluation evaluation) {
        basicEvaluationMapper.delete(evaluation);
    }
}
