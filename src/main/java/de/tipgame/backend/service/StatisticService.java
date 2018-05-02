package de.tipgame.backend.service;

import de.tipgame.backend.data.entity.*;
import de.tipgame.backend.processor.PointsProcessor;
import de.tipgame.backend.utils.TipgameUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StatisticService {

    private GameMatchService gameMatchService;
    private UserService userService;
    private UserMatchConnectionService userMatchConnectionService;
    private Map<Integer, GameResultEntity> gameMatchs;
    private PointsProcessor pointsProcessor;
    private UserStatisticService userStatisticService;
    private FinalResultService finalResultService;
    private TeamService teamService;
    private StatisticTimelineService statisticTimelineService;


    public StatisticService(GameMatchService gameMatchService,
                            UserService userService,
                            UserMatchConnectionService userMatchConnectionService,
                            PointsProcessor pointsProcessor,
                            UserStatisticService userStatisticService,
                            FinalResultService finalResultService,
                            TeamService teamService,
                            StatisticTimelineService statisticTimelineService) {
        this.gameMatchService = gameMatchService;
        this.userService = userService;
        this.userMatchConnectionService = userMatchConnectionService;
        this.pointsProcessor = pointsProcessor;
        this.userStatisticService = userStatisticService;
        this.finalResultService = finalResultService;
        this.teamService = teamService;
        this.statisticTimelineService = statisticTimelineService;
    }

    public boolean startCalculation() {
        Integer points = 0;
        gameMatchs = gameMatchService.getGameResultMapForFinishedGames();

        final List<UserEntity> allUsers = userService.findAllUsers();

        for(UserEntity currentUser : allUsers) {
            if (!gameMatchs.isEmpty()) {
                List<UserMatchConnectionEntity> allTippsFromUser =
                        userMatchConnectionService.getAllTippsFromUserByMatchIds(currentUser.getId(),
                                new ArrayList<>(gameMatchs.keySet()));

                for (UserMatchConnectionEntity userMatchConnection : allTippsFromUser) {
                    points = points + computePoints(userMatchConnection);
                    userMatchConnection.setAlreadyProcessed(true);
                    userMatchConnectionService.saveUserMatchConnection(userMatchConnection);
                    savePoints(points, currentUser.getId());
                }
            }

            calculateFullPointsAfterLastMatch(currentUser);
        }
        calculateTeamPointsAndRankForAllTeams();

        return true;
    }

    private void calculateTeamPointsAndRankForAllTeams() {
        List<TeamEntity> teams = teamService.getAllTeams();

        for (TeamEntity team : teams) {
            String[] userIDs = team.getUserIds().split(";");
            Float sumOfPoints = 0F;
            for (String userId : userIDs) {
                sumOfPoints = sumOfPoints + getAllPointsForUser(Integer.parseInt(userId));
            }

            if (userIDs.length > 0) {
                float teamPoints = ((sumOfPoints / userIDs.length) * 5);
                team.setPoints(teamPoints);
                teamService.saveTeam(team);
            }
        }
        computeTeamRank();
    }

    private void computeTeamRank() {

        List<TeamEntity> allTeamsOrderdByPointsDesc = teamService.getAllTeamsOrderdByPointsDesc();
        int rank = 1;
        float points = 0;
        for (TeamEntity team : allTeamsOrderdByPointsDesc) {
            if (team.getPoints() < points)
                rank++;
            team.setRank(rank);
            teamService.saveTeam(team);

            points = team.getPoints();
        }
    }

    private int getAllPointsForUser(int userId) {
        UserStatisticEntity userStatisticForUserId = userStatisticService.getUserStatisticForUserId(userId);
        return userStatisticForUserId.getPoints();
    }


    private void savePoints(Integer points, Integer userId) {
        UserStatisticEntity userStatisticForUserId = userStatisticService.getUserStatisticForUserId(userId);
        Integer pointsSum = points + userStatisticForUserId.getPoints();
        userStatisticForUserId.setPoints(pointsSum);
        userStatisticService.saveUserStatistic(userStatisticForUserId);
    }

    private Integer computePoints(UserMatchConnectionEntity userMatchConnection) {

        GameResultEntity match = gameMatchs.get(userMatchConnection.getGameMatchId());

        Integer homeTeamFinal = match.getResultHomeTeam();
        Integer awayTeamFinal = match.getResultAwayTeam();

        Integer homeTeamTipp = Integer.valueOf(userMatchConnection.getResultTippHomeTeam().trim());
        Integer awayTeamTipp = Integer.valueOf(userMatchConnection.getResultTippAwayTeam().trim());

        pointsProcessor.setAwayTeamFinal(awayTeamFinal);
        pointsProcessor.setHomeTeamFinal(homeTeamFinal);
        pointsProcessor.setAwayTeamTipp(awayTeamTipp);
        pointsProcessor.setHomeTeamTipp(homeTeamTipp);

        Integer points = pointsProcessor.computePoints();

        setStatisticTimeline(userMatchConnection, points);

        return points;
    }

    private void setStatisticTimeline(UserMatchConnectionEntity userMatchConnection,
                                      Integer points) {
        StatisticTimelineEntity statisticTimelineEntity = new StatisticTimelineEntity();
        statisticTimelineEntity.setMatchId(userMatchConnection.getGameMatchId());
        statisticTimelineEntity.setUserId(userMatchConnection.getUserId());
        statisticTimelineEntity.setPoints(points);

        statisticTimelineService.saveStatisticTimeline(statisticTimelineEntity);
    }

    private void calculateFullPointsAfterLastMatch(UserEntity user) {
        Integer points = 0;
        if (TipgameUtils.isTimeToCalcFinalResults("10.07.2018 00:01")) {

            String winner = user.getWinnerTipp();
            String tippGermany = user.getGermanyTipp();

            Iterable<FinalResultEntity> finalResults = finalResultService.getFinalResults();
            for (FinalResultEntity finalResultEntity : finalResults) {
                if (finalResultEntity.getResultGermany().equalsIgnoreCase(tippGermany)) {
                    points = points + 10;
                }
                if (finalResultEntity.getWinner().equalsIgnoreCase(winner)) {
                    points = points + 10;
                }
            }
            savePoints(points, user.getId());

        }
    }
}
