package de.tipgame.ui.view.admin;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import de.tipgame.backend.data.Role;
import de.tipgame.backend.service.GameMatchService;
import de.tipgame.backend.service.StatisticService;
import de.tipgame.ui.view.admin.component.AdminViewCustomComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import javax.annotation.PostConstruct;

@Secured(Role.ADMIN)
@SpringView
public class AdminView extends AdminViewDesign implements View {
    private final GameMatchService gameMatchService;
    private StatisticService statisticService;

    @Autowired
    public AdminView(GameMatchService gameMatchService,
                     StatisticService statisticService) {

        this.gameMatchService = gameMatchService;
        this.statisticService = statisticService;
    }

    @PostConstruct
    public void init() {
        setResponsive(true);
        VerticalLayout v = new VerticalLayout();
        createMatchLayout(v);
        createCalcStatisticAdminLayout(v);

        this.addComponent(v);
    }

    private void createMatchLayout(VerticalLayout verticalLayout) {
        Panel panel = new Panel("Begegnungen");
        AdminViewCustomComponent adminViewCustomComponent = new AdminViewCustomComponent(gameMatchService);
        panel.setContent(adminViewCustomComponent.init());
        verticalLayout.addComponent(panel, 0);
    }

    private void createCalcStatisticAdminLayout(VerticalLayout verticalLayout) {
        Panel panel = new Panel("Punkteberechnung");
        HorizontalLayout hL = new HorizontalLayout();
        Button calcStatistic = new Button("Punkte berechnen");
        calcStatistic.addClickListener(e -> {
            ProgressBar bar = new ProgressBar();
            bar.setIndeterminate(true);
            hL.addComponent(bar);
            if (statisticService.startCalculation()) {
                bar.setVisible(false);
                hL.addComponent(new Label("Berechnung beendet."));
            }
        });
        hL.addComponent(calcStatistic);
        panel.setContent(hL);
        verticalLayout.addComponent(panel, 1);
    }

}
