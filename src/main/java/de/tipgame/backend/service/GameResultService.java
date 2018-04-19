package de.tipgame.backend.service;

import de.tipgame.backend.data.entity.GameResultEntity;
import de.tipgame.backend.repository.MatchResultRepository;
import org.springframework.stereotype.Service;

@Service
public class GameResultService {

    private MatchResultRepository matchResultRepository;

    public GameResultService(MatchResultRepository matchResultRepository) {
        this.matchResultRepository = matchResultRepository;
    }

    public GameResultEntity getGameResultToMatch(Integer gameMatchId) {
        return matchResultRepository.findByGameMatchId(gameMatchId);
    }
}
