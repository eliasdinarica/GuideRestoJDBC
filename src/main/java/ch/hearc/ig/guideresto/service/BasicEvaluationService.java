package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.BasicEvaluation;
import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.persistence.BasicEvaluationMapper;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class BasicEvaluationService {

    private final BasicEvaluationMapper basicEvaluationMapper = new BasicEvaluationMapper();

    public BasicEvaluation createBasicEvaluation(Restaurant restaurant, boolean like, String ipAddress) {
        BasicEvaluation eval = new BasicEvaluation();
        eval.setVisitDate(new Date());
        eval.setRestaurant(restaurant);
        eval.setLikeRestaurant(like);
        eval.setIpAddress(ipAddress);

        // Le mapper gère la connexion et la transaction
        return basicEvaluationMapper.create(eval);
    }

    public Set<BasicEvaluation> findAll() {
        return basicEvaluationMapper.findAll();
    }

    public Set<BasicEvaluation> findByRestaurant(Restaurant restaurant) {
        return basicEvaluationMapper.findByRestaurant(restaurant);
    }

    public void deleteBasicEvaluation(BasicEvaluation evaluation) {
        basicEvaluationMapper.delete(evaluation);
    }
}
