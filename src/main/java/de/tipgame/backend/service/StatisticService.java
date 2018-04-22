package de.tipgame.backend.service;

import com.sun.org.glassfish.external.statistics.Statistic;
import de.tipgame.app.security.SecurityUtils;
import de.tipgame.backend.data.entity.*;
import de.tipgame.backend.processor.PointsProcessor;
import de.tipgame.backend.utils.TipgameUtils;
import org.springframework.stereotype.Service;

import java.util.*;

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


    public StatisticService(GameMatchService gameMatchService,
                            UserService userService,
                            UserMatchConnectionService userMatchConnectionService,
                            PointsProcessor pointsProcessor,
                            UserStatisticService userStatisticService,
                            FinalResultService finalResultService, 
                            TeamService teamService) {
        this.gameMatchService = gameMatchService;
        this.userService = userService;
        this.userMatchConnectionService = userMatchConnectionService;
        this.pointsProcessor = pointsProcessor;
        this.userStatisticService = userStatisticService;
        this.finalResultService = finalResultService;
        this.teamService = teamService;
    }

    public boolean startCalculation() {
        Integer points = 0;
        gameMatchs = gameMatchService.getGameResultMapForFinishedGames();

        UserEntity currentUser = SecurityUtils.getCurrentUser(userService);
        if (!gameMatchs.isEmpty()) {
            List<UserMatchConnectionEntity> allTippsFromUser =
                    userMatchConnectionService.getAllTippsFromUser(currentUser.getId(),
                            new ArrayList<>(gameMatchs.keySet()));

            for (UserMatchConnectionEntity userMatchConnection : allTippsFromUser) {
                points = points + computePoints(userMatchConnection);
                userMatchConnection.setAlreadyProcessed(true);
                userMatchConnectionService.saveUserMatchConnection(userMatchConnection);
                savePoints(points, currentUser.getId());
            }
        }

        CalculateFullPointsAfterLastMatch(currentUser);
        return true;
    }

    public void CalculateTeamPointsAndRankForAllTeams()
    {
        List<TeamEntity> teams= teamService.getAllTeams();
        
        for(TeamEntity team : teams) {
            String[] userIDs = team.getUserIds().split(";");
            Float sumOfPoints = 0F;
            for (String userId : userIDs) {
                sumOfPoints = sumOfPoints + getAllPointsForUser(Integer.parseInt(userId));
            }

            if(userIDs.length > 0)
            {
                float teamPoints = ((sumOfPoints / userIDs.length) * 5);
                team.setPoints(teamPoints);
                teamService.saveTeam(team);
            }
        }
        computeTeamRank();
    }

    private void computeTeamRank(){

        List<TeamEntity> allTeamsOrderdByPointsDesc = teamService.getAllTeamsOrderdByPointsDesc();
        int rank = 1;
        float points = 0;
        for (TeamEntity team : allTeamsOrderdByPointsDesc)
        {
            if(team.getPoints() < points)
                rank++;
            team.setRank(rank);
            teamService.saveTeam(team);

            points = team.getPoints();
        }
    }
    
    private int getAllPointsForUser(int userId)
    {
        UserStatisticEntity userStatisticForUserId = userStatisticService.getUserStatisticForUserId(userId);
        return userStatisticForUserId.getPoints();
    }


    private void savePoints(Integer points, Integer userId)
    {
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

        return pointsProcessor.computePoints();
    }

    private void CalculateFullPointsAfterLastMatch(UserEntity user)
    {
        Integer points = 0;
        if(TipgameUtils.isTimeToCalcFinalResults("10.07.2018 00:01")) {

            String winner = user.getWinnerTipp();
            String tippGermany = user.getGermanyTipp();

            Iterable<FinalResultEntity> finalResults = finalResultService.getFinalResults();
            for(FinalResultEntity finalResultEntity : finalResults) {
                if (finalResultEntity.getResultGermany().equalsIgnoreCase(tippGermany)){
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
