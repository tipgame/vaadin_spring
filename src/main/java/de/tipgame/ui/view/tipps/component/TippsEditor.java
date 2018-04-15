package de.tipgame.ui.view.tipps.component;

import de.tipgame.backend.data.dtos.GameMatchDto;
import de.tipgame.backend.repository.UserMatchConnectionRepository;
import de.tipgame.backend.service.UserMatchConnectionService;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class TippsEditor extends VerticalLayout {

    private GameMatchDto gameMatchDto;
    private UserMatchConnectionService userMatchConnectionService;

    TextField tippHomeTeam = new TextField("Ergebnis Heimmanschaft");
    TextField tippAwayTeam = new TextField("Ergebnis Auswärtsmannschaft");

    Button save = new Button("Save", VaadinIcons.CHECK);
    Button delete = new Button("Delete", VaadinIcons.TRASH);
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
    }

    private void saveTipp(GameMatchDto gameMatchDto) {
        userMatchConnectionService.saveTipp(gameMatchDto);
    }

    public interface ChangeHandler {

        void onChange();
    }

    public final void editTipp(GameMatchDto c) {
        if (c == null) {
            tippAwayTeam.setValue("");
            tippHomeTeam.setValue("");
            return;
        }
        gameMatchDto = c;
        binder.setBean(c);

        setVisible(true);

        save.focus();
    }

    public void setChangeHandler(ChangeHandler h) {
        save.addClickListener(e -> h.onChange());
        delete.addClickListener(e -> h.onChange());
    }

}