package de.tipgame.ui.view.tipps;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import de.tipgame.backend.repository.UserMatchConnectionRepository;
import de.tipgame.backend.service.GameMatchService;
import de.tipgame.backend.service.UserMatchConnectionService;
import de.tipgame.ui.navigation.NavigationManager;
import de.tipgame.ui.view.tipps.component.TippsCustomComponent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * The tipps view showing statistics about sales and deliveries.
 * <p>
 * Created as a single View class because the logic is so simple that using a
 * pattern like MVP would add much overhead for little gain. If more complexity
 * is added to the class, you should consider splitting out a presenter.
 */
@SpringView
public class TippsView extends TippsViewDesign implements View {

    private NavigationManager navigationManager;
    private final GameMatchService gameMatchService;
    private UserMatchConnectionService userMatchConnectionService;

    @Autowired
    public TippsView(NavigationManager navigationManager,
                     GameMatchService gameMatchService,
                     UserMatchConnectionService userMatchConnectionService) {
        this.gameMatchService = gameMatchService;
        this.navigationManager = navigationManager;
        this.userMatchConnectionService = userMatchConnectionService;
    }

    @PostConstruct
    public void init() {
        setResponsive(true);
        TippsCustomComponent tippsCustomComponent = new TippsCustomComponent(
                userMatchConnectionService,
                gameMatchService);
        this.addComponent(tippsCustomComponent.init());
    }

}
