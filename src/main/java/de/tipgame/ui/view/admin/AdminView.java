package de.tipgame.ui.view.admin;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import de.tipgame.app.security.SecurityUtils;
import de.tipgame.backend.data.Role;
import de.tipgame.backend.data.entity.NewsEntity;
import de.tipgame.backend.data.entity.UserEntity;
import de.tipgame.backend.service.GameMatchService;
import de.tipgame.backend.service.NewsService;
import de.tipgame.backend.service.StatisticService;
import de.tipgame.backend.service.UserService;
import de.tipgame.ui.view.admin.component.AdminViewCustomComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import javax.annotation.PostConstruct;
import java.time.Instant;

import static java.util.Date.from;


@Secured(Role.ADMIN)
@SpringView
public class AdminView extends AdminViewDesign implements View {
    private final GameMatchService gameMatchService;
    private StatisticService statisticService;
    private NewsService newsService;
    private UserService userService;

    @Autowired
    public AdminView(GameMatchService gameMatchService,
                     StatisticService statisticService,
                     NewsService newsService,
                     UserService userService) {

        this.gameMatchService = gameMatchService;
        this.statisticService = statisticService;
        this.newsService = newsService;
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        setResponsive(true);
        VerticalLayout v = new VerticalLayout();
        createMatchLayout(v);
        createCalcStatisticAdminLayout(v);
        createNewsSection(v);
        this.addComponent(v);
    }

    private void createNewsSection(VerticalLayout verticalLayout) {
        VerticalLayout vL = new VerticalLayout();
        TextField newsField = new TextField();
        newsField.setPlaceholder("Neuigkeit eingeben");
        vL.addComponent(newsField);
        Button saveNewsBtn = new Button();
        saveNewsBtn.setCaption("Neuigkeit speichern");
        vL.addComponent(saveNewsBtn);

        saveNewsBtn.addClickListener(clickEvent -> {
            if (!(newsField.getValue().isEmpty() ||
                    newsField.getValue().equalsIgnoreCase("Neuigkeit eingeben"))) {
                NewsEntity newsEntity = new NewsEntity();
                newsEntity.setMessage(newsField.getValue());
                newsEntity.setTimestamp(from(Instant.now()));
                this.newsService.saveNews(newsEntity);
            }
        });

        Panel p = new Panel();
        p.setCaption("Neuigkeiten");
        p.setContent(vL);

        verticalLayout.addComponent(p);

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
        verticalLayout.addComponent(panel);

        Panel panelAdditionalPoints = new Panel("Zusatzpunkte berechnen");
        HorizontalLayout hLAdditionalPoints = new HorizontalLayout();
        Button calcStatisticAdditionalPoints = new Button("Zusatzpunkte berechnen");
        calcStatisticAdditionalPoints.addClickListener(e -> {
            ProgressBar bar = new ProgressBar();
            bar.setIndeterminate(true);
            hLAdditionalPoints.addComponent(bar);
            final UserEntity currentUser = SecurityUtils.getCurrentUser(userService);
            if (statisticService.calculateFullPointsAfterLastMatch(currentUser)) {
                bar.setVisible(false);
                hLAdditionalPoints.addComponent(new Label("Berechnung beendet."));
            }
        });
        hLAdditionalPoints.addComponent(calcStatisticAdditionalPoints);
        panelAdditionalPoints.setContent(hLAdditionalPoints);
        verticalLayout.addComponent(panelAdditionalPoints);
    }

}
