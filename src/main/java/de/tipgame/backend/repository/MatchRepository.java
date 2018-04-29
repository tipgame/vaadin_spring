package de.tipgame.backend.repository;

import de.tipgame.backend.data.entity.GameMatchEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends CrudRepository<GameMatchEntity, Integer> {
    List<GameMatchEntity> findByRoundOrderByPrelimGroupAscKickOffAsc(@Param("round") final String round);
    List<GameMatchEntity> findByRoundAndPrelimGroupOrderByPrelimGroupAscKickOffAsc(
            @Param("round") final String round,
            @Param("prelim_group") final String prelimGroup);

    List<RoundOnly> findDistinctBy();
    List<PrelimGroupOnly> findDistinctPrelimGroupByRound(@Param("round") final String round);
    List<GameMatchEntity> findByMatchIdIn(List<Integer> matchIds);
    List<GameMatchEntity> findAll();
}
