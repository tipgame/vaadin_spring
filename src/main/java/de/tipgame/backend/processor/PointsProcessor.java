package de.tipgame.backend.processor;

import org.springframework.stereotype.Component;

@Component
public class PointsProcessor {

    private Integer homeTeamFinal;
    private Integer awayTeamFinal;
    private Integer homeTeamTipp;
    private Integer awayTeamTipp;

    public Integer computePoints() {
        Integer points = 0;
        if (isExcactPrediction())
            points = 6;
        else if (isGoalRatio())
            points = 4;
        else if (isTendency())
            points = 2;
        else if (isResultOfScoredGoals())
            points = 1;

        return points;
    }

    private Boolean isResultOfScoredGoals() {
        return ((homeTeamTipp + awayTeamTipp) == (homeTeamFinal + awayTeamFinal));
    }

    private Boolean isTendency() {
        Boolean winnerHomeTeamFinal = false;
        Boolean winnerHomeTeamTipp = false;
        Boolean equalResultFinal = false;
        Boolean equalResultTipp = false;

        if (homeTeamTipp > awayTeamTipp) {
            winnerHomeTeamTipp = true;
        } else if (homeTeamTipp == awayTeamTipp) {
            equalResultTipp = true;
        } else if (homeTeamTipp < awayTeamTipp) {
            winnerHomeTeamTipp = false;
        }

        if (homeTeamFinal > awayTeamFinal) {
            winnerHomeTeamFinal = true;
        } else if (homeTeamFinal == awayTeamFinal) {
            equalResultFinal = true;
        } else if (homeTeamFinal < awayTeamFinal) {
            winnerHomeTeamFinal = false;
        }

        if (equalResultFinal || equalResultTipp) {
            return false;
        } else {
            return (winnerHomeTeamFinal == winnerHomeTeamTipp);
        }
    }

    private Boolean isGoalRatio() {
        return (homeTeamTipp - awayTeamTipp) == (homeTeamFinal - awayTeamFinal);
    }

    private Boolean isExcactPrediction() {
        return (homeTeamFinal == homeTeamTipp) && (awayTeamFinal == awayTeamTipp);
    }

    public void setHomeTeamFinal(Integer homeTeamFinal) {
        this.homeTeamFinal = homeTeamFinal;
    }

    public void setAwayTeamFinal(Integer awayTeamFinal) {
        this.awayTeamFinal = awayTeamFinal;
    }

    public void setHomeTeamTipp(Integer homeTeamTipp) {
        this.homeTeamTipp = homeTeamTipp;
    }

    public void setAwayTeamTipp(Integer awayTeamTipp) {
        this.awayTeamTipp = awayTeamTipp;
    }
}
