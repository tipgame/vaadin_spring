package de.tipgame.ui.view.home;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
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
import de.tipgame.ui.charts.TippgameGroupRankChart;
import de.tipgame.ui.charts.TippgameUserPointsChart;
import de.tipgame.ui.navigation.NavigationManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
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
    private TippgameUserPointsChart tippgameUserPointsChart;
    private TippgameGroupRankChart tippgameGroupRankChart;
    private TeamService teamService;

    @Autowired
    public HomeView(NavigationManager navigationManager,
                    UserService userService,
                    StatisticService statisticService,
                    NewsService newsService,
                    TippgameUserPointsChart tippgameUserPointsChart,
                    TippgameGroupRankChart tippgameGroupRankChart,
                    TeamService teamService) {
        this.userService = userService;
        this.navigationManager = navigationManager;
        this.statisticService = statisticService;
        this.newsService = newsService;
        this.tippgameUserPointsChart = tippgameUserPointsChart;
        this.tippgameGroupRankChart = tippgameGroupRankChart;
        this.teamService = teamService;
    }

    @PostConstruct
    public void init() {
        setResponsive(true);
        this.setSizeFull();
        mainLayout = new VerticalLayout();
        createSalutationLabel();
        createNewsSection();
        createRankingLayout();
        createChartLayouts();
        this.addComponent(mainLayout);
    }

    private void createChartLayouts() {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        ResponsiveRow row = responsiveLayout.addRow();

        row
                .withHorizontalSpacing(true)
                .withVerticalSpacing(true)
                .addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(createChartStatisticTimelineLayout());

        ResponsiveRow row1 = responsiveLayout.addRow();

        row1
                .withHorizontalSpacing(true)
                .withVerticalSpacing(true)
                .addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(createChartTeamRankLayout());

        mainLayout.addComponent(responsiveLayout);
    }

    private void createSalutationLabel() {
        UserEntity currentUser = SecurityUtils.getCurrentUser(userService);
        VerticalLayout vL = new VerticalLayout();
        Label salutation = new Label("Hallo " + currentUser.getFirstname());
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
        Label newsLabel = new Label();
        if (newsEntityList.stream().findFirst().isPresent()) {
            NewsEntity newsEntity = newsEntityList.stream().findFirst().get();
            newsLabel.setValue(newsEntity.getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " : " + newsEntity.getMessage());
        }

        panel.setContent(newsLabel);
        newsLabel.setCaptionAsHtml(true);

        vL.addComponent(panel);
        mainLayout.addComponent(vL);

    }

    private void createRankingLayout() {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        ResponsiveRow row = responsiveLayout.addRow();

        row
                .withHorizontalSpacing(true)
                .withVerticalSpacing(true)
                .addColumn()
                .withDisplayRules(12, 6, 6, 6)
                .withComponent(createPanelAndGridForUserRanking());

        row
                .withHorizontalSpacing(true)
                .withVerticalSpacing(true)
                .addColumn()
                .withDisplayRules(12, 6, 6, 6)
                .withComponent(createPanelAndGridForTeamRanking());

        mainLayout.addComponent(responsiveLayout);

    }

    private Panel createChartStatisticTimelineLayout() {
        Panel panel = new Panel();
        VerticalLayout vL = new VerticalLayout();
        panel.setCaption("Persönlicher Punkteverlauf");
        panel.setContent(tippgameUserPointsChart.getChart());

        vL.addComponent(panel);
        return panel;
    }

    private Panel createChartTeamRankLayout() {
        Panel panel = new Panel();
        VerticalLayout vL = new VerticalLayout();
        panel.setCaption("Teams");
        panel.setContent(tippgameGroupRankChart.getChart());

        vL.addComponent(panel);
        return panel;
    }

    private Panel createPanelAndGridForUserRanking() {
        Panel panel = new Panel();
        panel.setCaption("Ranking der Teilnehmer");

        Grid<User> grid = new Grid<>();
        grid.setWidth(100, Unit.PERCENTAGE);
        grid.setItems(userService.getAllUsersSortedByRank());
        grid.addColumn(User::getRank).setCaption("Platz")
                .setStyleGenerator((StyleGenerator<User>) this::getCssClassIfGridCellShouldBeMarked);
        grid.addColumn(User::getFullname).setCaption("Name")
                .setStyleGenerator((StyleGenerator<User>) this::getCssClassIfGridCellShouldBeMarked);
        grid.addColumn(User::getPoints).setCaption("Punkte")
                .setStyleGenerator((StyleGenerator<User>) this::getCssClassIfGridCellShouldBeMarked);


        panel.setContent(grid);

        return panel;
    }

    private String getCssClassIfGridCellShouldBeMarked(User user) {
        final UserEntity currentUser = SecurityUtils.getCurrentUser(userService);
        if (user.getUsername().equalsIgnoreCase(currentUser.getUsername()))
            return "markUserInGrid";
        else
            return "";
    }

    private Panel createPanelAndGridForTeamRanking() {
        Panel panel = new Panel();
        panel.setCaption("Ranking der Teams");
        panel.setResponsive(true);
        Grid<TeamDto> grid = new Grid<>(TeamDto.class);

        grid.setItems(createTeamDtos());
        grid.setWidth(100, Unit.PERCENTAGE);
        grid.setColumns("rank", "teamName", "points");
        grid.getColumn("rank").setCaption("Platz");
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
            teamDto.setRank(team.getRank());
            teamDto.setTeamMembers(buildToolTipWithTeamMembersForTeam(team.getUserIds(), team));
            teamDtos.add(teamDto);
        }

        return teamDtos;
    }

    private String buildToolTipWithTeamMembersForTeam(String userIds, TeamEntity team) {
        String teamMembersHtmlToolTip = "";
        if (userIds != null && !userIds.isEmpty()) {
            List<Integer> userIdList = Stream.of(userIds.split(";"))
                    .map(e -> Integer.valueOf(e.trim()))
                    .collect(Collectors.toList());

            List<UserEntity> userEntities = userService.findByUserIdIn(userIdList);

            teamMembersHtmlToolTip = "Mitglieder: <br>";
            teamMembersHtmlToolTip += userEntities.stream()
                    .map(e -> e.getFirstname() + " " + e.getLastname() + " "+returnCaptain(e, team))
                    .collect(Collectors.joining("<br>"));
        }

        return teamMembersHtmlToolTip;
    }

    private String returnCaptain(UserEntity userEntity, TeamEntity team) {
        if(userEntity.getId() == team.getTeamCaptain())
            return "(C)";
        return "";
    }
}
