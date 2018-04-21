package de.tipgame.ui.view.home;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import de.tipgame.app.security.SecurityUtils;
import de.tipgame.backend.data.dtos.User;
import de.tipgame.backend.data.entity.UserEntity;
import de.tipgame.backend.service.StatisticService;
import de.tipgame.backend.service.UserService;
import de.tipgame.ui.navigation.NavigationManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView
public class HomeView extends HomeViewDesign implements View {

    private final NavigationManager navigationManager;
    private UserService userService;
    private StatisticService statisticService;
    @Autowired
    public HomeView(NavigationManager navigationManager, UserService userService, StatisticService statisticService){
        this.userService = userService;
        this.navigationManager = navigationManager;
        this.statisticService = statisticService;
    }

    @PostConstruct
    public void init() {
        setResponsive(true);
        createSalutationLabel();
        createPanelAndGridForUserRanking();
        createPanelAndGridForTeamRanking();
    }

    private void createSalutationLabel() {
        UserEntity currentUser = SecurityUtils.getCurrentUser(userService);
        Label salutation = new Label( "Herzlich willkommen "+ currentUser.getFirstname() + " " + currentUser.getLastname() );
        salutation.addStyleName( "h2" );
        this.addComponent(salutation);
        this.setComponentAlignment(salutation, Alignment.TOP_CENTER);
    }

    private void createPanelAndGridForUserRanking() {
        Panel panel = new Panel();
        panel.setWidth(90, Unit.PERCENTAGE);
        panel.setCaption("Ranking der Teilnehmer");
        this.addComponent(panel);
        setComponentAlignment(panel, Alignment.TOP_CENTER);

        Grid<User> grid = new Grid<>();
        grid.setWidth(100, Unit.PERCENTAGE);
        grid.setHeight(100, Unit.PERCENTAGE);
        grid.setItems(userService.getAllUsersSortedByRank());
        grid.addColumn(User::getFirstname).setCaption("Vorname");
        grid.addColumn(User::getLastname).setCaption("Nachname");
        grid.addColumn(User::getPoints).setCaption("Punkte");
        panel.setContent(grid);
    }

    private void createPanelAndGridForTeamRanking() {
        Panel panel = new Panel();
        panel.setWidth(90, Unit.PERCENTAGE);
        panel.setCaption("Ranking der Teams");
        this.addComponent(panel);
        this.setExpandRatio(panel, 1.0F);
        setComponentAlignment(panel, Alignment.TOP_CENTER);

        Grid<User> grid = new Grid<>();
        grid.setWidth(100, Unit.PERCENTAGE);
        grid.setHeight(100, Unit.PERCENTAGE);
        grid.setItems(userService.getAllUsersSortedByRank());
        grid.addColumn(User::getFirstname).setCaption("Vorname");
        grid.addColumn(User::getLastname).setCaption("Nachname");
        grid.addColumn(User::getPoints).setCaption("Punkte");
        panel.setContent(grid);
    }

}
