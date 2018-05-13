package de.tipgame.backend.repository;

import de.tipgame.backend.data.entity.DisableElementsEntity;
import org.springframework.data.repository.CrudRepository;

public interface DisableElementsRepository extends CrudRepository<DisableElementsEntity, Integer> {

    DisableElementsEntity findByElementToDisable(String elementToDisable);

}
