package de.tipgame.ui.view.home;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import de.tipgame.app.security.SecurityUtils;
import de.tipgame.backend.data.dtos.TeamDto;
import de.tipgame.backend.data.dtos.User;
import de.tipgame.backend.data.entity.NewsEntity;
import de.tipgame.backend.data.entity.TeamEntity;
import de.tipgame.backend.data.entity.UserEntity;
import de.tipgame.backend.service.NewsService;
import de.tipgame.backend.service.StatisticService;
import de.tipgame.backend.service.TeamService;
import de.tipgame.backend.service.UserService;
import de.tipgame.ui.charts.TippgameChart;
import de.tipgame.ui.navigation.NavigationManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringView
public class HomeView extends HomeViewDesign implements View {

    private final NavigationManager navigationManager;
    private UserService userService;
    private StatisticService statisticService;
    private NewsService newsService;
    private VerticalLayout mainLayout;
    private TippgameChart tippgameChart;
    private TeamService teamService;

    @Autowired
    public HomeView(NavigationManager navigationManager,
                    UserService userService,
                    StatisticService statisticService,
                    NewsService newsService,
                    TippgameChart tippgameChart,
                    TeamService teamService) {
        this.userService = userService;
        this.navigationManager = navigationManager;
        this.statisticService = statisticService;
        this.newsService = newsService;
        this.tippgameChart = tippgameChart;
        this.teamService = teamService;
    }

    @PostConstruct
    public void init() {
        setResponsive(true);
        this.setSizeFull();
        mainLayout = new VerticalLayout();
        createSalutationLabel();
        createNewsSection();
        createChartStatisticTimelineLayout();
        createRankingLayout();
        this.addComponent(mainLayout);
    }

    private void createSalutationLabel() {
        UserEntity currentUser = SecurityUtils.getCurrentUser(userService);
        VerticalLayout vL = new VerticalLayout();
        Label salutation = new Label("Herzlich willkommen " + currentUser.getFirstname() + " " + currentUser.getLastname());
        salutation.addStyleName("h2");
        vL.addComponent(salutation);
        vL.setComponentAlignment(salutation, Alignment.TOP_CENTER);
        mainLayout.addComponent(vL);
    }

    private void createNewsSection() {
        Panel panel = new Panel();
        VerticalLayout vL = new VerticalLayout();
        panel.setCaption("Neuigkeiten");

        List<NewsEntity> newsEntityList = newsService.getAllNewsOrderdByIdDesc();

        NewsEntity newsEntity = newsEntityList.stream().findFirst().get();

        Label newsLabel = new Label();
        newsLabel.setValue(newsEntity.getTimestamp().toString() + " " + newsEntity.getMessage());

        panel.setContent(newsLabel);

        vL.addComponent(panel);
        mainLayout.addComponent(vL);

    }

    private void createRankingLayout() {
        HorizontalLayout hL = new HorizontalLayout();
        VerticalLayout vL = new VerticalLayout();
        hL.addComponentsAndExpand(createPanelAndGridForUserRanking(), createPanelAndGridForTeamRanking());
        vL.addComponent(hL);

        mainLayout.addComponent(vL);

    }

    private void createChartStatisticTimelineLayout() {
        Panel panel = new Panel();
        VerticalLayout vL = new VerticalLayout();
        panel.setCaption("Punkteverlauf");
        panel.setContent(createChart());

        vL.addComponent(panel);
        mainLayout.addComponent(vL);
    }

    private Panel createPanelAndGridForUserRanking() {
        Panel panel = new Panel();
        panel.setCaption("Ranking der Teilnehmer");

        Grid<User> grid = new Grid<>();
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

        Grid<TeamDto> grid = new Grid<>(TeamDto.class);

        grid.setItems(createTeamDtos());

        grid.setColumns("teamName", "points");
        grid.getColumn("teamName").setCaption("Team");
        grid.getColumn("points").setCaption("Punkte");
        grid.getColumn("teamName").setDescriptionGenerator(TeamDto::getTeamMembers, ContentMode.HTML);

        panel.setContent(grid);

        return panel;
    }

    private List<TeamDto> createTeamDtos() {
        List<TeamEntity> allTeams = teamService.getAllTeamsOrderdByPointsDesc();

        List<TeamDto> teamDtos = new ArrayList<>();
        for (TeamEntity team : allTeams) {
            TeamDto teamDto = new TeamDto();
            teamDto.setTeamName(team.getTeamName());
            teamDto.setPoints(team.getPoints());
            teamDto.setTeamMembers(buildToolTipWithTeamMembersForTeam(team.getUserIds()));
            teamDtos.add(teamDto);
        }

        return teamDtos;
    }

    private String buildToolTipWithTeamMembersForTeam(String userIds) {
        String teamMembersHtmlToolTip = "";
        if (!userIds.isEmpty()) {
            List<Integer> userIdList = Stream.of(userIds.split(";"))
                    .map(Integer::new)
                    .collect(Collectors.toList());

            List<UserEntity> userEntities = userService.findByUserIdIn(userIdList);

            teamMembersHtmlToolTip = userEntities.stream()
                    .map(e -> e.getFirstname() + " " + e.getLastname())
                    .collect(Collectors.joining("<br>"));
        }

        return teamMembersHtmlToolTip;
    }

    private Component createChart() {
        return tippgameChart.getChart();
    }

}
