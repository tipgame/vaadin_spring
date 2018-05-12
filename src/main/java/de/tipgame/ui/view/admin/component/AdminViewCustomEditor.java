package de.tipgame.ui.view.admin.component;

import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import de.tipgame.backend.data.dtos.GameMatchDto;
import de.tipgame.backend.service.GameMatchService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@SpringComponent
@UIScope
public class AdminViewCustomEditor extends VerticalLayout {

    private GameMatchDto gameMatchDto;
    private GameMatchService gameMatchService;

    TextField kickOff = new TextField("Anpfiff");
    TextField longNameHomeTeam = new TextField("Heimmannschaft (lang)");
    TextField shortNameHomeTeam = new TextField("Heimmannschaft (kurz)");
    TextField longNameAwayTeam = new TextField("Auswärtsmannschaft (lang)");
    TextField shortNameAwayTeam = new TextField("Auswärtsmannschaft (kurz)");
    TextField prelimGroup = new TextField("Gruppe");
    TextField round = new TextField("Runde");
    TextField resultHomeTeam = new TextField("Ergebnis Heimmannschaft");
    TextField resultAwayTeam = new TextField("Ergebnis Auswärtsmannschaft");

    Button saveNew = new Button("Neuer Eintrag", VaadinIcons.CHECK);
    Button saveChanges = new Button("Änderungen Speichern", VaadinIcons.CHECK);
    Button clear = new Button("Eingabe leeren", VaadinIcons.TRASH);
    CssLayout actions = new CssLayout(saveNew, saveChanges, clear);

    Binder<GameMatchDto> binder = new Binder<>(GameMatchDto.class);

    @Autowired
    public AdminViewCustomEditor(GameMatchService gameMatchService) {
        this.gameMatchService = gameMatchService;
        HorizontalLayout hL1 = new HorizontalLayout();
        hL1.addComponents(
                kickOff,
                longNameHomeTeam,
                longNameAwayTeam);

        HorizontalLayout hL2_1 = new HorizontalLayout();
        hL2_1.addComponents(
                shortNameHomeTeam,
                shortNameAwayTeam);

        HorizontalLayout hL2_2 = new HorizontalLayout();
        hL2_2.addComponents(
                prelimGroup,
                round);

        HorizontalLayout hL3 = new HorizontalLayout();
        hL3.addComponents(
                resultHomeTeam,
                resultAwayTeam);

        HorizontalLayout hL4 = new HorizontalLayout();
        hL4.setSpacing(true);
        hL4.addComponents(
                actions);

        addComponents(
                hL1,
                hL2_1,
                hL2_2,
                hL3,
                hL4
        );

        binder.bindInstanceFields(this);
        setSpacing(true);

        saveChanges.addClickListener(e -> updateMatch(gameMatchDto));
        clear.addClickListener(e -> clearInput());
        saveNew.addClickListener(e -> saveMatch(gameMatchDto));
    }

    private void updateMatch(GameMatchDto gameMatchDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMANY);
        gameMatchDto.setOriginalKickOff(LocalDateTime.parse(gameMatchDto.getKickOff(), formatter));
        gameMatchService.updateGameMatch(gameMatchDto);
        new Notification("Eintrag geändert!",
                "",
                Notification.Type.HUMANIZED_MESSAGE, false)
                .show(Page.getCurrent());
    }

    private void clearInput() {
        for (Component c : components) {
            if (c instanceof TextField) {
                ((TextField) c).setValue("");
            }
        }
    }

    private void saveMatch(GameMatchDto gameMatchDto) {
        if (gameMatchDto == null) {
            gameMatchDto = new GameMatchDto();

            gameMatchDto.setShortNameHomeTeam(shortNameHomeTeam.getValue());
            gameMatchDto.setShortNameAwayTeam(shortNameAwayTeam.getValue());
            gameMatchDto.setLongNameHomeTeam(longNameHomeTeam.getValue());
            gameMatchDto.setLongNameAwayTeam(longNameAwayTeam.getValue());
            gameMatchDto.setKickOff(kickOff.getValue());
            gameMatchDto.setPrelimGroup(prelimGroup.getValue());
            gameMatchDto.setRound(round.getValue());
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMANY);
        gameMatchDto.setOriginalKickOff(LocalDateTime.parse(gameMatchDto.getKickOff(), formatter));
        gameMatchService.saveGameMatch(gameMatchDto);
        new Notification("Eintrag gespeichert!",
                "",
                Notification.Type.HUMANIZED_MESSAGE, false)
                .show(Page.getCurrent());
    }

    public interface ChangeHandler {
        void onChange();
    }

    public final void editTipp(GameMatchDto gameMatchDto) {

        this.gameMatchDto = gameMatchDto;
        binder.setBean(gameMatchDto);
    }

    public void setChangeHandler(ChangeHandler h) {
        saveChanges.addClickListener(e -> h.onChange());
        saveNew.addClickListener(e -> h.onChange());
    }
}