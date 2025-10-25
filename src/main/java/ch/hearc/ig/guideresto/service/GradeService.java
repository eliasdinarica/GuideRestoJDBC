package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import ch.hearc.ig.guideresto.business.Grade;
import ch.hearc.ig.guideresto.persistence.GradeMapper;

import java.util.Set;

/**
 * Service pour la gestion des notes (grades).
 * Implémente un lazy loading du critère et de l’évaluation associés.
 */
public class GradeService {

    private final GradeMapper gradeMapper = new GradeMapper();
    private CompleteEvaluationService evaluationService; // ⚙️ plus de final -> lazy
    private final EvaluationCriteriaService criteriaService = new EvaluationCriteriaService();

    // Setter pour injection croisée
    public void setEvaluationService(CompleteEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    // Lazy getter pour éviter le StackOverflowError
    private CompleteEvaluationService getEvaluationService() {
        if (this.evaluationService == null) {
            this.evaluationService = new CompleteEvaluationService();
            this.evaluationService.setGradeService(this);
        }
        return this.evaluationService;
    }

    public Grade createGrade(int value, CompleteEvaluation evaluation, EvaluationCriteria criteria) {
        Grade grade = new Grade();
        grade.setGrade(value);
        grade.setEvaluation(evaluation);
        grade.setCriteria(criteria);
        return gradeMapper.create(grade);
    }

    public Set<Grade> findAll() {
        return gradeMapper.findAll();
    }

    public Grade findById(int id) {
        return gradeMapper.findById(id);
    }

    public Set<Grade> findByEvaluation(CompleteEvaluation evaluation) {
        return gradeMapper.findByEvaluation(evaluation);
    }

    public Set<Grade> findByCriteria(EvaluationCriteria criteria) {
        return gradeMapper.findByCriteria(criteria);
    }

    public boolean updateGrade(Grade grade) {
        return gradeMapper.update(grade);
    }

    public boolean deleteGrade(Grade grade) {
        return gradeMapper.delete(grade);
    }

    public boolean deleteById(int id) {
        return gradeMapper.deleteById(id);
    }

    /**
     * Lazy loading de l’évaluation complète associée.
     */
    public CompleteEvaluation loadEvaluation(Grade grade) {
        if (grade.getEvaluation() == null || grade.getEvaluation().getUsername() == null) {
            CompleteEvaluation eval = getEvaluationService().findById(grade.getEvaluation().getId());
            grade.setEvaluation(eval);
        }
        return grade.getEvaluation();
    }

    /**
     * Lazy loading du critère associé.
     */
    public EvaluationCriteria loadCriteria(Grade grade) {
        if (grade.getCriteria() == null || grade.getCriteria().getName() == null) {
            EvaluationCriteria criteria = criteriaService.findById(grade.getCriteria().getId());
            grade.setCriteria(criteria);
        }
        return grade.getCriteria();
    }
}
