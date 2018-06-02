package de.tipgame.ui.view.tipps.component;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import de.tipgame.backend.data.dtos.GameMatchDto;
import de.tipgame.backend.service.GameMatchService;
import de.tipgame.backend.service.UserMatchConnectionService;

public class TippsCustomComponent extends CustomComponent {

    Grid<GameMatchDto> grid;
    private TippsEditor tippsEditor;
    GameMatchService gameMatchService;
    String filterArgumentForGettingAllMatches;

    public TippsCustomComponent(UserMatchConnectionService userMatchConnectionService,
                                GameMatchService gameMatchService,
                                String filterArgumentForGettingAllMatches) {
        this.gameMatchService = gameMatchService;
        this.tippsEditor = new TippsEditor(userMatchConnectionService);
        this.filterArgumentForGettingAllMatches = filterArgumentForGettingAllMatches;
    }

    public VerticalLayout init() {
        grid = new Grid<>(GameMatchDto.class);
        VerticalLayout mainLayout = new VerticalLayout(grid, tippsEditor);

        grid.setWidth(100, Unit.PERCENTAGE);
        grid.setColumns("kickOff", "fixture", "tipp", "resultGame");
        grid.getColumn("fixture").setDescriptionGenerator(GameMatchDto::getFixtureLongNameTooltip);

        grid.getColumn("kickOff").setCaption("Anpfiff");
        grid.getColumn("fixture").setCaption("Begegnung");
        grid.getColumn("tipp").setCaption("Dein Tipp");
        grid.getColumn("resultGame").setCaption("Ergebnis");

        grid.asSingleSelect().addValueChangeListener(e -> {
            tippsEditor.editTipp(e.getValue());
        });

        tippsEditor.setChangeHandler(gameMatchDto -> {
            if(gameMatchDto != null)
                listMatches();
        });

        listMatches();

        return mainLayout;
    }

    protected void listMatches() {
        grid.setItems(gameMatchService.buildGameMatchDtosToMatchesPerRound(filterArgumentForGettingAllMatches));
    }
}
