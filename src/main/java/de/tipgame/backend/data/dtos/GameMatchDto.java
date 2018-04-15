package de.tipgame.backend.data.dtos;

public class GameMatchDto {
    private String fixture;
    private String kickOff;
    private String tipp;
    private Integer gamcheMatchId;
    private String tippHomeTeam;
    private String tippAwayTeam;

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
        String tippAway = tippAwayTeam != null ? tippAwayTeam : "";
        String tippHome = tippHomeTeam != null ? tippHomeTeam : "";
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
}
