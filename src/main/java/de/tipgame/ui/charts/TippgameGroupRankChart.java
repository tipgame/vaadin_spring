package de.tipgame.ui.charts;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.CategoryScale;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.byteowls.vaadin.chartjs.utils.ColorUtils;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import de.tipgame.backend.data.entity.TeamEntity;
import de.tipgame.backend.service.TeamService;

import java.util.Collections;
import java.util.List;


@UIScope
@SpringView
public class TippgameGroupRankChart extends AbstractChartView {
    private TeamService teamService;

    public TippgameGroupRankChart(TeamService teamService) {
        this.teamService = teamService;
    }

    @Override
    public Component getChart() {
        BarChartConfig barChartConfig = new BarChartConfig();
        barChartConfig = addTeamRankData(barChartConfig);
        barChartConfig.data()
                .and()
                .options()
                .responsive(true)
                .title()
                .display(true)
                .text("Punkte der Teams")
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
                        .labelString("Team")
                        .and()
                        .position(Position.BOTTOM))
                .add(Axis.Y, new LinearScale()
                        .display(true)
                        .scaleLabel()
                        .display(true)
                        .labelString("Punkte")
                        .and()
                        .ticks()
                        .suggestedMin(0)
                        .and()
                        .position(Position.LEFT))
                .and()
                .done();

        ChartJs chart = new ChartJs(barChartConfig);
        chart.setWidth(100, Unit.PERCENTAGE);
        chart.setHeight(30, Unit.PERCENTAGE);
        chart.setJsLoggingEnabled(true);

        return chart;
    }


    private BarChartConfig addTeamRankData(BarChartConfig barChartConfig) {
        List<TeamEntity> allTeamsOrderdByPointsDesc = teamService.getAllTeamsOrderdByPointsDesc();

        for (TeamEntity team : allTeamsOrderdByPointsDesc) {
            if(team.getPoints() != null) {
                barChartConfig.data()
                        .addDataset(
                                new BarDataset().backgroundColor(
                                        ColorUtils.randomColor(0.7),
                                        ColorUtils.randomColor(0.7),
                                        ColorUtils.randomColor(0.7),
                                        ColorUtils.randomColor(0.7),
                                        ColorUtils.randomColor(0.7),
                                        ColorUtils.randomColor(0.7))
                                        .label(team.getTeamName())
                                        .addData(Math.round(team.getPoints() * 100) / 100.00));
            }
        }
        barChartConfig.data().labelsAsList(Collections.singletonList(""));
        return barChartConfig;
    }
}
