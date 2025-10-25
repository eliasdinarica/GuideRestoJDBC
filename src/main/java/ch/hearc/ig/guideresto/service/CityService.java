package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.City;
import ch.hearc.ig.guideresto.persistence.CityMapper;

import java.util.Set;

/**
 * Service pour la gestion des villes.
 * Sert d’intermédiaire entre la couche présentation et la couche persistance.
 * Gère la création, la recherche, la mise à jour et la suppression des villes.
 */
public class CityService {

    private final CityMapper cityMapper = new CityMapper();

    /**
     * Crée une nouvelle ville à partir d’un code postal et d’un nom.
     *
     * @param zipCode  le code postal de la ville
     * @param cityName le nom de la ville
     * @return la ville créée et persistée
     */
    public City createCity(String zipCode, String cityName) {
        City city = new City();
        city.setZipCode(zipCode);
        city.setCityName(cityName);
        return cityMapper.create(city);
    }

    /**
     * Crée une ville à partir d’un objet City déjà instancié.
     *
     * @param city la ville à persister
     * @return la ville persistée
     */
    public City createCity(City city) {
        return cityMapper.create(city);
    }

    /**
     * Recharge une ville partiellement chargée depuis la base.
     * Utile pour le lazy loading de la localisation d’un restaurant.
     *
     * @param city la ville potentiellement incomplète
     * @return la ville complétée depuis la base
     */
    public City loadCity(City city) {
        if (city == null) return null;

        if ((city.getCityName() == null || city.getZipCode() == null) && city.getId() != null) {
            City fullCity = cityMapper.findById(city.getId());
            if (fullCity != null) {
                city.setCityName(fullCity.getCityName());
                city.setZipCode(fullCity.getZipCode());
            }
        }
        return city;
    }

    /**
     * Récupère toutes les villes présentes dans la base.
     *
     * @return un ensemble de villes
     */
    public Set<City> findAllCities() {
        return cityMapper.findAll();
    }

    /**
     * Récupère une ville par son identifiant.
     *
     * @param id identifiant de la ville
     * @return la ville trouvée ou null si absente
     */
    public City findCityById(int id) {
        return cityMapper.findById(id);
    }

    /**
     * Recherche une ville par son code postal exact.
     *
     * @param zipCode code postal recherché
     * @return la ville trouvée ou null si absente
     */
    public City findCityByZipCode(String zipCode) {
        for (City city : cityMapper.findAll()) {
            if (city.getZipCode().equalsIgnoreCase(zipCode)) {
                return city;
            }
        }
        return null;
    }

    /**
     * Met à jour une ville existante.
     *
     * @param city la ville à mettre à jour
     * @return true si la mise à jour a réussi
     */
    public boolean updateCity(City city) {
        return cityMapper.update(city);
    }

    /**
     * Supprime une ville existante.
     *
     * @param city la ville à supprimer
     * @return true si la suppression a réussi
     */
    public boolean deleteCity(City city) {
        return city != null && cityMapper.delete(city);
    }

    /**
     * Supprime une ville à partir de son identifiant.
     *
     * @param id identifiant de la ville
     * @return true si la suppression a réussi
     */
    public boolean deleteCityById(int id) {
        return cityMapper.deleteById(id);
    }
}
