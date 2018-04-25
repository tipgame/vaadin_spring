package de.tipgame.backend.repository;

import de.tipgame.backend.data.entity.NewsEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NewsRepository extends CrudRepository<NewsEntity, Integer> {
    List<NewsEntity> findByOrderByIdDesc();
}
