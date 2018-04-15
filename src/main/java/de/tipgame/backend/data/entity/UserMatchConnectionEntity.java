package de.tipgame.backend.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "user_match_connection")
public class UserMatchConnectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "already_processed")
    private Boolean alreadyProcessed;

    @Column(name = "game_match_id")
    private Integer gameMatchId;

    @Column(name = "result_tipp_away_team")
    private String resultTippAwayTeam;

    @Column(name = "result_tipp_home_team")
    private String resultTippHomeTeam;

    @Column(name = "user_id")
    private Integer userId;

    private String round;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getAlreadyProcessed() {
        return alreadyProcessed;
    }

    public void setAlreadyProcessed(Boolean alreadyProcessed) {
        this.alreadyProcessed = alreadyProcessed;
    }

    public Integer getGameMatchId() {
        return gameMatchId;
    }

    public void setGameMatchId(Integer gameMatchId) {
        this.gameMatchId = gameMatchId;
    }

    public String getResultTippAwayTeam() {
        return resultTippAwayTeam;
    }

    public void setResultTippAwayTeam(String resultTippAwayTeam) {
        this.resultTippAwayTeam = resultTippAwayTeam;
    }

    public String getResultTippHomeTeam() {
        return resultTippHomeTeam;
    }

    public void setResultTippHomeTeam(String resultTippHomeTeam) {
        this.resultTippHomeTeam = resultTippHomeTeam;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }
}
