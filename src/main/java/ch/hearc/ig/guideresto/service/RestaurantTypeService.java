package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.RestaurantType;
import ch.hearc.ig.guideresto.persistence.RestaurantTypeMapper;

import java.util.Set;

/**
 * Service pour la gestion des types de restaurants.
 * Sert d'intermédiaire entre la couche présentation et la couche persistance.
 * Les transactions sont gérées au niveau du mapper.
 */
public class RestaurantTypeService {

    private final RestaurantTypeMapper restaurantTypeMapper = new RestaurantTypeMapper();

    /**
     * Crée un nouveau type de restaurant.
     */
    public RestaurantType createRestaurantType(String label, String description) {
        RestaurantType type = new RestaurantType();
        type.setLabel(label);
        type.setDescription(description);
        return restaurantTypeMapper.create(type);
    }

    /**
     * Récupère un type de restaurant par son identifiant.
     */
    public RestaurantType findById(int id) {
        return restaurantTypeMapper.findById(id);
    }

    /**
     * Récupère tous les types de restaurants disponibles.
     */
    public Set<RestaurantType> findAll() {
        return restaurantTypeMapper.findAll();
    }

    /**
     * Met à jour un type de restaurant.
     */
    public boolean updateRestaurantType(RestaurantType type) {
        return restaurantTypeMapper.update(type);
    }

    /**
     * Supprime un type de restaurant.
     */
    public boolean deleteRestaurantType(RestaurantType type) {
        return restaurantTypeMapper.delete(type);
    }

    /**
     * Supprime un type de restaurant par son identifiant.
     */
    public boolean deleteById(int id) {
        return restaurantTypeMapper.deleteById(id);
    }
}
