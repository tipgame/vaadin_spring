package de.tipgame.backend.service;

import de.tipgame.backend.data.entity.UserStatisticEntity;
import de.tipgame.backend.repository.UserStatisticRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserStatisticService {
    private UserStatisticRepository userStatisticRepository;

    public UserStatisticService(UserStatisticRepository userStatisticRepository) {
        this.userStatisticRepository = userStatisticRepository;
    }

    public UserStatisticEntity getUserStatisticForUserId(Integer userId) {
        return userStatisticRepository.findByUserId(userId);
    }

    public void saveUserStatistic(UserStatisticEntity userStatisticEntity) {
        userStatisticRepository.save(userStatisticEntity);
    }

    public List<UserStatisticEntity> getAllStatisticsOrderdByPointsDesc() {
        return userStatisticRepository.findAllByOrderByPointsDesc();
    }
}
