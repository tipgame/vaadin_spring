package de.tipgame.backend.data.dtos;

import java.time.LocalDateTime;

public class GameMatchDto {
    private String fixture;
    private String kickOff;
    private String tipp;
    private Integer gamcheMatchId;
    private String tippHomeTeam;
    private String tippAwayTeam;
    private String round;
    private String longNameHomeTeam;
    private String longNameAwayTeam;
    private String shortNameHomeTeam;
    private String shortNameAwayTeam;
    private String resultHomeTeam;
    private String resultAwayTeam;
    private String resultGame;
    private String fixtureLongNameTooltip;
    private String prelimGroup;
    private LocalDateTime originalKickOff;

    public String getFixture() {
        return fixture;
    }

    public void setFixture(String fixture) {
        this.fixture = fixture;
    }

    public String getKickOff() {
        return kickOff;
    }

    public void setKickOff(String kickOff) {
        this.kickOff = kickOff;
    }

    public String getTipp() {
        String tippAway = tippAwayTeam != null ? tippAwayTeam : "-";
        String tippHome = tippHomeTeam != null ? tippHomeTeam : "-";
        return tippHome + " : " + tippAway;
    }

    public void setTipp(String tipp) {
        this.tipp = tipp;
    }

    public Integer getGamcheMatchId() {
        return gamcheMatchId;
    }

    public void setGamcheMatchId(Integer gamcheMatchId) {
        this.gamcheMatchId = gamcheMatchId;
    }

    public String getTippHomeTeam() {
        return tippHomeTeam;
    }

    public void setTippHomeTeam(String tippHomeTeam) {
        this.tippHomeTeam = tippHomeTeam;
    }

    public String getTippAwayTeam() {
        return tippAwayTeam;
    }

    public void setTippAwayTeam(String tippAwayTeam) {
        this.tippAwayTeam = tippAwayTeam;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getLongNameHomeTeam() {
        return longNameHomeTeam;
    }

    public void setLongNameHomeTeam(String longNameHomeTeam) {
        this.longNameHomeTeam = longNameHomeTeam;
    }

    public String getLongNameAwayTeam() {
        return longNameAwayTeam;
    }

    public void setLongNameAwayTeam(String longNameAwayTeam) {
        this.longNameAwayTeam = longNameAwayTeam;
    }

    public String getShortNameHomeTeam() {
        return shortNameHomeTeam;
    }

    public void setShortNameHomeTeam(String shortNameHomeTeam) {
        this.shortNameHomeTeam = shortNameHomeTeam;
    }

    public String getShortNameAwayTeam() {
        return shortNameAwayTeam;
    }

    public void setShortNameAwayTeam(String shortNameAwayTeam) {
        this.shortNameAwayTeam = shortNameAwayTeam;
    }

    public String getResultHomeTeam() {
        return resultHomeTeam;
    }

    public void setResultHomeTeam(String resultHomeTeam) {
        this.resultHomeTeam = resultHomeTeam;
    }

    public String getResultAwayTeam() {
        return resultAwayTeam;
    }

    public void setResultAwayTeam(String resultAwayTeam) {
        this.resultAwayTeam = resultAwayTeam;
    }

    public String getResultGame() {
        String resultAway = resultAwayTeam != null ? resultAwayTeam : "-";
        String resultHome = resultHomeTeam != null ? resultHomeTeam : "-";
        return resultHome + " : " + resultAway;
    }

    public String getFixtureLongNameTooltip() {
        return longNameHomeTeam + " : " + longNameAwayTeam;
    }

    public String getPrelimGroup() {
        return prelimGroup;
    }

    public void setPrelimGroup(String prelimGroup) {
        this.prelimGroup = prelimGroup;
    }

    public LocalDateTime getOriginalKickOff() {
        return originalKickOff;
    }

    public void setOriginalKickOff(LocalDateTime originalKickOff) {
        this.originalKickOff = originalKickOff;
    }
}
