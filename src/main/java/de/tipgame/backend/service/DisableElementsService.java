package de.tipgame.backend.service;

import de.tipgame.backend.data.entity.DisableElementsEntity;
import de.tipgame.backend.repository.DisableElementsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Service
public class DisableElementsService {

    private DisableElementsRepository disableElementsRepository;

    public DisableElementsService(DisableElementsRepository disableElementsRepository) {
        this.disableElementsRepository = disableElementsRepository;
    }

    public DisableElementsEntity findElementToDisableEntity(String elementToDisable) {
        return disableElementsRepository.findByElementToDisable(elementToDisable);
    }

    public Boolean isTimeToDisableElement(String elementToDisable) {
         DisableElementsEntity elementToDisableEntity = disableElementsRepository.findByElementToDisable(elementToDisable);

        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 2); // adds one hour

        TimeZone tz = cal.getTimeZone();
        ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();

        LocalDateTime now = LocalDateTime.ofInstant(cal.toInstant(), zid);


         if(elementToDisableEntity != null && elementToDisableEntity.getDateElementIsDisabled() != null)
             return now.compareTo(elementToDisableEntity.getDateElementIsDisabled()) > 0;
         else
             return false;
    }

    public LocalDateTime getTimeElementShouldBeDisabled(String elementToDisable) {
        DisableElementsEntity elementToDisableEntity = disableElementsRepository.findByElementToDisable(elementToDisable);

        if(elementToDisableEntity != null)
            return elementToDisableEntity.getDateElementIsDisabled();
        else
            return null;
    }

    public void saveTimeToDisableElement(DisableElementsEntity disableElementsEntity) {
        disableElementsRepository.save(disableElementsEntity);
    }
}
