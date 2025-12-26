package net.proselyte.web3.db;

import net.proselyte.web3.db.models.PointModel;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class PointDAO {

    @PersistenceContext(unitName = "lab3PU")
    private EntityManager entityManager;

    @Transactional
    public void save(PointModel point) {
        entityManager.persist(point);
        entityManager.flush();
    }

    public List<PointModel> findAllDesc() {
        return entityManager.createQuery("select p from PointModel p order by p.createdAt desc", PointModel.class)
                .getResultList();
    }

    @Transactional
    public void deleteAll() {
        entityManager.createQuery("delete from PointModel").executeUpdate();
        entityManager.flush();
    }
}
