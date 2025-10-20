package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.business.BasicEvaluation;
import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.persistence.RestaurantMapper;
import ch.hearc.ig.guideresto.persistence.BasicEvaluationMapper;
import ch.hearc.ig.guideresto.persistence.CompleteEvaluationMapper;

import java.util.Set;

/**
 * Service pour la gestion des restaurants.
 * Sert d'intermédiaire entre la présentation et la persistance.
 * Gère uniquement la suppression des évaluations associées au restaurant,
 * mais délègue la cascade interne aux services spécialisés.
 */
public class RestaurantService {

    private final RestaurantMapper restaurantMapper = new RestaurantMapper();
    private final BasicEvaluationMapper basicEvaluationMapper = new BasicEvaluationMapper();
    private final CompleteEvaluationMapper completeEvaluationMapper = new CompleteEvaluationMapper();
    private final CompleteEvaluationService completeEvaluationService = new CompleteEvaluationService();

    /**
     * Crée un nouveau restaurant.
     */
    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantMapper.create(restaurant);
    }

    /**
     * Récupère tous les restaurants.
     */
    public Set<Restaurant> findAll() {
        return restaurantMapper.findAll();
    }

    /**
     * Récupère un restaurant par son identifiant.
     */
    public Restaurant findById(int id) {
        return restaurantMapper.findById(id);
    }

    /**
     * Met à jour un restaurant.
     */
    public boolean updateRestaurant(Restaurant restaurant) {
        return restaurantMapper.update(restaurant);
    }

    /**
     * Supprime un restaurant et toutes ses évaluations associées.
     * ⚠️ Ne supprime pas les notes — elles sont supprimées par CompleteEvaluationService.
     */
    public boolean deleteRestaurant(Restaurant restaurant) {
        try {
            // 🔹 Supprimer les évaluations basiques associées
            Set<BasicEvaluation> basicEvaluations = basicEvaluationMapper.findAll();
            for (BasicEvaluation eval : basicEvaluations) {
                if (eval.getRestaurant() != null && eval.getRestaurant().getId() == restaurant.getId()) {
                    basicEvaluationMapper.delete(eval);
                }
            }

            // 🔹 Supprimer les évaluations complètes associées
            Set<CompleteEvaluation> completeEvaluations = completeEvaluationMapper.findAll();
            for (CompleteEvaluation eval : completeEvaluations) {
                if (eval.getRestaurant() != null && eval.getRestaurant().getId() == restaurant.getId()) {
                    // délégation au service pour gérer les notes liées
                    completeEvaluationService.deleteCompleteEvaluation(eval);
                }
            }

            // 🔹 Supprimer le restaurant lui-même
            return restaurantMapper.delete(restaurant);

        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du restaurant : " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime un restaurant par son identifiant.
     */
    public boolean deleteById(int id) {
        Restaurant restaurant = restaurantMapper.findById(id);
        if (restaurant == null) return false;
        return deleteRestaurant(restaurant);
    }
}
