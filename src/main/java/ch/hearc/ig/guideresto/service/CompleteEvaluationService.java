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
 * Service pour la gestion des évaluations complètes (COMMENTAIRES).
 * Sert d'intermédiaire entre la couche présentation et la couche persistance.
 */
public class CompleteEvaluationService {

    private final CompleteEvaluationMapper completeEvaluationMapper = new CompleteEvaluationMapper();
    private final GradeService gradeService = new GradeService();

    /**
     * Crée une nouvelle évaluation complète et la persiste dans la base.
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
     * Retourne toutes les évaluations présentes dans la base.
     */
    public Set<CompleteEvaluation> findAllCompleteEvaluations() {
        return completeEvaluationMapper.findAll();
    }

    /**
     * Supprime une évaluation complète et toutes les notes associées.
     */
    public boolean deleteCompleteEvaluation(CompleteEvaluation evaluation) {
        if (evaluation == null) return false;

        // 🔹 Étape 1 : supprimer les notes associées via GradeService
        Set<Grade> gradesToDelete = gradeService.findByEvaluation(evaluation);
        for (Grade grade : gradesToDelete) {
            gradeService.deleteGrade(grade);
        }

        // 🔹 Étape 2 : supprimer l’évaluation
        return completeEvaluationMapper.delete(evaluation);
    }

    /**
     * Supprime une évaluation complète par son identifiant.
     */
    public boolean deleteCompleteEvaluationById(int id) {
        CompleteEvaluation eval = completeEvaluationMapper.findById(id);
        return deleteCompleteEvaluation(eval);
    }

    /**
     * Retourne toutes les évaluations d’un restaurant.
     */
    public Set<CompleteEvaluation> findByRestaurant(Restaurant restaurant) {
        Set<CompleteEvaluation> results = new HashSet<>();
        for (CompleteEvaluation eval : completeEvaluationMapper.findAll()) {
            if (eval.getRestaurant() != null && eval.getRestaurant().getId() == restaurant.getId()) {
                results.add(eval);
            }
        }
        return results;
    }

    /**
     * Ajoute une note à une évaluation complète existante et la persiste.
     */
    public Grade addGradeToEvaluation(CompleteEvaluation evaluation, EvaluationCriteria criteria, int note) {
        if (evaluation == null || criteria == null) {
            throw new IllegalArgumentException("Evaluation et critère doivent être non nuls.");
        }

        Grade grade = gradeService.createGrade(note, evaluation, criteria);
        evaluation.getGrades().add(grade);
        return grade;
    }
}
