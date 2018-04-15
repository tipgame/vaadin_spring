package de.tipgame.backend.service;

import de.tipgame.backend.data.dtos.GameMatchDto;
import de.tipgame.backend.data.entity.GameMatchEntity;
import de.tipgame.backend.data.entity.UserMatchConnectionEntity;
import de.tipgame.backend.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class GameMatchService {

    private MatchRepository matchRepository;
    private  UserMatchConnectionService userMatchConnectionService;

    public GameMatchService(MatchRepository matchRepository, UserMatchConnectionService userMatchConnectionService) {
        this.matchRepository = matchRepository;
        this.userMatchConnectionService = userMatchConnectionService;
    }

    public List<GameMatchDto> getAllMatches() {
        List<GameMatchEntity> matches = matchRepository.findByRoundOrderByPrelimGroupAscKickOffAsc("Vorrunde");
        List<GameMatchDto> gameMatchDtos = matches.stream()
                .map(gameMatchEntity -> buildGameMatchDto(gameMatchEntity,
                        userMatchConnectionService.getUserMatchConnectionToGameMatchId(gameMatchEntity.getMatchId())))
                .collect(Collectors.toList());

        return gameMatchDtos;
    }

    private GameMatchDto buildGameMatchDto(GameMatchEntity match, UserMatchConnectionEntity userMatchConnectionEntity) {
        GameMatchDto gameMatchDto = new GameMatchDto();
        gameMatchDto.setFixture(match.getHomeTeamShortName() + " : " + match.getAwayTeamShortName());
        gameMatchDto.setKickOff(match.getKickOff().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(new Locale("de"))));
        if(userMatchConnectionEntity != null) {
            gameMatchDto.setTippHomeTeam(userMatchConnectionEntity.getResultTippHomeTeam());
            gameMatchDto.setTippAwayTeam(userMatchConnectionEntity.getResultTippAwayTeam());
        }
        gameMatchDto.setGamcheMatchId(match.getMatchId());
        return gameMatchDto;
    }
}
