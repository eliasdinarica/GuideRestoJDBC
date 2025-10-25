package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.RestaurantType;
import ch.hearc.ig.guideresto.persistence.RestaurantTypeMapper;

import java.util.Set;

/**
 * Service pour la gestion des types de restaurants.
 * Sert d’intermédiaire entre la couche présentation et la persistance.
 * Aucune relation complexe : les transactions simples sont gérées directement par le mapper.
 */
public class RestaurantTypeService {

    private final RestaurantTypeMapper restaurantTypeMapper = new RestaurantTypeMapper();

    /**
     * Crée un nouveau type de restaurant et le persiste dans la base de données.
     *
     * @param label       le libellé du type (ex. "Italien", "Japonais")
     * @param description la description du type
     * @return le type de restaurant créé et persisté
     */
    public RestaurantType createRestaurantType(String label, String description) {
        RestaurantType type = new RestaurantType();
        type.setLabel(label);
        type.setDescription(description);
        return restaurantTypeMapper.create(type);
    }

    /**
     * Recherche un type de restaurant par son identifiant.
     *
     * @param id identifiant du type
     * @return le type trouvé ou null si absent
     */
    public RestaurantType findById(int id) {
        return restaurantTypeMapper.findById(id);
    }

    /**
     * Récupère tous les types de restaurants disponibles.
     *
     * @return un ensemble de types de restaurants
     */
    public Set<RestaurantType> findAll() {
        return restaurantTypeMapper.findAll();
    }

    /**
     * Met à jour un type de restaurant existant.
     *
     * @param type le type à mettre à jour
     * @return true si la mise à jour a réussi
     */
    public boolean updateRestaurantType(RestaurantType type) {
        return restaurantTypeMapper.update(type);
    }

    /**
     * Supprime un type de restaurant.
     *
     * @param type le type à supprimer
     * @return true si la suppression a réussi
     */
    public boolean deleteRestaurantType(RestaurantType type) {
        return restaurantTypeMapper.delete(type);
    }

    /**
     * Supprime un type de restaurant à partir de son identifiant.
     *
     * @param id identifiant du type
     * @return true si la suppression a réussi
     */
    public boolean deleteById(int id) {
        return restaurantTypeMapper.deleteById(id);
    }
}
