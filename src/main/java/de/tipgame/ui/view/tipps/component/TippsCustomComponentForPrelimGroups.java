package de.tipgame.ui.view.tipps.component;

import de.tipgame.backend.service.GameMatchService;
import de.tipgame.backend.service.UserMatchConnectionService;

public class TippsCustomComponentForPrelimGroups extends TippsCustomComponent {

    public TippsCustomComponentForPrelimGroups(UserMatchConnectionService userMatchConnectionService,
                                               GameMatchService gameMatchService,
                                               String prelimGroup) {
        super(userMatchConnectionService, gameMatchService, prelimGroup);
    }

    @Override
    protected void listMatches() {
        grid.setItems(gameMatchService.getAllMatchesByPrelimGroup(filterArgumentForGettingAllMatches));
    }
}
