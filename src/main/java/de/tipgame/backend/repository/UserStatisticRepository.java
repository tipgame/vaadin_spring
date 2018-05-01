package de.tipgame.backend.repository;

import de.tipgame.backend.data.entity.UserStatisticEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStatisticRepository extends CrudRepository<UserStatisticEntity, Integer> {

    List<UserStatisticEntity> findAll();
    UserStatisticEntity findByUserId(Integer userId);
    List<UserStatisticEntity> findAllByOrderByPointsDesc();
}
