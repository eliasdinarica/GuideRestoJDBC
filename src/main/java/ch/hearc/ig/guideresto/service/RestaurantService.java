package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.BasicEvaluation;
import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.Evaluation;
import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.persistence.RestaurantMapper;

import java.util.HashSet;
import java.util.Set;

/**
 * Service pour la gestion des restaurants.
 * Sert d’intermédiaire entre la couche présentation et la couche persistance.
 * Gère également la suppression et le chargement des évaluations associées (basiques et complètes).
 */
public class RestaurantService {

    private final RestaurantMapper restaurantMapper = new RestaurantMapper();
    private final BasicEvaluationService basicEvaluationService = new BasicEvaluationService();
    private final CompleteEvaluationService completeEvaluationService = new CompleteEvaluationService();
    private final CityService cityService = new CityService();

    /**
     * Crée un nouveau restaurant et le persiste dans la base de données.
     *
     * @param restaurant le restaurant à créer
     * @return le restaurant persisté
     */
    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantMapper.create(restaurant);
    }

    /**
     * Récupère tous les restaurants enregistrés.
     *
     * @return un ensemble de restaurants
     */
    public Set<Restaurant> findAll() {
        return restaurantMapper.findAll();
    }

    /**
     * Recherche un restaurant par son identifiant.
     *
     * @param id identifiant du restaurant
     * @return le restaurant trouvé ou null si absent
     */
    public Restaurant findById(int id) {
        return restaurantMapper.findById(id);
    }

    /**
     * Met à jour un restaurant existant.
     *
     * @param restaurant le restaurant à mettre à jour
     * @return true si la mise à jour a réussi
     */
    public boolean updateRestaurant(Restaurant restaurant) {
        return restaurantMapper.update(restaurant);
    }

    /**
     * Supprime un restaurant ainsi que toutes ses évaluations associées (basiques et complètes).
     *
     * @param restaurant le restaurant à supprimer
     * @return true si la suppression a réussi
     */
    public boolean deleteRestaurant(Restaurant restaurant) {
        if (restaurant == null) return false;

        try {
            // Supprimer les évaluations basiques liées
            Set<BasicEvaluation> basicEvaluations = basicEvaluationService.findByRestaurant(restaurant);
            for (BasicEvaluation eval : basicEvaluations) {
                basicEvaluationService.deleteBasicEvaluation(eval);
            }

            // Supprimer les évaluations complètes liées
            Set<CompleteEvaluation> completeEvaluations = completeEvaluationService.findByRestaurant(restaurant);
            for (CompleteEvaluation eval : completeEvaluations) {
                completeEvaluationService.deleteCompleteEvaluation(eval);
            }

            // Supprimer enfin le restaurant
            return restaurantMapper.delete(restaurant);

        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du restaurant : " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime un restaurant à partir de son identifiant.
     *
     * @param id identifiant du restaurant
     * @return true si la suppression a réussi
     */
    public boolean deleteById(int id) {
        Restaurant restaurant = restaurantMapper.findById(id);
        if (restaurant == null) return false;
        return deleteRestaurant(restaurant);
    }

    /**
     * Charge les évaluations associées à un restaurant (lazy loading).
     * Récupère à la fois les évaluations basiques et complètes,
     * puis charge les notes (grades) des évaluations complètes.
     *
     * @param restaurant le restaurant dont il faut charger les évaluations
     */
    public void loadEvaluations(Restaurant restaurant) {
        if (restaurant == null) return;

        if (restaurant.getEvaluations() == null || restaurant.getEvaluations().isEmpty()) {
            Set<Evaluation> evaluations = new HashSet<>();
            evaluations.addAll(basicEvaluationService.findByRestaurant(restaurant));
            evaluations.addAll(completeEvaluationService.findByRestaurant(restaurant));
            restaurant.setEvaluations(evaluations);
        }

        // Lazy load des grades pour chaque évaluation complète
        for (Evaluation e : restaurant.getEvaluations()) {
            if (e instanceof CompleteEvaluation completeEval) {
                completeEvaluationService.loadGrades(completeEval);
            }
        }
    }
}
