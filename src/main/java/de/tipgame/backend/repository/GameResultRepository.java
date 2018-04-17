package de.tipgame.backend.repository;

import de.tipgame.backend.data.entity.GameResultEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface GameResultRepository extends CrudRepository<GameResultEntity, Integer> {

    GameResultEntity findByGameMatchId(@Param("game_match_id") Integer gameMatchId);
}
