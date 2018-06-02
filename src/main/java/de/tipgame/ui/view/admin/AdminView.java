package de.tipgame.ui.view.admin;

import com.vaadin.data.Result;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import de.tipgame.app.security.SecurityUtils;
import de.tipgame.backend.data.Role;
import de.tipgame.backend.data.entity.DisableElementsEntity;
import de.tipgame.backend.data.entity.NewsEntity;
import de.tipgame.backend.data.entity.UserEntity;
import de.tipgame.backend.service.*;
import de.tipgame.ui.view.admin.component.AdminViewCustomComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Secured(Role.ADMIN)
@SpringView
public class AdminView extends AdminViewDesign implements View {
    private final GameMatchService gameMatchService;
    private StatisticService statisticService;
    private NewsService newsService;
    private UserService userService;
    private DisableElementsService disableElementsService;

    @Autowired
    public AdminView(GameMatchService gameMatchService,
                     StatisticService statisticService,
                     NewsService newsService,
                     UserService userService,
                     DisableElementsService disableElementsService) {

        this.gameMatchService = gameMatchService;
        this.statisticService = statisticService;
        this.newsService = newsService;
        this.userService = userService;
        this.disableElementsService = disableElementsService;
    }

    @PostConstruct
    public void init() {
        setResponsive(true);
        VerticalLayout v = new VerticalLayout();
        createMatchLayout(v);
        createCalcStatisticAdminLayout(v);
        createNewsSection(v);
        createDisableTippGermanySection(v);
        createDisableTippWinnerChampionshipSection(v);
        createDisableRegistrationSection(v);
        this.addComponent(v);
    }

    private void createDisableRegistrationSection(VerticalLayout verticalLayout) {
        Panel panel = new Panel();
        VerticalLayout vL = new VerticalLayout();

        LocalDateTime timeElementShouldBeDisabled = disableElementsService
                .getTimeElementShouldBeDisabled("registration");

        DateTimeField date = new DateTimeField("Wann soll die Anmeldung gesperrt werden?", timeElementShouldBeDisabled) {
            @Override
            protected Result<LocalDateTime> handleUnparsableDateString(
                    String dateString) {
                try {
                    // try to parse with alternative format
                    LocalDateTime parsedAtServer = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE);
                    return Result.ok(parsedAtServer);
                } catch (Exception e) {
                    return Result.error("Falsche Eingabe");
                }
            }
        };

        date.setDateFormat("dd.MM.yyyy hh:mm");
        date.setLenient(true);

        date.addValueChangeListener(e -> {
            saveElementToDisable("registration", e.getValue());
        });

        vL.addComponent(date);
        panel.setContent(vL);
        verticalLayout.addComponent(panel);
    }

    private void createDisableTippWinnerChampionshipSection(VerticalLayout verticalLayout) {
        Panel panel = new Panel();
        VerticalLayout vL = new VerticalLayout();

        LocalDateTime timeElementShouldBeDisabled = disableElementsService.getTimeElementShouldBeDisabled("tippChampionshipWinner");

        DateTimeField date = new DateTimeField("Wann soll der Tipp für den Weltmeister gesperrt werden?", timeElementShouldBeDisabled) {
            @Override
            protected Result<LocalDateTime> handleUnparsableDateString(
                    String dateString) {
                try {
                    // try to parse with alternative format
                    LocalDateTime parsedAtServer = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE);
                    return Result.ok(parsedAtServer);
                } catch (Exception e) {
                    return Result.error("Falsche Eingabe");
                }
            }
        };

        date.setDateFormat("dd.MM.yyyy hh:mm");
        date.setLenient(true);

        date.addValueChangeListener(e -> {
            saveElementToDisable("tippChampionshipWinner", e.getValue());
        });

        vL.addComponent(date);
        panel.setContent(vL);
        verticalLayout.addComponent(panel);
    }

    private void createDisableTippGermanySection(VerticalLayout verticalLayout) {
        Panel panel = new Panel();
        VerticalLayout vL = new VerticalLayout();

        LocalDateTime timeElementShouldBeDisabled = disableElementsService.getTimeElementShouldBeDisabled("tippGermany");

        DateTimeField date = new DateTimeField("Wann soll der Tipp für das Ergebnis für Deutschland gesperrt werden?", timeElementShouldBeDisabled) {
            @Override
            protected Result<LocalDateTime> handleUnparsableDateString(
                    String dateString) {
                try {
                    // try to parse with alternative format
                    LocalDateTime parsedAtServer = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE);
                    return Result.ok(parsedAtServer);
                } catch (Exception e) {
                    return Result.error("Falsche Eingabe");
                }
            }
        };

        date.setDateFormat("dd.MM.yyyy hh:mm");
        date.setLenient(true);

        date.addValueChangeListener(e -> {
            saveElementToDisable("tippGermany", e.getValue());
        });

        vL.addComponent(date);
        panel.setContent(vL);
        verticalLayout.addComponent(panel);
    }

    private void saveElementToDisable(String elementToDisable, LocalDateTime timeToDisable) {
        DisableElementsEntity elementToDisableEntity = disableElementsService.findElementToDisableEntity(elementToDisable);
        if(elementToDisableEntity == null) {
            elementToDisableEntity = new DisableElementsEntity();
            elementToDisableEntity.setElementToDisable(elementToDisable);
        }
        elementToDisableEntity.setDateElementIsDisabled(timeToDisable);
        disableElementsService.saveTimeToDisableElement(elementToDisableEntity);
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
                newsEntity.setTimestamp(LocalDateTime.now());
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
        VerticalLayout vL = new VerticalLayout();
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
        vL.addComponent(hL);
        panel.setContent(vL);
        verticalLayout.addComponent(panel);

        Panel panelAdditionalPoints = new Panel("Zusatzpunkte berechnen");
        HorizontalLayout hLAdditionalPoints = new HorizontalLayout();
        VerticalLayout vLAdditionalPoints = new VerticalLayout();
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
        vLAdditionalPoints.addComponent(hLAdditionalPoints);
        panelAdditionalPoints.setContent(vLAdditionalPoints);

        verticalLayout.addComponent(panelAdditionalPoints);
    }

}
