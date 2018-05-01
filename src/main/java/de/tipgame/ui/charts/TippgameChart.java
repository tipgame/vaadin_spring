package de.tipgame.ui.charts;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.LineChartConfig;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.CategoryScale;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.byteowls.vaadin.chartjs.utils.ColorUtils;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import de.tipgame.app.security.SecurityUtils;
import de.tipgame.backend.data.entity.GameMatchEntity;
import de.tipgame.backend.data.entity.StatisticTimelineEntity;
import de.tipgame.backend.data.entity.UserEntity;
import de.tipgame.backend.data.entity.UserMatchConnectionEntity;
import de.tipgame.backend.service.GameMatchService;
import de.tipgame.backend.service.StatisticTimelineService;
import de.tipgame.backend.service.UserMatchConnectionService;
import de.tipgame.backend.service.UserService;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@UIScope
@SpringView
public class TippgameChart extends AbstractChartView {

    private UserMatchConnectionService userMatchConnectionService;
    private UserService userService;
    private GameMatchService gameMatchService;
    private MultiValuedMap<String, GameMatchEntity> labelToEntityMap;
    private StatisticTimelineService statisticTimelineService;

    public TippgameChart(UserMatchConnectionService userMatchConnectionService,
                         UserService userService,
                         GameMatchService gameMatchService,
                         StatisticTimelineService statisticTimelineService) {
        this.userMatchConnectionService = userMatchConnectionService;
        this.userService = userService;
        this.gameMatchService = gameMatchService;
        this.statisticTimelineService = statisticTimelineService;
    }

    @Override
    public Component getChart() {
        LineChartConfig lineConfig = new LineChartConfig();
        lineConfig.data()
                .labelsAsList(setLabels())
                .addDataset(setUserPointsData(lineConfig))
                .addDataset(setDataForPerfectPoints(lineConfig))
                .and()
                .options()
                .responsive(true)
                .title()
                .display(true)
                .text("Punkteverlauf")
                .and()
                .tooltips()
                .mode(InteractionMode.INDEX)
                .intersect(false)
                .and()
                .hover()
                .mode(InteractionMode.NEAREST)
                .intersect(true)
                .and()
                .scales()
                .add(Axis.X, new CategoryScale()
                        .display(true)
                        .scaleLabel()
                        .display(true)
                        .labelString("Spieltag")
                        .and()
                        .position(Position.BOTTOM))
                .add(Axis.Y, new LinearScale()
                        .display(true)
                        .scaleLabel()
                        .display(true)
                        .labelString("Punkte")
                        .and()
                        .ticks()
                        .and()
                        .position(Position.LEFT))
                .and()
                .done();

        ChartJs chart = new ChartJs(lineConfig);
        chart.setWidth(100, Unit.PERCENTAGE);
        chart.setHeight(30, Unit.PERCENTAGE);
        chart.setJsLoggingEnabled(true);

        return chart;
    }

    private LineDataset setDataForPerfectPoints(LineChartConfig lineChartConfig) {
        LineDataset perfectPointsData = new LineDataset()
                .label("Perfekte Punkteausbeute")
                .fill(false);

        List<String> labels = lineChartConfig.data().getLabels();
        List<Double> data = new ArrayList<>();
        for (String label : labels) {
            Double points = 0D;
            final Collection<GameMatchEntity> gameMatchEntities = labelToEntityMap.get(label);
            for (GameMatchEntity gameMatchEntity : gameMatchEntities) {
                points = points + 6D;
            }
            data.add(points);
        }

        perfectPointsData.dataAsList(data);

        perfectPointsData.borderColor(ColorUtils.randomColor(0.3));
        perfectPointsData.backgroundColor(ColorUtils.randomColor(0.5));

        return perfectPointsData;
    }

    private LineDataset setUserPointsData(LineChartConfig lineChartConfig) {
        LineDataset userPointsData = new LineDataset()
                .label("Deine Punkte")
                .fill(false);
        List<String> labels = lineChartConfig.data().getLabels();
        List<Double> data = new ArrayList<>();

        UserEntity currentUser = SecurityUtils.getCurrentUser(userService);

        for (String label : labels) {
            final Collection<GameMatchEntity> gameMatchEntities = labelToEntityMap.get(label);
            Double points = 0D;
            for(GameMatchEntity gameMatchEntity : gameMatchEntities) {
                StatisticTimelineEntity statisticTimelineByUserIdAndMatchId =
                        statisticTimelineService.getStatisticTimelineByUserIdAndMatchId(currentUser.getId(),
                                gameMatchEntity.getMatchId());
                if (statisticTimelineByUserIdAndMatchId != null) {
                    points = points + statisticTimelineByUserIdAndMatchId.getPoints();
                }
            }
            data.add(points);
        }

        userPointsData.dataAsList(data);
        userPointsData.borderColor(ColorUtils.randomColor(0.3));
        userPointsData.backgroundColor(ColorUtils.randomColor(0.5));
        return userPointsData;
    }

    private List<String> setLabels() {
        labelToEntityMap = new ArrayListValuedHashMap<>();
        UserEntity currentUser = SecurityUtils.getCurrentUser(userService);
        List<UserMatchConnectionEntity> allProcessedTippsFromUser = userMatchConnectionService.getAllProcessedTippsFromUser(currentUser.getId());

        List<GameMatchEntity> allMatchesById = gameMatchService.getAllMatchesById(allProcessedTippsFromUser.stream()
                .map(UserMatchConnectionEntity::getGameMatchId)
                .collect(Collectors.toList()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.YYYY");

        for(GameMatchEntity gameMatchEntity : allMatchesById) {
            labelToEntityMap.put(String.format("%s", gameMatchEntity.getKickOff().format(formatter)), gameMatchEntity);
        }

         return new ArrayList<>(labelToEntityMap.keySet());
    }

}
