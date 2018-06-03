package de.tipgame.ui.view.tipps.component;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import de.tipgame.backend.data.dtos.GameMatchDto;
import de.tipgame.backend.service.UserMatchConnectionService;
import de.tipgame.backend.utils.TipgameUtils;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class TippsEditor extends VerticalLayout {

    private GameMatchDto gameMatchDto;
    private UserMatchConnectionService userMatchConnectionService;

    private TextField tippHomeTeam = new TextField("Heimmanschaft");
    private TextField tippAwayTeam = new TextField("Auswärtsmannschaft");

    private Button save = new Button("Speichern", VaadinIcons.CHECK);
    private Button delete = new Button("Löschen", VaadinIcons.TRASH);
    private CssLayout actions = new CssLayout(save, delete) {
        @Override
        protected String getCss(Component c) {
            if (c instanceof Button) {
                return "margin: 5px";
            }
            return null;
        }
    };

    private Binder<GameMatchDto> binder = new Binder<>(GameMatchDto.class);

    @Autowired
    TippsEditor(UserMatchConnectionService userMatchConnectionService) {
        this.userMatchConnectionService = userMatchConnectionService;

        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        ResponsiveRow row = responsiveLayout.addRow();

        row.addColumn()
                .withDisplayRules(12,3,3,3)
                .withComponent(tippHomeTeam);
        row.addColumn()
                .withDisplayRules(12,3,3,3)
                .withComponent(tippAwayTeam);

        tippHomeTeam.setWidth(50, Unit.PIXELS);
        tippAwayTeam.setWidth(50, Unit.PIXELS);
        this.addComponents(responsiveLayout, actions);

        binder.bindInstanceFields(this);

        save.setStyleName(ValoTheme.BUTTON_PRIMARY);

        save.addClickListener(e -> saveTipp(gameMatchDto));
        delete.addClickListener(e -> deleteTipp(gameMatchDto));
    }

    private void deleteTipp(GameMatchDto gameMatchDto) {
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
                    "Bitte wähle in der Liste eine Begegnung aus für welche der Tipp abgegeben werden soll. Evtl. musst du den Filter wieder entfernen.",
                    Notification.Type.WARNING_MESSAGE, false)
                    .show(Page.getCurrent());
            return;
        }

        if (!tippAwayTeam.getValue().matches("-?\\d+(\\.\\d+)?") ||
                !tippHomeTeam.getValue().matches("-?\\d+(\\.\\d+)?")) {
            new Notification("Achtung: ",
                    "Bitte in den Ergebnisfeldern nur Zahlen eintragen.",
                    Notification.Type.WARNING_MESSAGE, false)
                    .show(Page.getCurrent());
        } else {

            gameMatchDto.setTippHomeTeam(tippHomeTeam.getValue());
            gameMatchDto.setTippAwayTeam(tippAwayTeam.getValue());
            userMatchConnectionService.saveTipp(gameMatchDto);

            new Notification("Tipp gespeichert!",
                    "",
                    Notification.Type.HUMANIZED_MESSAGE, false)
                    .show(Page.getCurrent());
        }

        binder.setBean(gameMatchDto);
    }

    public interface ChangeHandler {
        void onChange(GameMatchDto gameMatchDto);
    }

    public final void editTipp(GameMatchDto gameMatchDto) {
        if (gameMatchDto == null) {
            tippAwayTeam.setValue("");
            tippHomeTeam.setValue("");
            save.setEnabled(false);
            delete.setEnabled(false);
            return;
        }

        save.setEnabled(!TipgameUtils.isTimeToDisable(gameMatchDto.getKickOff()));
        delete.setEnabled(!TipgameUtils.isTimeToDisable(gameMatchDto.getKickOff()));
        tippHomeTeam.setCaption(gameMatchDto.getLongNameHomeTeam());
        tippAwayTeam.setCaption(gameMatchDto.getLongNameAwayTeam());

        tippHomeTeam.setValue(gameMatchDto.getTippHomeTeam());
        tippAwayTeam.setValue(gameMatchDto.getTippAwayTeam());

        tippAwayTeam.setEnabled(!TipgameUtils.isTimeToDisable(gameMatchDto.getKickOff()));
        tippHomeTeam.setEnabled(!TipgameUtils.isTimeToDisable(gameMatchDto.getKickOff()));


        this.gameMatchDto = gameMatchDto;
    }

    void setChangeHandler(ChangeHandler h) {
        save.addClickListener(e -> h.onChange(gameMatchDto));
        delete.addClickListener(e -> h.onChange(gameMatchDto));
    }

}