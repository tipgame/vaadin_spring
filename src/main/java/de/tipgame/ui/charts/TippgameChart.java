package de.tipgame.ui.charts;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.LineChartConfig;
import com.byteowls.vaadin.chartjs.data.Dataset;
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
import de.tipgame.backend.data.entity.UserEntity;
import de.tipgame.backend.data.entity.UserMatchConnectionEntity;
import de.tipgame.backend.service.GameMatchService;
import de.tipgame.backend.service.UserMatchConnectionService;
import de.tipgame.backend.service.UserService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UIScope
@SpringView
public class TippgameChart extends AbstractChartView {

    private UserMatchConnectionService userMatchConnectionService;
    private UserService userService;
    private GameMatchService gameMatchService;
    private Map<String, GameMatchEntity> labelToEntityMap;

    public TippgameChart(UserMatchConnectionService userMatchConnectionService,
                         UserService userService,
                         GameMatchService gameMatchService) {
        this.userMatchConnectionService = userMatchConnectionService;
        this.userService = userService;
        this.gameMatchService = gameMatchService;
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
                        .labelString("Spiele")
                        .and()
                        .position(Position.TOP))
                .add(Axis.Y, new LinearScale()
                        .display(true)
                        .scaleLabel()
                        .display(true)
                        .labelString("Punkte")
                        .and()
                        .ticks()
                        .suggestedMin(0)
                        .suggestedMax(250)
                        .and()
                        .position(Position.RIGHT))
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
        for(String ignored : labels) {
            data.add(6D);
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
        data.add(2D);
        data.add(4D);
        data.add(2D);
        data.add(6D);

        userPointsData.dataAsList(data);
        userPointsData.borderColor(ColorUtils.randomColor(0.3));
        userPointsData.backgroundColor(ColorUtils.randomColor(0.5));
        return userPointsData;
    }

    private List<String> setLabels() {
        labelToEntityMap = new HashMap<>();
        UserEntity currentUser = SecurityUtils.getCurrentUser(userService);
        List<UserMatchConnectionEntity> allProcessedTippsFromUser = userMatchConnectionService.getAllProcessedTippsFromUser(currentUser.getId());

        List<GameMatchEntity> allMatchesById = gameMatchService.getAllMatchesById(allProcessedTippsFromUser.stream()
                .map(UserMatchConnectionEntity::getGameMatchId)
                .collect(Collectors.toList()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return allMatchesById.stream()
                .map(e -> {
                    String label = String.format("%s %s:%s", e.getKickOff().format(formatter),
                            e.getHomeTeamShortName(),
                            e.getAwayTeamShortName());
                    labelToEntityMap.put(label, e);
                    return label;
                })
                .collect(Collectors.toList());
    }

}
