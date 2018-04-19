package de.tipgame.backend.service;

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


    public StatisticService(GameMatchService gameMatchService,
                            UserService userService,
                            UserMatchConnectionService userMatchConnectionService,
                            PointsProcessor pointsProcessor,
                            UserStatisticService userStatisticService,
                            FinalResultService finalResultService) {
        this.gameMatchService = gameMatchService;
        this.userService = userService;
        this.userMatchConnectionService = userMatchConnectionService;
        this.pointsProcessor = pointsProcessor;
        this.userStatisticService = userStatisticService;
        this.finalResultService = finalResultService;
    }

    public void startCalculation() {
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

    }

//    public void CalculateTeamPointsAndRankForAllTeams()
//    {
//        Session session = databaseHelper.getHibernateSession();
//        Iterator<Team> iter = session.createQuery(
//                "from Team")
//                .iterate();
//
//        while(iter.hasNext())
//        {
//            Team team = iter.next();
//
//            String[] userIDs = team.getUserIds().split(";");
//            float sumOfPoints = 0;
//            for (String user : userIDs) {
//                sumOfPoints = sumOfPoints + getAllPointsForUser(Integer.parseInt(user));
//            }
//
//            if(userIDs.length > 0)
//            {
//                float teamPoints = ((sumOfPoints / userIDs.length) * 5);
//                databaseHelper.attachPojoToSession(session, team);
//                team.setPoints(teamPoints);
//                session.saveOrUpdate(team);
//            }
//        }
//        computeTeamRank();
//    }
//
//    private void computeTeamRank(){
//        Session session = databaseHelper.getHibernateSession();
//
//        String sqlQuery = "FROM Team order by points desc";
//        Iterator<Team> iter = session.createQuery(sqlQuery).iterate();
//        int rank = 1;
//        float points = 0;
//        while(iter.hasNext())
//        {
//            Team team = iter.next();
//            if(team.getPoints() < points)
//                rank++;
//            team.setRank(rank);
//            session.saveOrUpdate(team);
//
//            points = team.getPoints();
//        }
//    }
//    private int getAllPointsForUser(int userId)
//    {
//        Session session = databaseHelper.getHibernateSession();
//        Iterator<Statistic> iter = session.createQuery(
//                "from Statistic where userId = ?")
//                .setLong(0, userId)
//                .iterate();
//
//        while(iter.hasNext())
//        {
//            Statistic statistic = iter.next();
//            return statistic.getPoints();
//        }
//        return 0;
//    }


    private void savePoints(Integer points, Integer userId)
    {
        UserStatisticEntity userStatisticForUserId = userStatisticService.getUserStatisticForUserId(userId);
        Integer pointsSum = points + userStatisticForUserId.getPoints();
        userStatisticForUserId.setPoints(pointsSum);
        userStatisticService.saveUserStatistic(userStatisticForUserId);
    }


//    private Integer getStatisticId()
//    {
//        Integer statisticId = null;
//
//        Session session = databaseHelper.getHibernateSession();
//        databaseHelper.attachPojoToSession(session, user);
//
//        statisticId = user.getStatisticId();
//
//        return statisticId;
//    }
//
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

//    public Boolean getDoFullComputation() {
//        return doFullComputation;
//    }
//
//    public void setDoFullComputation(Boolean doFullComputation) {
//        this.doFullComputation = doFullComputation;
//    }
}
