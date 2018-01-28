package com.mat.dao;

import com.mat.domain.entity.GameEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class GameDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public GameEntity create(final GameEntity gameEntity)
    {
        entityManager.persist(gameEntity);
        entityManager.flush();
        return gameEntity;
    }

    @Transactional
    public GameEntity load(final long id) {
        final GameEntity gameEntity = entityManager.find(GameEntity.class, id);
        return gameEntity;
    }

    @Transactional
    public void update(final GameEntity gameEntity)
    {
        entityManager.merge(gameEntity);
        entityManager.flush();
    }

}
