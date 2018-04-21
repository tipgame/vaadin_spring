package de.tipgame.ui.view.tipps.component;

import com.vaadin.server.Page;
import com.vaadin.ui.*;
import de.tipgame.backend.data.dtos.GameMatchDto;
import de.tipgame.backend.service.UserMatchConnectionService;
import de.tipgame.backend.utils.TipgameUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class TippsEditor extends VerticalLayout {

    private GameMatchDto gameMatchDto;
    private UserMatchConnectionService userMatchConnectionService;

    TextField tippHomeTeam = new TextField("Ergebnis Heimmanschaft");
    TextField tippAwayTeam = new TextField("Ergebnis Auswärtsmannschaft");

    Button save = new Button("Speichern", VaadinIcons.CHECK);
    Button delete = new Button("Löschen", VaadinIcons.TRASH);
    CssLayout actions = new CssLayout(save, delete);

    Binder<GameMatchDto> binder = new Binder<>(GameMatchDto.class);

    @Autowired
    public TippsEditor(UserMatchConnectionService userMatchConnectionService) {
        this.userMatchConnectionService = userMatchConnectionService;
        addComponents(tippHomeTeam, tippAwayTeam, actions);

        binder.bindInstanceFields(this);
        setSpacing(true);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        save.addClickListener(e -> saveTipp(gameMatchDto));
        delete.addClickListener(e -> deleteTipp(gameMatchDto));
    }

    private void deleteTipp (GameMatchDto gameMatchDto) {
        if (gameMatchDto == null) {
            tippHomeTeam.setValue("");
            tippAwayTeam.setValue("");
            return;
        }
        gameMatchDto.setTippHomeTeam("");
        gameMatchDto.setTippAwayTeam("");
        userMatchConnectionService.saveTipp(gameMatchDto);

        new Notification("Tipp gelöscht!",
                "",
                Notification.Type.HUMANIZED_MESSAGE, false)
                .show(Page.getCurrent());
    }

    private void saveTipp(GameMatchDto gameMatchDto) {
        if (gameMatchDto == null) {
            new Notification("Hinweis: ",
                    "Bitte wähle in der Liste links eine Begegnung aus für welche der Tipp abgegeben werden soll",
                    Notification.Type.WARNING_MESSAGE, false)
                    .show(Page.getCurrent());
            return;
        }
        userMatchConnectionService.saveTipp(gameMatchDto);

        new Notification("Tipp gespeichert!",
                "",
                Notification.Type.HUMANIZED_MESSAGE, false)
                .show(Page.getCurrent());
    }

    public interface ChangeHandler {
        void onChange();
    }

    public final void editTipp(GameMatchDto gameMatchDto) {
        if (gameMatchDto == null) {
            tippAwayTeam.setValue("");
            tippHomeTeam.setValue("");
            return;
        }
        tippHomeTeam.setCaption("Ergebnis für " + gameMatchDto.getLongNameHomeTeam());
        tippAwayTeam.setCaption("Ergebnis für " + gameMatchDto.getLongNameAwayTeam());

        tippAwayTeam.setEnabled(!TipgameUtils.isTimeToDisable(gameMatchDto.getKickOff()));
        tippHomeTeam.setEnabled(!TipgameUtils.isTimeToDisable(gameMatchDto.getKickOff()));
        save.setEnabled(!TipgameUtils.isTimeToDisable(gameMatchDto.getKickOff()));
        delete.setEnabled(!TipgameUtils.isTimeToDisable(gameMatchDto.getKickOff()));

        this.gameMatchDto = gameMatchDto;
        binder.setBean(gameMatchDto);
    }

    public void setChangeHandler(ChangeHandler h) {
        save.addClickListener(e -> h.onChange());
        delete.addClickListener(e -> h.onChange());
    }

}