package de.tipgame.backend.repository;

import de.tipgame.backend.data.entity.UserMatchConnectionEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserMatchConnectionRepository extends CrudRepository<UserMatchConnectionEntity, Integer> {
    UserMatchConnectionEntity findByUserIdAndGameMatchId(final Integer userId, final Integer gameMatchId);
}
