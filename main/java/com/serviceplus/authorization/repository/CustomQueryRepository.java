package com.serviceplus.authorization.repository;

import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import com.serviceplus.authorization.entity.UserAuthHistoryModel;
import com.serviceplus.authorization.entity.UserAuthModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CustomQueryRepository  extends JPADao<Long, Object>{

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void moveUserSessionToHistory(String uuid) {

        try {

            UserAuthModel uat = em.createQuery(
                            "SELECT u FROM UserAuthModel u WHERE u.id.uuid = :uuid",
                            UserAuthModel.class)
                    .setParameter("uuid", uuid)
                    .getSingleResult();

            em.persist(new UserAuthHistoryModel(uat));

            em.createQuery(
                            "DELETE FROM UserAuthModel u WHERE u.id.uuid = :uuid")
                    .setParameter("uuid", uuid)
                    .executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
