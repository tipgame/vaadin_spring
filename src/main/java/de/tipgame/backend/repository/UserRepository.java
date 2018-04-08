package de.tipgame.backend.repository;

import de.tipgame.backend.data.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    UserEntity findByUsername(String username);

    List<UserEntity> findAll();

    UserEntity findById(int id);
}
