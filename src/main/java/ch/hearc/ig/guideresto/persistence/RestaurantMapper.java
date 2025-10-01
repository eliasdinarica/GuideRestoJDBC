package ch.hearc.ig.guideresto.persistence;
import ch.hearc.ig.guideresto.business.IBusinessObject;
import ch.hearc.ig.guideresto.business.Restaurant;

import java.util.Set;

public class RestaurantMapper extends AbstractMapper {

    @Override
    public Restaurant findById(int id){
        String sql = "SELECT numero, nom, adresse FROM restaurants WHERE numero = ?";
        return null;
    }

    @Override
    public Set findAll() {
        return Set.of();
    }

    @Override
    public IBusinessObject create(IBusinessObject object) {
        return null;
    }

    @Override
    public boolean update(IBusinessObject object) {
        return false;
    }

    @Override
    public boolean delete(IBusinessObject object) {
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
        return "SELECT 1 FROM restaurants WHERE id = ?";
    }

    @Override
    protected String getCountQuery() {
        return "";
    }
}
