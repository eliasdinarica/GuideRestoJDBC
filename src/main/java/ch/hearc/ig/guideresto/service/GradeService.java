package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import ch.hearc.ig.guideresto.business.Grade;
import ch.hearc.ig.guideresto.persistence.GradeMapper;

import java.util.Set;

/**
 * Service pour la gestion des notes (grades).
 * Sert d’intermédiaire entre la couche présentation et la persistance.
 * Implémente un lazy loading du critère et de l’évaluation associés.
 */
public class GradeService {

    private final GradeMapper gradeMapper = new GradeMapper();
    private CompleteEvaluationService evaluationService; // ⚙️ Injection paresseuse (évite boucle circulaire)
    private final EvaluationCriteriaService criteriaService = new EvaluationCriteriaService();

    /**
     * Injection croisée du service d’évaluations complètes.
     *
     * @param evaluationService le service des évaluations complètes
     */
    public void setEvaluationService(CompleteEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    /**
     * Accès paresseux au service d’évaluations complètes.
     * Initialise le service si nécessaire pour éviter les dépendances circulaires.
     */
    private CompleteEvaluationService getEvaluationService() {
        if (this.evaluationService == null) {
            this.evaluationService = new CompleteEvaluationService();
            this.evaluationService.setGradeService(this);
        }
        return this.evaluationService;
    }

    /**
     * Crée une nouvelle note (grade) associée à une évaluation et un critère donnés.
     *
     * @param value      la valeur de la note (1 à 5)
     * @param evaluation l’évaluation complète associée
     * @param criteria   le critère évalué
     * @return la note créée et persistée
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
     *
     * @return un ensemble de notes
     */
    public Set<Grade> findAll() {
        return gradeMapper.findAll();
    }

    /**
     * Recherche une note par son identifiant.
     *
     * @param id identifiant de la note
     * @return la note trouvée ou null si absente
     */
    public Grade findById(int id) {
        return gradeMapper.findById(id);
    }

    /**
     * Récupère toutes les notes associées à une évaluation complète donnée.
     *
     * @param evaluation l’évaluation concernée
     * @return l’ensemble des notes liées à cette évaluation
     */
    public Set<Grade> findByEvaluation(CompleteEvaluation evaluation) {
        return gradeMapper.findByEvaluation(evaluation);
    }

    /**
     * Récupère toutes les notes associées à un critère donné.
     *
     * @param criteria le critère concerné
     * @return l’ensemble des notes liées à ce critère
     */
    public Set<Grade> findByCriteria(EvaluationCriteria criteria) {
        return gradeMapper.findByCriteria(criteria);
    }

    /**
     * Met à jour une note existante.
     *
     * @param grade la note à mettre à jour
     * @return true si la mise à jour a réussi
     */
    public boolean updateGrade(Grade grade) {
        return gradeMapper.update(grade);
    }

    /**
     * Supprime une note spécifique.
     *
     * @param grade la note à supprimer
     * @return true si la suppression a réussi
     */
    public boolean deleteGrade(Grade grade) {
        return gradeMapper.delete(grade);
    }

    /**
     * Supprime une note à partir de son identifiant.
     *
     * @param id identifiant de la note
     * @return true si la suppression a réussi
     */
    public boolean deleteById(int id) {
        return gradeMapper.deleteById(id);
    }

    /**
     * Lazy loading de l’évaluation complète associée à une note.
     * Recharge l’évaluation uniquement si elle est partiellement chargée.
     *
     * @param grade la note concernée
     * @return l’évaluation complète liée à cette note
     */
    public CompleteEvaluation loadEvaluation(Grade grade) {
        if (grade.getEvaluation() == null || grade.getEvaluation().getUsername() == null) {
            CompleteEvaluation eval = getEvaluationService().findById(grade.getEvaluation().getId());
            grade.setEvaluation(eval);
        }
        return grade.getEvaluation();
    }

    /**
     * Lazy loading du critère associé à une note.
     * Recharge le critère uniquement si ses données sont incomplètes.
     *
     * @param grade la note concernée
     * @return le critère complet lié à cette note
     */
    public EvaluationCriteria loadCriteria(Grade grade) {
        if (grade.getCriteria() == null || grade.getCriteria().getName() == null) {
            EvaluationCriteria criteria = criteriaService.findById(grade.getCriteria().getId());
            grade.setCriteria(criteria);
        }
        return grade.getCriteria();
    }
}
