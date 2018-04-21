package de.tipgame.ui.view.tipps;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import de.tipgame.backend.service.GameMatchService;
import de.tipgame.backend.service.UserMatchConnectionService;
import de.tipgame.ui.navigation.NavigationManager;
import de.tipgame.ui.view.tipps.component.TippsCustomComponent;
import de.tipgame.ui.view.tipps.component.TippsCustomComponentForPrelimGroups;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

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
        buildTippsLayout();
    }

    private void buildTippsLayout() {
        List<String> rounds = gameMatchService.getDistinctRounds();
        Accordion tippsAccordionBaseLayout = new Accordion();
        for(String round : rounds) {

            if(round.equalsIgnoreCase("Vorrunde")) {
                buildLayoutForPrelimGroups(tippsAccordionBaseLayout);
            } else {
                TippsCustomComponent tippsCustomComponent = new TippsCustomComponent(
                        userMatchConnectionService,
                        gameMatchService,
                        round);
                tippsAccordionBaseLayout.addTab(tippsCustomComponent.init(), round);
            }
        }

        selectFirstEntryOnTippsView(tippsAccordionBaseLayout);
        tippsAccordionBaseLayout.addSelectedTabChangeListener(e -> {
            HorizontalLayout vL = (HorizontalLayout) e.getTabSheet().getSelectedTab();
            Grid component = (Grid) vL.getComponent(0);
            component.select(component.getDataCommunicator().fetchItemsWithRange(0,1).get(0));
        });
        this.addComponent(tippsAccordionBaseLayout);
    }

    private void selectFirstEntryOnTippsView(Accordion tippsAccordionBaseLayout) {
        HorizontalLayout vL = (HorizontalLayout) tippsAccordionBaseLayout.getTab(0).getComponent();
        Grid component = (Grid) vL.getComponent(0);
        component.select(component.getDataCommunicator().fetchItemsWithRange(0,1).get(0));
    }

    private void buildLayoutForPrelimGroups(Accordion tippsAccordionBaseLayout) {
        List<String> distinctPrelimGroups = gameMatchService.getDistinctPrelimGroups();

        for (String prelimGroup : distinctPrelimGroups) {
            TippsCustomComponentForPrelimGroups tippsCustomComponentForPrelimGroups = new TippsCustomComponentForPrelimGroups(
                    userMatchConnectionService,
                    gameMatchService,
                    prelimGroup);
            tippsAccordionBaseLayout.addTab(tippsCustomComponentForPrelimGroups.init(), "Gruppe " + prelimGroup);
        }
    }
}
