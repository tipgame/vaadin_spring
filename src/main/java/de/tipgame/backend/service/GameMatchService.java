package de.tipgame.backend.service;

import de.tipgame.backend.data.dtos.GameMatchDto;
import de.tipgame.backend.data.entity.GameMatchEntity;
import de.tipgame.backend.data.entity.UserMatchConnectionEntity;
import de.tipgame.backend.repository.MatchRepository;
import de.tipgame.backend.repository.PrelimGroupOnly;
import de.tipgame.backend.repository.RoundOnly;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
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

    public List<String> getDistinctRounds() {
        List<RoundOnly> distinctBy = matchRepository.findDistinctBy();

        return distinctBy.stream().map(roundOnly -> roundOnly.getRound()).collect(Collectors.toList());
    }

    public List<String> getDistinctPrelimGroups() {
        List<PrelimGroupOnly> distinctBy = matchRepository.findPrelimGroupDistinctBy();

        return distinctBy.stream().map(prelimGroupOnly -> prelimGroupOnly.getPrelimGroup()).collect(Collectors.toList());
    }

    public List<GameMatchDto> getAllMatchesByRound(String round) {
        List<GameMatchEntity> matches = matchRepository.findByRoundOrderByPrelimGroupAscKickOffAsc(round);
        List<GameMatchDto> gameMatchDtos = matches.stream()
                .map(gameMatchEntity -> buildGameMatchDto(gameMatchEntity,
                        userMatchConnectionService.getUserMatchConnectionToGameMatchId(gameMatchEntity.getMatchId())))
                .collect(Collectors.toList());

        return gameMatchDtos;
    }

    public List<GameMatchDto> getAllMatchesByPrelimGroup(String prelimGroup) {
        List<GameMatchEntity> matches = matchRepository.findByPrelimGroupOrderByPrelimGroupAscKickOffAsc(prelimGroup);
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
