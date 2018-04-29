package de.tipgame.backend.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "game_result")
public class GameResultEntity {
    @Id
    @Column(name = "game_match_id")
    private int gameMatchId;

    @Column(name = "result_away_team")
    private int resultAwayTeam;

    @Column(name = "result_home_team")
    private int resultHomeTeam;

    public int getGameMatchId() {
        return gameMatchId;
    }

    public void setGameMatchId(int gameMatchId) {
        this.gameMatchId = gameMatchId;
    }

    public int getResultAwayTeam() {
        return resultAwayTeam;
    }

    public void setResultAwayTeam(int resultAwayTeam) {
        this.resultAwayTeam = resultAwayTeam;
    }

    public int getResultHomeTeam() {
        return resultHomeTeam;
    }

    public void setResultHomeTeam(int resultHomeTeam) {
        this.resultHomeTeam = resultHomeTeam;
    }
}
