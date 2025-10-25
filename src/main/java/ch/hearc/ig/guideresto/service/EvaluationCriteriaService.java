package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import ch.hearc.ig.guideresto.persistence.EvaluationCriteriaMapper;

import java.util.Set;

/**
 * Service pour la gestion des critères d’évaluation.
 * Sert d’intermédiaire entre la couche présentation et la couche persistance.
 * Ce service ne gère pas de relation 1..* complexe, donc aucun lazy loading n’est nécessaire ici.
 */
public class EvaluationCriteriaService {

    private final EvaluationCriteriaMapper criteriaMapper = new EvaluationCriteriaMapper();

    /**
     * Crée un nouveau critère d’évaluation et le persiste dans la base de données.
     *
     * @param name        le nom du critère (ex. "Service", "Qualité des plats")
     * @param description la description du critère
     * @return le critère créé et persisté
     */
    public EvaluationCriteria createCriteria(String name, String description) {
        EvaluationCriteria criteria = new EvaluationCriteria();
        criteria.setName(name);
        criteria.setDescription(description);
        return criteriaMapper.create(criteria);
    }

    /**
     * Recherche un critère d’évaluation par son identifiant.
     *
     * @param id identifiant du critère
     * @return le critère trouvé ou null si absent
     */
    public EvaluationCriteria findById(int id) {
        return criteriaMapper.findById(id);
    }

    /**
     * Récupère tous les critères d’évaluation disponibles.
     *
     * @return un ensemble de critères
     */
    public Set<EvaluationCriteria> findAll() {
        return criteriaMapper.findAll();
    }

    /**
     * Met à jour un critère existant dans la base.
     *
     * @param criteria le critère à mettre à jour
     * @return true si la mise à jour a réussi
     */
    public boolean updateCriteria(EvaluationCriteria criteria) {
        return criteriaMapper.update(criteria);
    }

    /**
     * Supprime un critère d’évaluation.
     *
     * @param criteria le critère à supprimer
     * @return true si la suppression a réussi
     */
    public boolean deleteCriteria(EvaluationCriteria criteria) {
        return criteriaMapper.delete(criteria);
    }

    /**
     * Supprime un critère d’évaluation par son identifiant.
     *
     * @param id identifiant du critère
     * @return true si la suppression a réussi
     */
    public boolean deleteById(int id) {
        return criteriaMapper.deleteById(id);
    }
}
