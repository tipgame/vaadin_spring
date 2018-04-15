package de.tipgame.ui.view.tipps.component;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import de.tipgame.backend.data.dtos.GameMatchDto;
import de.tipgame.backend.repository.UserMatchConnectionRepository;
import de.tipgame.backend.service.GameMatchService;
import de.tipgame.backend.service.UserMatchConnectionService;

import java.util.List;

public class TippsCustomComponent extends CustomComponent {

    private Grid<GameMatchDto> grid;
    private TippsEditor tippsEditor;
    private HorizontalLayout mainLayout;
    private UserMatchConnectionService userMatchConnectionService;
    private GameMatchService gameMatchService;

    public TippsCustomComponent(UserMatchConnectionService userMatchConnectionService,
                                GameMatchService gameMatchService) {
        this.userMatchConnectionService = userMatchConnectionService;
        this.gameMatchService = gameMatchService;
        this.tippsEditor = new TippsEditor(userMatchConnectionService);
    }

    public HorizontalLayout init() {
        grid = new Grid<>(GameMatchDto.class);
        mainLayout = new HorizontalLayout(grid, tippsEditor);

        grid.setColumns("kickOff", "fixture", "tipp");

        grid.asSingleSelect().addValueChangeListener(e -> {
            tippsEditor.editTipp(e.getValue());
        });

        tippsEditor.setChangeHandler(() -> {
            listMatches(gameMatchService.getAllMatches());
        });

        listMatches(gameMatchService.getAllMatches());

        return mainLayout;
    }

    private void listMatches(List<GameMatchDto> matches) {
        grid.setItems(matches);
    }
}
