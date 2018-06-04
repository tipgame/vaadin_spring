package de.tipgame.ui.charts;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.elements.Rectangle;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import de.tipgame.backend.data.entity.TeamEntity;
import de.tipgame.backend.service.TeamService;

import java.util.ArrayList;
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
        barChartConfig.horizontal();
        barChartConfig = addTeamRankData(barChartConfig);
        barChartConfig
                .data()
                .and()
                .options()
                .responsive(true)
                .title()
                .display(true)
                .text("Punkte der Teams")
                .and()
                .scales()
                .add(Axis.X, new LinearScale()
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
                .elements()
                .rectangle()
                .borderWidth(0)
                .borderColor("rgb(0, 255, 0)")
                .borderSkipped(Rectangle.RectangleEdge.LEFT)
                .and()
                .and()
                .legend()
                .fullWidth(false)
                .position(Position.LEFT)
                .and()
                .done();

        ChartJs chart = new ChartJs(barChartConfig);
        chart.setWidth(100, Unit.PERCENTAGE);
        chart.setHeight(30, Unit.PERCENTAGE);
        chart.setJsLoggingEnabled(true);

        return chart;
    }


    private BarChartConfig addTeamRankData(BarChartConfig barChartConfig) {
        List<String> teamColor = new ArrayList<>();
        teamColor.add("rgb(209, 12, 12)");
        teamColor.add("rgb(209, 153, 11)");
        teamColor.add("rgb(176, 209, 11)");
        teamColor.add("rgb(47, 209, 11)");
        teamColor.add("rgb(11, 209, 189)");
        teamColor.add("rgb(11, 110, 209)");
        teamColor.add("rgb(83, 11, 209)");
        teamColor.add("rgb(209, 11, 209)");
        List<TeamEntity> allTeamsOrderedByPointsDesc = teamService.getAllTeamsOrderdByPointsDesc();

        Integer index = 0;
        for (TeamEntity team : allTeamsOrderedByPointsDesc) {
            if (team.getPoints() != null) {
                barChartConfig.data()
                        .addDataset(
                                new BarDataset().backgroundColor(teamColor.get(index))
                                        .borderColor(teamColor.get(index))
                                        .hoverBackgroundColor(teamColor.get(index))
                                        .hoverBorderColor(teamColor.get(index))
                                        .label(team.getTeamName())
                                        .addData(Math.round(team.getPoints() * 100) / 100.00));
                if(index < allTeamsOrderedByPointsDesc.size())
                    index += 1;
                else
                    index = 0;
            }
        }
        barChartConfig.data().labelsAsList(Collections.singletonList(""));
        return barChartConfig;
    }
}
