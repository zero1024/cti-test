package com.cti.repository.impl;


import com.cti.repository.PrintJobsRepository;
import com.cti.repository.RepositoryConstraintException;
import com.cti.repository.model.PrintJob;
import com.cti.repository.model.PrintType;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;

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
            try {
                em.persist(job);
            } catch (PersistenceException e) {
                checkForConstraintsViolations(job, e);
                throw e;
            }
        }
    }

    @Override
    public List<PrintJob> find(String user, PrintType type, String device, Date timeFrom, Date timeTo) {
        CriteriaQuery<PrintJob> query = buildQuery(user, type, device, timeFrom, timeTo);
        return em.createQuery(query).getResultList();
    }

    private static void checkForConstraintsViolations(PrintJob job, PersistenceException e) {
        if (e.getCause() instanceof ConstraintViolationException) {
            if (((ConstraintViolationException) e.getCause()).getConstraintName().contains("JOB_AND_DEVICE_CONSTRAINT")) {
                throw new RepositoryConstraintException(format("Job with jobId [%s] and device [%s] already exists", job.getJobId(), job.getDevice()));
            }
        }
    }


    private CriteriaQuery<PrintJob> buildQuery(
            String user,
            PrintType type,
            String device,
            Date timeFrom,
            Date timeTo) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PrintJob> q = cb.createQuery(PrintJob.class);
        Root<PrintJob> root = q.from(PrintJob.class);

        //филььры
        List<Predicate> filters = new ArrayList<>();
        if (user != null) {
            filters.add(cb.equal(root.get("user"), user));
        }
        if (type != null) {
            filters.add(cb.equal(root.get("type"), type));
        }
        if (device != null) {
            filters.add(cb.equal(root.get("device"), device));
        }
        if (timeFrom != null) {
            filters.add(cb.greaterThan(root.get("time"), timeFrom.getTime()));
        }
        if (timeTo != null) {
            filters.add(cb.lessThan(root.get("time"), timeTo.getTime()));
        }
        q.where(filters.toArray(new Predicate[filters.size()]));

        return q;
    }


}
