package de.tipgame.backend.service;

import de.tipgame.backend.data.entity.GameResultEntity;
import de.tipgame.backend.repository.GameResultRepository;
import org.springframework.stereotype.Service;

@Service
public class GameResultService {

    private GameResultRepository gameResultRepository;

    public GameResultService(GameResultRepository gameResultRepository) {
        this.gameResultRepository = gameResultRepository;
    }

    public GameResultEntity getGameResultToMatch(Integer gameMatchId) {
        return gameResultRepository.findByGameMatchId(gameMatchId);
    }
}
