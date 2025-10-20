package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.City;
import ch.hearc.ig.guideresto.persistence.CityMapper;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Service pour la gestion des villes.
 * Fait le pont entre la couche présentation (Application)
 * et la couche persistance (CityMapper).
 */
public class CityService {

    private final CityMapper cityMapper = new CityMapper();

    /**
     * Crée une nouvelle ville et la persiste dans la base de données.
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
     * Variante : crée une ville déjà construite (plus générique).
     *
     * @param city l'objet City à persister
     * @return la ville persistée
     */
    public City createCity(City city) {
        return cityMapper.create(city);
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
     * @return la ville trouvée, ou null si absente
     */
    public City findCityById(int id) {
        return cityMapper.findById(id);
    }

    /**
     * Recherche une ville par son code postal exact.
     *
     * @param zipCode code postal recherché
     * @return la ville trouvée, ou null si absente
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
     * Supprime une ville.
     *
     * @param city la ville à supprimer
     * @return true si la suppression a réussi
     */
    public boolean deleteCity(City city) {
        return cityMapper.delete(city);
    }

    /**
     * Supprime une ville à partir de son ID.
     *
     * @param id identifiant de la ville
     * @return true si la suppression a réussi
     */
    public boolean deleteCityById(int id) {
        return cityMapper.deleteById(id);
    }
}
