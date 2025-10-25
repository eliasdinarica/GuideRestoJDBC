package ch.hearc.ig.guideresto.presentation;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Application principale de GuideResto.
 * Sert de couche présentation (console) et interagit exclusivement avec les services.
 *
 * @author
 *  cedric.baudet
 *  alain.matile
 */
public class Application {

    private static Scanner scanner;
    private static final Logger logger = LogManager.getLogger(Application.class);

    // 🔹 Services
    private static final RestaurantService restaurantService = new RestaurantService();
    private static final CityService cityService = new CityService();
    private static final RestaurantTypeService restaurantTypeService = new RestaurantTypeService();
    private static final BasicEvaluationService basicEvaluationService = new BasicEvaluationService();
    private static final CompleteEvaluationService completeEvaluationService = new CompleteEvaluationService();
    private static final GradeService gradeService = new GradeService();
    private static final EvaluationCriteriaService evaluationCriteriaService = new EvaluationCriteriaService();

    public static void main(String[] args) {
        scanner = new Scanner(System.in);

        System.out.println("Bienvenue dans GuideResto ! Que souhaitez-vous faire ?");
        int choice;
        do {
            printMainMenu();
            choice = readInt();
            proceedMainMenu(choice);
        } while (choice != 0);
    }

    /**
     * Affichage du menu principal.
     */
    private static void printMainMenu() {
        System.out.println("======================================================");
        System.out.println("Que voulez-vous faire ?");
        System.out.println("1. Afficher la liste de tous les restaurants");
        System.out.println("2. Rechercher un restaurant par son nom");
        System.out.println("3. Rechercher un restaurant par ville");
        System.out.println("4. Rechercher un restaurant par son type de cuisine");
        System.out.println("5. Saisir un nouveau restaurant");
        System.out.println("0. Quitter l'application");
    }

    /**
     * Gère le choix de l'utilisateur dans le menu principal.
     */
    private static void proceedMainMenu(int choice) {
        switch (choice) {
            case 1 -> showRestaurantsList();
            case 2 -> searchRestaurantByName();
            case 3 -> searchRestaurantByCity();
            case 4 -> searchRestaurantByType();
            case 5 -> addNewRestaurant();
            case 0 -> System.out.println("Au revoir !");
            default -> System.out.println("Erreur : saisie incorrecte. Veuillez réessayer");
        }
    }

    /**
     * Permet à l'utilisateur de sélectionner un restaurant dans une liste.
     */
    private static Restaurant pickRestaurant(Set<Restaurant> restaurants) {
        if (restaurants.isEmpty()) {
            System.out.println("Aucun restaurant n'a été trouvé !");
            return null;
        }

        for (Restaurant currentRest : restaurants) {
            System.out.println("\"" + currentRest.getName() + "\" - "
                    + currentRest.getAddress().getStreet() + " - "
                    + currentRest.getAddress().getCity().getZipCode() + " "
                    + currentRest.getAddress().getCity().getCityName());
        }

        System.out.println("Veuillez saisir le nom exact du restaurant dont vous voulez voir le détail, ou appuyez sur Enter pour revenir en arrière");
        String choice = readString();
        return searchRestaurantByName(restaurants, choice);
    }

    /**
     * Affiche la liste complète des restaurants.
     */
    private static void showRestaurantsList() {
        System.out.println("Liste des restaurants : ");
        Restaurant restaurant = pickRestaurant(restaurantService.findAll());
        if (restaurant != null) {
            showRestaurant(restaurant);
        }
    }

    /**
     * Recherche un restaurant par son nom.
     */
    private static void searchRestaurantByName() {
        System.out.println("Veuillez entrer une partie du nom recherché : ");
        String research = readString();

        Set<Restaurant> fullList = restaurantService.findAll();
        Set<Restaurant> filteredList = new LinkedHashSet<>();

        for (Restaurant currentRestaurant : fullList) {
            if (currentRestaurant.getName().toUpperCase().contains(research.toUpperCase())) {
                filteredList.add(currentRestaurant);
            }
        }

        Restaurant restaurant = pickRestaurant(filteredList);
        if (restaurant != null) {
            showRestaurant(restaurant);
        }
    }

    /**
     * Recherche un restaurant par le nom de sa ville.
     */
    private static void searchRestaurantByCity() {
        System.out.println("Veuillez entrer une partie du nom de la ville désirée : ");
        String research = readString();

        Set<Restaurant> fullList = restaurantService.findAll();
        Set<Restaurant> filteredList = new LinkedHashSet<>();

        for (Restaurant currentRestaurant : fullList) {
            if (currentRestaurant.getAddress().getCity().getCityName().toUpperCase().contains(research.toUpperCase())) {
                filteredList.add(currentRestaurant);
            }
        }

        Restaurant restaurant = pickRestaurant(filteredList);
        if (restaurant != null) {
            showRestaurant(restaurant);
        }
    }

    /**
     * Permet de sélectionner ou créer une ville.
     */
    private static City pickCity(Set<City> cities) {
        System.out.println("Voici la liste des villes possibles, veuillez entrer le NPA de la ville désirée : ");
        for (City currentCity : cities) {
            System.out.println(currentCity.getZipCode() + " " + currentCity.getCityName());
        }
        System.out.println("Entrez \"NEW\" pour créer une nouvelle ville");
        String choice = readString();

        if (choice.equals("NEW")) {
            City city = new City();
            System.out.println("Veuillez entrer le NPA de la nouvelle ville : ");
            city.setZipCode(readString());
            System.out.println("Veuillez entrer le nom de la nouvelle ville : ");
            city.setCityName(readString());
            City created = cityService.createCity(city);
            if (created == null) {
                System.out.println("❌ Erreur : la création de la ville a échoué.");
                return null;
            }
            return created;
        }

        return searchCityByZipCode(cities, choice);
    }

    /**
     * Permet de sélectionner un type de restaurant.
     */
    private static RestaurantType pickRestaurantType(Set<RestaurantType> types) {
        System.out.println("Voici la liste des types possibles, veuillez entrer le libellé exact du type désiré : ");
        for (RestaurantType currentType : types) {
            System.out.println("\"" + currentType.getLabel() + "\" : " + currentType.getDescription());
        }
        String choice = readString();

        return searchTypeByLabel(types, choice);
    }

    /**
     * Recherche de restaurant par type de cuisine.
     */
    private static void searchRestaurantByType() {
        Set<Restaurant> fullList = restaurantService.findAll();
        Set<Restaurant> filteredList = new LinkedHashSet<>();

        RestaurantType chosenType = pickRestaurantType(restaurantTypeService.findAll());
        if (chosenType != null) {
            for (Restaurant currentRestaurant : fullList) {
                if (currentRestaurant.getType().getId() == chosenType.getId()) {
                    filteredList.add(currentRestaurant);
                }
            }
        }

        Restaurant restaurant = pickRestaurant(filteredList);
        if (restaurant != null) {
            showRestaurant(restaurant);
        }
    }

    /**
     * Création d’un nouveau restaurant.
     */
    private static void addNewRestaurant() {
        System.out.println("Vous allez ajouter un nouveau restaurant !");
        System.out.println("Quel est son nom ?");
        String name = readString();
        System.out.println("Veuillez entrer une courte description : ");
        String description = readString();
        System.out.println("Veuillez entrer l'adresse de son site internet : ");
        String website = readString();
        System.out.println("Rue : ");
        String street = readString();

        City city;
        do {
            city = pickCity(cityService.findAllCities());
        } while (city == null);

        RestaurantType restaurantType;
        do {
            restaurantType = pickRestaurantType(restaurantTypeService.findAll());
        } while (restaurantType == null);

        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setDescription(description);
        restaurant.setWebsite(website);
        restaurant.setAddress(new Localisation(street, city));
        restaurant.setType(restaurantType);

        Restaurant created = restaurantService.createRestaurant(restaurant);
        if (created == null) {
            System.out.println("❌ Erreur : impossible de créer le restaurant.");
            return;
        }

        showRestaurant(created);
    }

    /**
     * Affiche le détail d’un restaurant et ses évaluations.
     */
    private static void showRestaurant(Restaurant restaurant) {
        restaurantService.loadEvaluations(restaurant);

        System.out.println("Affichage d'un restaurant : ");
        StringBuilder sb = new StringBuilder();
        sb.append(restaurant.getName()).append("\n");
        sb.append(restaurant.getDescription()).append("\n");
        sb.append(restaurant.getType().getLabel()).append("\n");
        sb.append(restaurant.getWebsite()).append("\n");
        sb.append(restaurant.getAddress().getStreet()).append(", ");
        sb.append(restaurant.getAddress().getCity().getZipCode()).append(" ").append(restaurant.getAddress().getCity().getCityName()).append("\n");
        sb.append("Nombre de likes : ").append(countLikes(restaurant.getEvaluations(), true)).append("\n");
        sb.append("Nombre de dislikes : ").append(countLikes(restaurant.getEvaluations(), false)).append("\n");
        sb.append("\nEvaluations reçues : ").append("\n");

        String text;
        for (Evaluation currentEval : restaurant.getEvaluations()) {
            text = getCompleteEvaluationDescription(currentEval);
            if (text != null) {
                sb.append(text).append("\n");
            }
        }

        System.out.println(sb);

        int choice;
        do {
            showRestaurantMenu();
            choice = readInt();
            proceedRestaurantMenu(choice, restaurant);
        } while (choice != 0 && choice != 6);
    }

    /**
     * Compte le nombre de likes/dislikes pour un restaurant.
     */
    private static int countLikes(Set<Evaluation> evaluations, Boolean likeRestaurant) {
        int count = 0;
        for (Evaluation currentEval : evaluations) {
            if (currentEval instanceof BasicEvaluation && ((BasicEvaluation) currentEval).getLikeRestaurant() == likeRestaurant) {
                count++;
            }
        }
        return count;
    }

    /**
     * Affiche une évaluation complète (commentaire + notes).
     */
    private static String getCompleteEvaluationDescription(Evaluation eval) {
        StringBuilder result = new StringBuilder();

        if (eval instanceof CompleteEvaluation ce) {
            completeEvaluationService.loadGrades(ce);

            result.append("Evaluation de : ").append(ce.getUsername()).append("\n");
            result.append("Commentaire : ").append(ce.getComment()).append("\n");

            for (Grade currentGrade : ce.getGrades()) {
                gradeService.loadCriteria(currentGrade);
                result.append(currentGrade.getCriteria().getName())
                        .append(" : ").append(currentGrade.getGrade()).append("/5").append("\n");
            }
        }

        return result.toString();
    }

    /**
     * Affiche les actions disponibles sur un restaurant.
     */
    private static void showRestaurantMenu() {
        System.out.println("======================================================");
        System.out.println("Que souhaitez-vous faire ?");
        System.out.println("1. J'aime ce restaurant !");
        System.out.println("2. Je n'aime pas ce restaurant !");
        System.out.println("3. Faire une évaluation complète de ce restaurant !");
        System.out.println("4. Editer ce restaurant");
        System.out.println("5. Editer l'adresse du restaurant");
        System.out.println("6. Supprimer ce restaurant");
        System.out.println("0. Revenir au menu principal");
    }

    /**
     * Gère le choix d'une action sur un restaurant.
     */
    private static void proceedRestaurantMenu(int choice, Restaurant restaurant) {
        switch (choice) {
            case 1 -> addBasicEvaluation(restaurant, true);
            case 2 -> addBasicEvaluation(restaurant, false);
            case 3 -> evaluateRestaurant(restaurant);
            case 4 -> editRestaurant(restaurant);
            case 5 -> editRestaurantAddress(restaurant);
            case 6 -> deleteRestaurant(restaurant);
            case 0 -> {}
        }
    }

    /**
     * Ajoute un like ou dislike à un restaurant.
     */
    private static void addBasicEvaluation(Restaurant restaurant, Boolean like) {
        String ipAddress;
        try {
            ipAddress = Inet4Address.getLocalHost().toString();
        } catch (UnknownHostException ex) {
            logger.error("Error - Couldn't retrieve host IP address");
            ipAddress = "Indisponible";
        }
        BasicEvaluation eval = basicEvaluationService.createBasicEvaluation(restaurant, like, ipAddress);
        if (eval == null) {
            System.out.println("❌ Erreur : le vote n'a pas pu être enregistré.");
        } else {
            System.out.println("Votre vote a été pris en compte !");
        }
    }

    /**
     * Permet à l'utilisateur d'évaluer complètement un restaurant.
     */
    private static void evaluateRestaurant(Restaurant restaurant) {
        System.out.println("Merci d'évaluer ce restaurant !");
        System.out.println("Quel est votre nom d'utilisateur ? ");
        String username = readString();
        System.out.println("Quel commentaire aimeriez-vous publier ?");
        String comment = readString();

        CompleteEvaluation eval = completeEvaluationService.createCompleteEvaluation(restaurant, comment, username);
        if (eval == null) {
            System.out.println("❌ Erreur : impossible de créer l’évaluation.");
            return;
        }

        System.out.println("Veuillez svp donner une note entre 1 et 5 pour chacun de ces critères : ");
        for (EvaluationCriteria currentCriteria : evaluationCriteriaService.findAll()) {
            System.out.println(currentCriteria.getName() + " : " + currentCriteria.getDescription());
            Integer note = readInt();
            completeEvaluationService.addGradeToEvaluation(eval, currentCriteria, note);
        }

        System.out.println("Votre évaluation a bien été enregistrée, merci !");
    }

    /**
     * Permet d'éditer un restaurant.
     */
    private static void editRestaurant(Restaurant restaurant) {
        System.out.println("Edition d'un restaurant !");
        System.out.println("Nouveau nom : ");
        restaurant.setName(readString());
        System.out.println("Nouvelle description : ");
        restaurant.setDescription(readString());
        System.out.println("Nouveau site web : ");
        restaurant.setWebsite(readString());
        System.out.println("Nouveau type de restaurant : ");

        RestaurantType newType = pickRestaurantType(restaurantTypeService.findAll());
        if (newType != null && newType != restaurant.getType()) {
            restaurant.setType(newType);
        }

        if (!restaurantService.updateRestaurant(restaurant)) {
            System.out.println("❌ Erreur : la mise à jour du restaurant a échoué.");
        } else {
            System.out.println("Merci, le restaurant a bien été modifié !");
        }
    }

    /**
     * Permet d'éditer l'adresse d'un restaurant.
     */
    private static void editRestaurantAddress(Restaurant restaurant) {
        System.out.println("Edition de l'adresse d'un restaurant !");
        System.out.println("Nouvelle rue : ");
        String newStreet = readString();

        City newCity = pickCity(cityService.findAllCities());
        if (newCity != null) {
            restaurant.getAddress().setStreet(newStreet);
            restaurant.getAddress().setCity(newCity);
        }

        if (!restaurantService.updateRestaurant(restaurant)) {
            System.out.println("❌ Erreur : la mise à jour de l'adresse a échoué.");
        } else {
            System.out.println("L'adresse a bien été modifiée ! Merci !");
        }
    }

    /**
     * Supprime un restaurant après confirmation.
     */
    private static void deleteRestaurant(Restaurant restaurant) {
        System.out.println("Etes-vous sûr de vouloir supprimer ce restaurant ? (O/n)");
        String choice = readString();
        if (choice.equalsIgnoreCase("O")) {
            if (!restaurantService.deleteRestaurant(restaurant)) {
                System.out.println("❌ Erreur : la suppression du restaurant a échoué.");
            } else {
                System.out.println("Le restaurant a bien été supprimé !");
            }
        }
    }

    // ---------- Méthodes utilitaires ----------

    private static Restaurant searchRestaurantByName(Set<Restaurant> restaurants, String name) {
        for (Restaurant current : restaurants) {
            if (current.getName().equalsIgnoreCase(name)) {
                return current;
            }
        }
        return null;
    }

    private static City searchCityByZipCode(Set<City> cities, String zipCode) {
        for (City current : cities) {
            if (current.getZipCode().equalsIgnoreCase(zipCode)) {
                return current;
            }
        }
        return null;
    }

    private static RestaurantType searchTypeByLabel(Set<RestaurantType> types, String label) {
        for (RestaurantType current : types) {
            if (current.getLabel().equalsIgnoreCase(label)) {
                return current;
            }
        }
        return null;
    }

    private static int readInt() {
        int i = 0;
        boolean success = false;
        do {
            try {
                i = scanner.nextInt();
                success = true;
            } catch (InputMismatchException e) {
                System.out.println("Erreur ! Veuillez entrer un nombre entier s'il vous plaît !");
            } finally {
                scanner.nextLine();
            }
        } while (!success);
        return i;
    }

    private static String readString() {
        return scanner.nextLine();
    }
}
