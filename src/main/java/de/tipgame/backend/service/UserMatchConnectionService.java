package de.tipgame.backend.service;

import de.tipgame.app.security.SecurityUtils;
import de.tipgame.backend.data.dtos.GameMatchDto;
import de.tipgame.backend.data.entity.UserEntity;
import de.tipgame.backend.data.entity.UserMatchConnectionEntity;
import de.tipgame.backend.repository.UserMatchConnectionRepository;
import org.springframework.stereotype.Service;

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

    public UserMatchConnectionEntity getUserMatchConnectionToGameMatchId(Integer gameMatchId) {
        UserEntity currentUser = SecurityUtils.getCurrentUser(userService);
        return userMatchConnectionRepository.findByUserIdAndGameMatchId(
                currentUser.getId(), gameMatchId);
    }
}
