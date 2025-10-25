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
 * Implémente un lazy loading des notes (grades).
 */
public class CompleteEvaluationService {

    private final CompleteEvaluationMapper completeEvaluationMapper = new CompleteEvaluationMapper();
    private final GradeService gradeService = new GradeService();

    public CompleteEvaluation createCompleteEvaluation(Restaurant restaurant, String comment, String username) {
        CompleteEvaluation evaluation = new CompleteEvaluation();
        evaluation.setVisitDate(new Date());
        evaluation.setComment(comment);
        evaluation.setUsername(username);
        evaluation.setRestaurant(restaurant);
        return completeEvaluationMapper.create(evaluation);
    }

    public Set<CompleteEvaluation> findAllCompleteEvaluations() {
        return completeEvaluationMapper.findAll();
    }

    public boolean deleteCompleteEvaluation(CompleteEvaluation evaluation) {
        if (evaluation == null) return false;

        // Lazy load des grades avant suppression
        Set<Grade> grades = loadGrades(evaluation);
        for (Grade grade : grades) {
            gradeService.deleteGrade(grade);
        }

        return completeEvaluationMapper.delete(evaluation);
    }

    public boolean deleteCompleteEvaluationById(int id) {
        CompleteEvaluation eval = completeEvaluationMapper.findById(id);
        return deleteCompleteEvaluation(eval);
    }

    public Set<CompleteEvaluation> findByRestaurant(Restaurant restaurant) {
        return completeEvaluationMapper.findByRestaurant(restaurant);
    }

    public Grade addGradeToEvaluation(CompleteEvaluation evaluation, EvaluationCriteria criteria, int note) {
        if (evaluation == null || criteria == null)
            throw new IllegalArgumentException("Evaluation et critère doivent être non nuls.");

        Grade grade = gradeService.createGrade(note, evaluation, criteria);
        evaluation.getGrades().add(grade);
        return grade;
    }

    /**
     * Lazy loading des grades : ne charge les notes que si elles ne sont pas encore connues.
     */
    public Set<Grade> loadGrades(CompleteEvaluation evaluation) {
        if (evaluation == null) return Set.of();

        if (evaluation.getGrades() == null || evaluation.getGrades().isEmpty()) {
            Set<Grade> loaded = gradeService.findByEvaluation(evaluation);
            evaluation.setGrades(new HashSet<>(loaded));
        }

        return evaluation.getGrades();
    }
}
