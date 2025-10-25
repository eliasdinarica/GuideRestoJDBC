package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.business.BasicEvaluation;
import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.Evaluation;
import ch.hearc.ig.guideresto.persistence.RestaurantMapper;
import ch.hearc.ig.guideresto.persistence.BasicEvaluationMapper;
import ch.hearc.ig.guideresto.persistence.CompleteEvaluationMapper;

import java.util.HashSet;
import java.util.Set;

/**
 * Service pour la gestion des restaurants.
 * Sert d'intermédiaire entre la présentation et la persistance.
 * Gère uniquement la suppression des évaluations associées au restaurant,
 * mais délègue la cascade interne aux services spécialisés.
 */
public class RestaurantService {

    private final RestaurantMapper restaurantMapper = new RestaurantMapper();

    private final BasicEvaluationService basicEvaluationService = new BasicEvaluationService();

    private final CompleteEvaluationService completeEvaluationService = new CompleteEvaluationService();
    private final CityService cityService = new CityService();

    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantMapper.create(restaurant);
    }

    public Set<Restaurant> findAll() {
        return restaurantMapper.findAll();
    }

    public Restaurant findById(int id) {
        return restaurantMapper.findById(id);
    }

    public boolean updateRestaurant(Restaurant restaurant) {
        return restaurantMapper.update(restaurant);
    }

    public boolean deleteRestaurant(Restaurant restaurant) {
        try {
            Set<BasicEvaluation> basicEvaluations = basicEvaluationService.findByRestaurant(restaurant);
            for (BasicEvaluation eval : basicEvaluations) {
                basicEvaluationService.deleteBasicEvaluation(eval);
            }

            Set<CompleteEvaluation> completeEvaluations = completeEvaluationService.findByRestaurant(restaurant);
            for (CompleteEvaluation eval : completeEvaluations) {
                completeEvaluationService.deleteCompleteEvaluation(eval);
            }

            return restaurantMapper.delete(restaurant);

        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du restaurant : " + e.getMessage());
            return false;
        }
    }

    public boolean deleteById(int id) {
        Restaurant restaurant = restaurantMapper.findById(id);
        if (restaurant == null) return false;
        return deleteRestaurant(restaurant);
    }

    /**
     * Charge les évaluations (basiques et complètes) associées à un restaurant.
     */
    public void loadEvaluations(Restaurant restaurant) {
        if (restaurant == null) return;

        if (restaurant.getEvaluations() == null || restaurant.getEvaluations().isEmpty()) {
            Set<Evaluation> evaluations = new HashSet<>();
            evaluations.addAll(basicEvaluationService.findByRestaurant(restaurant));
            evaluations.addAll(completeEvaluationService.findByRestaurant(restaurant));
            restaurant.setEvaluations(evaluations);
        }


            for (Evaluation e : restaurant.getEvaluations()) {
                if (e instanceof CompleteEvaluation completeEval) {
                    completeEvaluationService.loadGrades(completeEval);
                }
            }
    }



}
