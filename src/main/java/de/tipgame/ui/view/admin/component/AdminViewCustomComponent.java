package de.tipgame.ui.view.admin.component;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import de.tipgame.backend.data.dtos.GameMatchDto;
import de.tipgame.backend.data.entity.GameMatchEntity;
import de.tipgame.backend.service.GameMatchService;
import de.tipgame.backend.service.UserMatchConnectionService;

public class AdminViewCustomComponent extends CustomComponent {

    private Grid<GameMatchEntity> grid;
    private AdminViewCustomEditor adminViewCustomEditor;
    private HorizontalLayout mainLayout;
    GameMatchService gameMatchService;

    public AdminViewCustomComponent(GameMatchService gameMatchService) {
        this.gameMatchService = gameMatchService;
        this.adminViewCustomEditor = new AdminViewCustomEditor(gameMatchService);
    }

    public HorizontalLayout init() {
        grid = new Grid<>(GameMatchEntity.class);
        mainLayout = new HorizontalLayout(grid, adminViewCustomEditor);

        grid.setColumns("kickOff", "fixture", "tipp", "resultGame");

        grid.asSingleSelect().addValueChangeListener(e -> {
            adminViewCustomEditor.editTipp(e.getValue());
        });

        adminViewCustomEditor.setChangeHandler(this::listMatches);

        listMatches();

        return mainLayout;
    }

    protected void listMatches() {
        grid.setItems(gameMatchService.getAllMatches());
    }
}
