package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import ch.hearc.ig.guideresto.persistence.EvaluationCriteriaMapper;

import java.util.Set;

/**
 * Service pour la gestion des critères d'évaluation.
 * Sert d'intermédiaire entre la présentation et la persistance.
 * Les transactions simples sont gérées par le mapper.
 */
public class EvaluationCriteriaService {

    private final EvaluationCriteriaMapper criteriaMapper = new EvaluationCriteriaMapper();

    /**
     * Crée un nouveau critère d'évaluation.
     */
    public EvaluationCriteria createCriteria(String name, String description) {
        EvaluationCriteria criteria = new EvaluationCriteria();
        criteria.setName(name);
        criteria.setDescription(description);
        return criteriaMapper.create(criteria);
    }

    /**
     * Récupère un critère par son identifiant.
     */
    public EvaluationCriteria findById(int id) {
        return criteriaMapper.findById(id);
    }

    /**
     * Récupère tous les critères disponibles.
     */
    public Set<EvaluationCriteria> findAll() {
        return criteriaMapper.findAll();
    }

    /**
     * Met à jour un critère d'évaluation.
     */
    public boolean updateCriteria(EvaluationCriteria criteria) {
        return criteriaMapper.update(criteria);
    }

    /**
     * Supprime un critère d'évaluation.
     */
    public boolean deleteCriteria(EvaluationCriteria criteria) {
        return criteriaMapper.delete(criteria);
    }

    /**
     * Supprime un critère d'évaluation par son identifiant.
     */
    public boolean deleteById(int id) {
        return criteriaMapper.deleteById(id);
    }
}
