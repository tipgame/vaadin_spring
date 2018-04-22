package de.tipgame.ui.view.tipps.component;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import de.tipgame.backend.data.dtos.GameMatchDto;
import de.tipgame.backend.service.GameMatchService;
import de.tipgame.backend.service.UserMatchConnectionService;

public class TippsCustomComponent extends CustomComponent {

    protected Grid<GameMatchDto> grid;
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

        grid.getColumn("kickOff").setCaption("Anpfiff");
        grid.getColumn("fixture").setCaption("Begegnung");
        grid.getColumn("tipp").setCaption("Dein Tipp");
        grid.getColumn("resultGame").setCaption("Ergebnis");

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
