package de.tipgame.backend.repository;

import de.tipgame.backend.data.entity.TeamEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamRepository extends CrudRepository<TeamEntity, Integer> {
    List<TeamEntity> findAll();
    List<TeamEntity> findByOrderByPointsDesc();
}
