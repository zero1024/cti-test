package com.cti.repository;


import com.cti.repository.model.PrintJob;
import com.cti.repository.model.PrintType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public class PrintJobsRepositoryImpl implements PrintJobsRepository {

    private EntityManager em;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.em = entityManager;
    }

    @Override
    @Transactional
    public void save(List<PrintJob> jobs) {
        for (PrintJob job : jobs) {
            em.persist(job);
        }
    }

    @Override
    public List<PrintJob> find(String user, PrintType type, String device, Date timeFrom, Date timeTo) {

        CriteriaQuery<PrintJob> query = createQuery(user, type, device, timeFrom, timeTo);

        return em.createQuery(query).getResultList();

    }

    private CriteriaQuery<PrintJob> createQuery(
            String user,
            PrintType type,
            String device,
            Date timeFrom,
            Date timeTo) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<PrintJob> q = cb.createQuery(PrintJob.class);
        Root<PrintJob> root = q.from(PrintJob.class);


        if (user != null) {
            q.where(cb.equal(root.get("user"), user));
        }
        if (type != null) {
            q.where(cb.equal(root.get("type"), type));
        }
        if (device != null) {
            q.where(cb.equal(root.get("device"), device));
        }
        if (timeFrom != null) {
            q.where(cb.greaterThan(root.get("timeFrom"), timeFrom));
        }
        if (timeTo != null) {
            q.where(cb.lessThan(root.get("timeTo"), timeTo));
        }
        return q;
    }


}
