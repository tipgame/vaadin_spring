package de.tipgame.backend.repository;

import de.tipgame.backend.data.entity.GameMatchEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MatchRepository extends CrudRepository<GameMatchEntity, Integer> {
    List<GameMatchEntity> findAllByOrderByPrelimGroupAscKickOffAsc();
}
