package de.tipgame.backend.service;

import de.tipgame.backend.data.dtos.GameMatchDto;
import de.tipgame.backend.data.entity.GameMatchEntity;
import de.tipgame.backend.data.entity.GameResultEntity;
import de.tipgame.backend.data.entity.UserMatchConnectionEntity;
import de.tipgame.backend.repository.MatchRepository;
import de.tipgame.backend.repository.MatchResultRepository;
import de.tipgame.backend.repository.PrelimGroupOnly;
import de.tipgame.backend.repository.RoundOnly;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class GameMatchService {

    private MatchRepository matchRepository;
    private UserMatchConnectionService userMatchConnectionService;
    private GameResultService gameResultService;
    private MatchResultRepository matchResultRepository;

    public GameMatchService(MatchRepository matchRepository, UserMatchConnectionService userMatchConnectionService, GameResultService gameResultService, MatchResultRepository matchResultRepository) {
        this.matchRepository = matchRepository;
        this.userMatchConnectionService = userMatchConnectionService;
        this.gameResultService = gameResultService;
        this.matchResultRepository = matchResultRepository;
    }

    public List<String> getDistinctRounds() {
        List<RoundOnly> distinctBy = matchRepository.findDistinctBy();

        return distinctBy.stream().map(roundOnly -> roundOnly.getRound()).collect(Collectors.toList());
    }

    public List<String> getDistinctPrelimGroups() {
        List<PrelimGroupOnly> distinctBy = matchRepository.findDistinctPrelimGroupByRound("Vorrunde");

        return distinctBy.stream().map(prelimGroupOnly -> prelimGroupOnly.getPrelimGroup()).collect(Collectors.toList());
    }

    public List<GameMatchEntity> getAllMatchesByRound(String round) {
        List<GameMatchEntity> matches = matchRepository.findByRoundOrderByPrelimGroupAscKickOffAsc(round);
        return matches;
    }

    public List<GameMatchEntity> getAllMatches() {
        return matchRepository.findAll();
    }

    public List<GameMatchDto> buildGameMatchDtosToMatchesPerRound(String round) {
        List<GameMatchEntity> matches = getAllMatchesByRound(round);
        List<GameMatchDto> gameMatchDtos = matches.stream()
                .map(gameMatchEntity -> buildGameMatchDto(gameMatchEntity,
                        userMatchConnectionService.getUserMatchConnectionToGameMatchId(gameMatchEntity.getMatchId()),
                        gameResultService.getGameResultToMatch(gameMatchEntity.getMatchId())))
                .collect(Collectors.toList());

        return gameMatchDtos;
    }

    public List<GameMatchDto> getAllMatchesByPrelimGroup(String prelimGroup) {
        List<GameMatchEntity> matches = matchRepository.findByRoundAndPrelimGroupOrderByPrelimGroupAscKickOffAsc(
                "Vorrunde", prelimGroup);
        List<GameMatchDto> gameMatchDtos = matches.stream()
                .map(gameMatchEntity -> buildGameMatchDto(gameMatchEntity,
                        userMatchConnectionService.getUserMatchConnectionToGameMatchId(gameMatchEntity.getMatchId()),
                        gameResultService.getGameResultToMatch(gameMatchEntity.getMatchId())))
                .collect(Collectors.toList());

        return gameMatchDtos;
    }

    public Map<Integer, GameResultEntity> getGameResultMapForFinishedGames() {
        Iterable<GameResultEntity> allMatchResults = matchResultRepository.findAll();

        return StreamSupport.stream(allMatchResults.spliterator(), false)
                .collect(Collectors.toMap(GameResultEntity::getGameMatchId, Function.identity()));
    }

    private GameMatchDto buildGameMatchDto(GameMatchEntity match,
                                           UserMatchConnectionEntity userMatchConnectionEntity,
                                           GameResultEntity gameResultEntity) {
        GameMatchDto gameMatchDto = new GameMatchDto();
        gameMatchDto.setFixture(match.getHomeTeamShortName() + " : " + match.getAwayTeamShortName());
        gameMatchDto.setKickOff(match.getKickOff().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(new Locale("de"))));

        if (userMatchConnectionEntity != null) {
            gameMatchDto.setTippHomeTeam(userMatchConnectionEntity.getResultTippHomeTeam());
            gameMatchDto.setTippAwayTeam(userMatchConnectionEntity.getResultTippAwayTeam());
        }
        if(gameResultEntity != null) {
            gameMatchDto.setResultAwayTeam(String.valueOf(gameResultEntity.getResultAwayTeam()));
            gameMatchDto.setResultHomeTeam(String.valueOf(gameResultEntity.getResultHomeTeam()));
        }
        gameMatchDto.setRound(match.getRound());
        gameMatchDto.setGamcheMatchId(match.getMatchId());
        gameMatchDto.setLongNameAwayTeam(match.getAwayTeamName());
        gameMatchDto.setLongNameHomeTeam(match.getHomeTeamName());

        return gameMatchDto;
    }
}
