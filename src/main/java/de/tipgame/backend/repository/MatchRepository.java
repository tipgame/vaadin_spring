package de.tipgame.backend.repository;

import de.tipgame.backend.data.entity.GameMatchEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends CrudRepository<GameMatchEntity, Integer> {
    List<GameMatchEntity> findByRoundOrderByPrelimGroupAscKickOffAsc(@Param("round") final String round);
}
