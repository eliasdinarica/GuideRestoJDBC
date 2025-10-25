package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import ch.hearc.ig.guideresto.persistence.EvaluationCriteriaMapper;

import java.util.Set;

/**
 * Service pour la gestion des critères d'évaluation.
 * Lazy loading non nécessaire ici sauf si tu veux les notes associées.
 */
public class EvaluationCriteriaService {

    private final EvaluationCriteriaMapper criteriaMapper = new EvaluationCriteriaMapper();

    public EvaluationCriteria createCriteria(String name, String description) {
        EvaluationCriteria criteria = new EvaluationCriteria();
        criteria.setName(name);
        criteria.setDescription(description);
        return criteriaMapper.create(criteria);
    }

    public EvaluationCriteria findById(int id) {
        return criteriaMapper.findById(id);
    }

    public Set<EvaluationCriteria> findAll() {
        return criteriaMapper.findAll();
    }

    public boolean updateCriteria(EvaluationCriteria criteria) {
        return criteriaMapper.update(criteria);
    }

    public boolean deleteCriteria(EvaluationCriteria criteria) {
        return criteriaMapper.delete(criteria);
    }

    public boolean deleteById(int id) {
        return criteriaMapper.deleteById(id);
    }
}
