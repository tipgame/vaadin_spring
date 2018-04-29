package de.tipgame.backend.service;

import de.tipgame.app.security.SecurityUtils;
import de.tipgame.backend.data.dtos.GameMatchDto;
import de.tipgame.backend.data.entity.UserEntity;
import de.tipgame.backend.data.entity.UserMatchConnectionEntity;
import de.tipgame.backend.repository.UserMatchConnectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMatchConnectionService {
    private UserMatchConnectionRepository userMatchConnectionRepository;
    private UserService userService;

    public UserMatchConnectionService(UserMatchConnectionRepository userMatchConnectionRepository,
                                      UserService userService) {
        this.userMatchConnectionRepository = userMatchConnectionRepository;
        this.userService = userService;
    }

    public void saveTipp(GameMatchDto gameMatchDto) {
        UserEntity currentUser = SecurityUtils.getCurrentUser(userService);
        UserMatchConnectionEntity userMatchConnection = userMatchConnectionRepository.findByUserIdAndGameMatchId(
                currentUser.getId(), gameMatchDto.getGamcheMatchId());

        userMatchConnection.setResultTippHomeTeam(gameMatchDto.getTippHomeTeam());
        userMatchConnection.setResultTippAwayTeam(gameMatchDto.getTippAwayTeam());

        userMatchConnectionRepository.save(userMatchConnection);
    }

    public void saveUserMatchConnection(UserMatchConnectionEntity userMatchConnectionEntities) {
        userMatchConnectionRepository.save(userMatchConnectionEntities);
    }

    public void saveUserMatchConnections(List<UserMatchConnectionEntity> userMatchConnectionEntities) {
        userMatchConnectionRepository.save(userMatchConnectionEntities);
    }

    public UserMatchConnectionEntity getUserMatchConnectionToGameMatchId(Integer gameMatchId) {
        UserEntity currentUser = SecurityUtils.getCurrentUser(userService);
        return userMatchConnectionRepository.findByUserIdAndGameMatchId(
                currentUser.getId(), gameMatchId);
    }

    public List<UserMatchConnectionEntity> getAllTippsFromUserByMatchIds(Integer userId, List<Integer> gameMatchIds) {
        List<UserMatchConnectionEntity> userMatchConnections =
                userMatchConnectionRepository.findByUserIdAndGameMatchIdIn(userId, gameMatchIds);

        return userMatchConnections.stream()
                .filter(e -> !e.getAlreadyProcessed())
                .filter(e -> !e.getResultTippAwayTeam().equals(""))
                .filter(e -> !e.getResultTippHomeTeam().equals(""))
                .collect(Collectors.toList());
    }

    public List<UserMatchConnectionEntity> getAllProcessedTippsFromUser(Integer userId) {
        List<UserMatchConnectionEntity> allTippsFromUser = userMatchConnectionRepository.findByUserId(userId);

        return allTippsFromUser.stream()
                .filter(e -> e.getAlreadyProcessed().equals(true))
                .collect(Collectors.toList());
    }
}
