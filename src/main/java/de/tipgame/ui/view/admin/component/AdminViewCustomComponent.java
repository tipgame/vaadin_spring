package de.tipgame.ui.view.admin.component;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import de.tipgame.backend.data.dtos.GameMatchDto;
import de.tipgame.backend.service.GameMatchService;

import java.util.Arrays;
import java.util.List;

public class AdminViewCustomComponent extends CustomComponent {

    private Grid<GameMatchDto> grid;
    private AdminViewCustomEditor adminViewCustomEditor;
    private VerticalLayout mainLayout;
    GameMatchService gameMatchService;

    public AdminViewCustomComponent(GameMatchService gameMatchService) {
        this.gameMatchService = gameMatchService;
        this.adminViewCustomEditor = new AdminViewCustomEditor(gameMatchService);
    }

    public VerticalLayout init() {
        grid = new Grid<>(GameMatchDto.class);
        grid.setWidth(100, Unit.PERCENTAGE);
        mainLayout = new VerticalLayout(grid, adminViewCustomEditor);

        grid.setColumns(
                "originalKickOff",
                "longNameHomeTeam",
                "longNameAwayTeam",
                "shortNameHomeTeam",
                "shortNameAwayTeam",
                "prelimGroup",
                "round",
                "resultHomeTeam",
                "resultAwayTeam");

        List<String> captions = Arrays.asList(
                "Anpfiff",
                "Heimmannschaft (lang)",
                "Auswärtsmannschaft (lang)",
                "Heimmannschaft (kurz)",
                "Auswärtsmannschaft (kurz)",
                "Gruppe",
                "Runde",
                "Ergebnis Heimmannschaft",
                "Ergebnis Auswärtsmannschaft");

        List<Grid.Column<GameMatchDto, ?>> columns = grid.getColumns();
        Integer index = 0;
        for(Grid.Column col : columns) {
            col.setCaption(captions.get(index));
            index++;
        }
        grid.asSingleSelect().addValueChangeListener(e -> {
            adminViewCustomEditor.editTipp(e.getValue());
        });

        adminViewCustomEditor.setChangeHandler(this::listMatches);

        listMatches();

        return mainLayout;
    }

    protected void listMatches() {
        grid.setItems(gameMatchService.getAllMatchesAsGameMatchDto());
    }
}
