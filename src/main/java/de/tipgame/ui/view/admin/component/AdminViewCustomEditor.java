package de.tipgame.ui.view.admin.component;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import de.tipgame.backend.data.dtos.GameMatchDto;
import de.tipgame.backend.data.entity.GameMatchEntity;
import de.tipgame.backend.service.GameMatchService;
import de.tipgame.backend.service.UserMatchConnectionService;
import de.tipgame.backend.utils.TipgameUtils;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class AdminViewCustomEditor extends VerticalLayout {

    private GameMatchEntity gameMatchEntity;
    private GameMatchService gameMatchService;

    TextField tippHomeTeam = new TextField("Ergebnis Heimmanschaft");
    TextField tippAwayTeam = new TextField("Ergebnis Auswärtsmannschaft");

    Button save = new Button("Speichern", VaadinIcons.CHECK);
    Button delete = new Button("Löschen", VaadinIcons.TRASH);
    CssLayout actions = new CssLayout(save, delete);

    Binder<GameMatchEntity> binder = new Binder<>(GameMatchEntity.class);

    @Autowired
    public AdminViewCustomEditor(GameMatchService gameMatchService) {
        this.gameMatchService = gameMatchService;
        addComponents(tippHomeTeam, tippAwayTeam, actions);

        binder.bindInstanceFields(this);
        setSpacing(true);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        save.addClickListener(e -> saveTipp(gameMatchEntity));
        delete.addClickListener(e -> deleteTipp(gameMatchEntity));
    }

    private void deleteTipp (GameMatchEntity gameMatchEntity) {

        new Notification("Tipp gelöscht!",
                "",
                Notification.Type.HUMANIZED_MESSAGE, false)
                .show(Page.getCurrent());
    }

    private void saveTipp(GameMatchEntity gameMatchEntity) {

        new Notification("Tipp gespeichert!",
                "",
                Notification.Type.HUMANIZED_MESSAGE, false)
                .show(Page.getCurrent());
    }

    public interface ChangeHandler {
        void onChange();
    }

    public final void editTipp(GameMatchEntity gameMatchEntity) {

        this.gameMatchEntity = gameMatchEntity;
        binder.setBean(gameMatchEntity);
    }

    public void setChangeHandler(ChangeHandler h) {
        save.addClickListener(e -> h.onChange());
        delete.addClickListener(e -> h.onChange());
    }

}