package net.proselyte.web3.db;

import net.proselyte.web3.db.models.PointModel;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class PointService {

    @Inject
    private PointDAO dao;

    public void save(PointModel point) {
        dao.save(point);
    }

    public List<PointModel> listAll() {
        return dao.findAllDesc();
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}
