package de.tipgame.backend.repository;

import de.tipgame.backend.data.entity.UserMatchConnectionEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserMatchConnectionRepository extends CrudRepository<UserMatchConnectionEntity, Integer> {
    UserMatchConnectionEntity findByUserIdAndGameMatchId(final Integer userId, final Integer gameMatchId);
    List<UserMatchConnectionEntity> findByUserIdAndGameMatchIdIn(Integer userId, List<Integer> gameMatchId);
    List<UserMatchConnectionEntity> findByUserId(Integer userId);
}
