package de.tipgame.ui.view.home;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import de.tipgame.app.security.SecurityUtils;
import de.tipgame.backend.data.dtos.User;
import de.tipgame.backend.data.entity.NewsEntity;
import de.tipgame.backend.data.entity.UserEntity;
import de.tipgame.backend.service.NewsService;
import de.tipgame.backend.service.StatisticService;
import de.tipgame.backend.service.UserService;
import de.tipgame.ui.navigation.NavigationManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringView
public class HomeView extends HomeViewDesign implements View {

    private final NavigationManager navigationManager;
    private UserService userService;
    private StatisticService statisticService;
    private NewsService newsService;

    @Autowired
    public HomeView(NavigationManager navigationManager,
                    UserService userService,
                    StatisticService statisticService,
                    NewsService newsService){
        this.userService = userService;
        this.navigationManager = navigationManager;
        this.statisticService = statisticService;
        this.newsService = newsService;
    }

    @PostConstruct
    public void init() {
        setResponsive(true);
        createSalutationLabel();
        createNewsSection();
        createRankingLayout();
    }

    private void createRankingLayout() {
        HorizontalLayout hL = new HorizontalLayout();
        hL.setWidth(90, Unit.PERCENTAGE);
        hL.addComponent(createPanelAndGridForUserRanking());
        hL.addComponent(createPanelAndGridForTeamRanking());

        this.addComponent(hL);
    }
    private void createNewsSection() {
        Panel panel = new Panel();
        panel.setWidth(90, Unit.PERCENTAGE);
        panel.setCaption("Neuigkeiten");
        this.addComponent(panel);
        setComponentAlignment(panel, Alignment.TOP_CENTER);

        List<NewsEntity> newsEntityList = newsService.getAllNewsOrderdByIdDesc();

        NewsEntity newsEntity = newsEntityList.stream().findFirst().get();

        Label newsLabel = new Label();
        newsLabel.setValue(newsEntity.getTimestamp().toString() + " " + newsEntity.getMessage());

        panel.setContent(newsLabel);
    }

    private void createSalutationLabel() {
        UserEntity currentUser = SecurityUtils.getCurrentUser(userService);
        Label salutation = new Label( "Herzlich willkommen "+ currentUser.getFirstname() + " " + currentUser.getLastname() );
        salutation.addStyleName( "h2" );
        this.addComponent(salutation);
        this.setComponentAlignment(salutation, Alignment.TOP_CENTER);
    }

    private Panel createPanelAndGridForUserRanking() {
        Panel panel = new Panel();
        panel.setCaption("Ranking der Teilnehmer");

        Grid<User> grid = new Grid<>();
        grid.setWidth(100, Unit.PERCENTAGE);
        grid.setHeight(100, Unit.PERCENTAGE);
        grid.setItems(userService.getAllUsersSortedByRank());
        grid.addColumn(User::getFirstname).setCaption("Vorname");
        grid.addColumn(User::getLastname).setCaption("Nachname");
        grid.addColumn(User::getPoints).setCaption("Punkte");
        panel.setContent(grid);

        return panel;
    }

    private Panel createPanelAndGridForTeamRanking() {
        Panel panel = new Panel();
        panel.setCaption("Ranking der Teams");

        Grid<User> grid = new Grid<>();
        grid.setWidth(100, Unit.PERCENTAGE);
        grid.setHeight(100, Unit.PERCENTAGE);
        grid.setItems(userService.getAllUsersSortedByRank());
        grid.addColumn(User::getFirstname).setCaption("Vorname");
        grid.addColumn(User::getLastname).setCaption("Nachname");
        grid.addColumn(User::getPoints).setCaption("Punkte");
        panel.setContent(grid);

        return panel;
    }

}
