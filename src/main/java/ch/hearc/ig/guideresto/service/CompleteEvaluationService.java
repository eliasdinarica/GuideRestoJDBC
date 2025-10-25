package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import ch.hearc.ig.guideresto.business.Grade;
import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.persistence.CompleteEvaluationMapper;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Service pour la gestion des évaluations complètes (commentaires + notes).
 * Sert d’intermédiaire entre la couche présentation et la persistance.
 * Implémente un lazy loading des notes (grades) associées à chaque évaluation.
 */
public class CompleteEvaluationService {

    private final CompleteEvaluationMapper completeEvaluationMapper = new CompleteEvaluationMapper();
    private GradeService gradeService; // ⚙️ Injection paresseuse pour éviter les dépendances circulaires

    /**
     * Injection croisée du GradeService (pour éviter la récursivité infinie).
     *
     * @param gradeService le service des notes
     */
    public void setGradeService(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    /**
     * Accès paresseux au GradeService.
     * Initialise le service si nécessaire (lazy loading).
     */
    private GradeService getGradeService() {
        if (this.gradeService == null) {
            this.gradeService = new GradeService();
            this.gradeService.setEvaluationService(this);
        }
        return this.gradeService;
    }

    /**
     * Crée une nouvelle évaluation complète pour un restaurant donné.
     *
     * @param restaurant le restaurant évalué
     * @param comment    le commentaire associé
     * @param username   le nom de l’utilisateur ayant laissé l’évaluation
     * @return l’évaluation créée et persistée
     */
    public CompleteEvaluation createCompleteEvaluation(Restaurant restaurant, String comment, String username) {
        CompleteEvaluation evaluation = new CompleteEvaluation();
        evaluation.setVisitDate(new Date());
        evaluation.setComment(comment);
        evaluation.setUsername(username);
        evaluation.setRestaurant(restaurant);
        return completeEvaluationMapper.create(evaluation);
    }

    /**
     * Récupère toutes les évaluations complètes enregistrées.
     *
     * @return un ensemble d’évaluations complètes
     */
    public Set<CompleteEvaluation> findAllCompleteEvaluations() {
        return completeEvaluationMapper.findAll();
    }

    /**
     * Recherche une évaluation complète par son identifiant.
     *
     * @param id identifiant de l’évaluation
     * @return l’évaluation trouvée ou null si absente
     */
    public CompleteEvaluation findById(int id) {
        return completeEvaluationMapper.findById(id);
    }

    /**
     * Récupère toutes les évaluations complètes associées à un restaurant.
     *
     * @param restaurant le restaurant concerné
     * @return un ensemble d’évaluations liées à ce restaurant
     */
    public Set<CompleteEvaluation> findByRestaurant(Restaurant restaurant) {
        return completeEvaluationMapper.findByRestaurant(restaurant);
    }

    /**
     * Ajoute une note (grade) à une évaluation complète existante.
     *
     * @param evaluation l’évaluation concernée
     * @param criteria   le critère évalué
     * @param note       la valeur de la note (1 à 5)
     * @return la note créée et liée à l’évaluation
     */
    public Grade addGradeToEvaluation(CompleteEvaluation evaluation, EvaluationCriteria criteria, int note) {
        if (evaluation == null || criteria == null)
            throw new IllegalArgumentException("L’évaluation et le critère doivent être non nuls.");

        Grade grade = getGradeService().createGrade(note, evaluation, criteria);
        evaluation.getGrades().add(grade);
        return grade;
    }

    /**
     * Supprime une évaluation complète ainsi que toutes les notes associées.
     *
     * @param evaluation l’évaluation à supprimer
     * @return true si la suppression a réussi
     */
    public boolean deleteCompleteEvaluation(CompleteEvaluation evaluation) {
        if (evaluation == null) return false;

        // Lazy load des grades avant suppression
        Set<Grade> grades = loadGrades(evaluation);
        for (Grade grade : grades) {
            getGradeService().deleteGrade(grade);
        }

        return completeEvaluationMapper.delete(evaluation);
    }

    /**
     * Supprime une évaluation complète par son identifiant.
     *
     * @param id identifiant de l’évaluation
     * @return true si la suppression a réussi
     */
    public boolean deleteCompleteEvaluationById(int id) {
        CompleteEvaluation eval = completeEvaluationMapper.findById(id);
        return deleteCompleteEvaluation(eval);
    }

    /**
     * Charge les notes (grades) d’une évaluation complète.
     * Implémente un lazy loading : les notes ne sont chargées que si nécessaire.
     *
     * @param evaluation l’évaluation dont il faut charger les notes
     * @return l’ensemble des notes associées
     */
    public Set<Grade> loadGrades(CompleteEvaluation evaluation) {
        if (evaluation == null) return Set.of();

        if (evaluation.getGrades() == null || evaluation.getGrades().isEmpty()) {
            Set<Grade> loaded = getGradeService().findByEvaluation(evaluation);
            evaluation.setGrades(new HashSet<>(loaded));
        }

        return evaluation.getGrades();
    }
}
