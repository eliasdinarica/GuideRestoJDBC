package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import ch.hearc.ig.guideresto.business.Grade;
import ch.hearc.ig.guideresto.persistence.GradeMapper;

import java.util.Set;

/**
 * Service pour la gestion des notes (grades).
 * Sert de couche intermédiaire entre la présentation et la persistance.
 * Aucune transaction complexe ici : le mapper gère les commits individuellement.
 */
public class GradeService {

    private final GradeMapper gradeMapper = new GradeMapper();

    /**
     * Crée une nouvelle note (grade) associée à une évaluation complète et un critère.
     */
    public Grade createGrade(int value, CompleteEvaluation evaluation, EvaluationCriteria criteria) {
        Grade grade = new Grade();
        grade.setGrade(value);
        grade.setEvaluation(evaluation);
        grade.setCriteria(criteria);
        return gradeMapper.create(grade);
    }

    /**
     * Récupère toutes les notes présentes en base.
     */
    public Set<Grade> findAll() {
        return gradeMapper.findAll();
    }

    /**
     * Récupère une note par son identifiant.
     */
    public Grade findById(int id) {
        return gradeMapper.findById(id);
    }

    /**
     * Récupère toutes les notes associées à une évaluation complète.
     */
    public Set<Grade> findByEvaluation(CompleteEvaluation evaluation) {
        return gradeMapper.findByEvaluation(evaluation);
    }

    /**
     * Met à jour une note existante.
     */
    public boolean updateGrade(Grade grade) {
        return gradeMapper.update(grade);
    }

    /**
     * Supprime une note.
     */
    public boolean deleteGrade(Grade grade) {
        return gradeMapper.delete(grade);
    }

    /**
     * Supprime une note par son identifiant.
     */
    public boolean deleteById(int id) {
        return gradeMapper.deleteById(id);
    }
}
