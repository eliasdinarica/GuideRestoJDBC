package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.Evaluation;

import java.util.Set;

public class EvaluationMapper extends AbstractMapper<Evaluation> {
    @Override
    public Evaluation findById(int id) {
        return null;
    }

    @Override
    public Set<Evaluation> findAll() {
        return Set.of();
    }

    @Override
    public Evaluation create(Evaluation object) {
        return null;
    }

    @Override
    public boolean update(Evaluation object) {
        return false;
    }

    @Override
    public boolean delete(Evaluation object) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }

    @Override
    protected String getSequenceQuery() {
        return "";
    }

    @Override
    protected String getExistsQuery() {
        return "";
    }

    @Override
    protected String getCountQuery() {
        return "";
    }
}
