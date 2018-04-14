package de.tipgame.backend.data.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_match")
public class GameMatchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "match_id")
    private int matchId;

    @Column(name = "away_team_image")
    private String awayTeamImage;
    @Column(name = "away_team_name")
    private String awayTeamName;
    @Column(name = "away_team_short_name")
    private String awayTeamShortName;

    @Column(name = "home_team_image")
    private String homeTeamImage;
    @Column(name = "home_team_name")
    private String homeTeamName;
    @Column(name = "home_team_short_name")
    private String homeTeamShortName;

    @Column(name = "kick_off")
    private LocalDateTime kickOff;

    @Column(name = "prelim_group")
    private String prelimGroup;

    private String round;

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public String getAwayTeamImage() {
        return awayTeamImage;
    }

    public void setAwayTeamImage(String awayTeamImage) {
        this.awayTeamImage = awayTeamImage;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public String getAwayTeamShortName() {
        return awayTeamShortName;
    }

    public void setAwayTeamShortName(String awayTeamShortName) {
        this.awayTeamShortName = awayTeamShortName;
    }

    public String getHomeTeamImage() {
        return homeTeamImage;
    }

    public void setHomeTeamImage(String homeTeamImage) {
        this.homeTeamImage = homeTeamImage;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public String getHomeTeamShortName() {
        return homeTeamShortName;
    }

    public void setHomeTeamShortName(String homeTeamShortName) {
        this.homeTeamShortName = homeTeamShortName;
    }

    public LocalDateTime getKickOff() {
        return kickOff;
    }

    public void setKickOff(LocalDateTime kickOff) {
        this.kickOff = kickOff;
    }

    public String getPrelimGroup() {
        return prelimGroup;
    }

    public void setPrelimGroup(String prelimGroup) {
        this.prelimGroup = prelimGroup;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }
}
