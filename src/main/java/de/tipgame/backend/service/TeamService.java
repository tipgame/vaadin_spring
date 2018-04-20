package de.tipgame.backend.service;

import de.tipgame.backend.data.entity.TeamEntity;
import de.tipgame.backend.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TeamService {

    private TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }


    public List<TeamEntity> getAllTeams() {
        return teamRepository.findAll();
    }

    public void saveTeam(TeamEntity teamEntity) {
        teamRepository.save(teamEntity);
    }

    public List<TeamEntity> getAllTeamsOrderdByPointsDesc() {
        return teamRepository.findByOrderByPointsDesc();
    }
}
