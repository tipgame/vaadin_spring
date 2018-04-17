package de.tipgame.ui.view.tipps.component;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DescriptionGenerator;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import de.tipgame.backend.data.dtos.GameMatchDto;
import de.tipgame.backend.service.GameMatchService;
import de.tipgame.backend.service.UserMatchConnectionService;

public class TippsCustomComponent extends CustomComponent {

    Grid<GameMatchDto> grid;
    private TippsEditor tippsEditor;
    private HorizontalLayout mainLayout;
    private UserMatchConnectionService userMatchConnectionService;
    GameMatchService gameMatchService;
    String filterArgumentForGettingAllMatches;

    public TippsCustomComponent(UserMatchConnectionService userMatchConnectionService,
                                GameMatchService gameMatchService,
                                String filterArgumentForGettingAllMatches) {
        this.userMatchConnectionService = userMatchConnectionService;
        this.gameMatchService = gameMatchService;
        this.tippsEditor = new TippsEditor(userMatchConnectionService);
        this.filterArgumentForGettingAllMatches = filterArgumentForGettingAllMatches;
    }

    public HorizontalLayout init() {
        grid = new Grid<>(GameMatchDto.class);
        mainLayout = new HorizontalLayout(grid, tippsEditor);

        grid.setColumns("kickOff", "fixture", "tipp", "resultGame");
        grid.getColumn("fixture").setDescriptionGenerator(GameMatchDto::getFixtureLongNameTooltip);

        grid.asSingleSelect().addValueChangeListener(e -> {
            tippsEditor.editTipp(e.getValue());
        });

        tippsEditor.setChangeHandler(() -> {
            listMatches();
        });

        listMatches();

        return mainLayout;
    }

    protected void listMatches() {
        grid.setItems(gameMatchService.buildGameMatchDtosToMatchesPerRound(filterArgumentForGettingAllMatches));
    }
}
