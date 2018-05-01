package de.tipgame.backend.data.dtos;

public class TeamDto {
    private Integer teamId;
    private Float points;
    private Integer rank;
    private String userIds;
    private String teamName;
    private String teamMembers;

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public void setPoints(Float points) {
        this.points = points;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getUserIds() {
        return userIds;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public Float getPoints() {
        return points;
    }

    public Integer getRank() {
        return rank;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(String teamMembers) {
        this.teamMembers = teamMembers;
    }
}
