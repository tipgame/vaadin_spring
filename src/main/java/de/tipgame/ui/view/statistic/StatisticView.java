package de.tipgame.ui.view.statistic;

import com.vaadin.navigator.View;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;

@SpringView
public class StatisticView extends StatisticViewDesign implements View {

    @PostConstruct
    public void init() {
        setResponsive(true);
        this.setSizeFull();
        VerticalLayout vL = new VerticalLayout();

        ThemeResource resource = new ThemeResource("images/1.png");
        Image image = new Image("<h2>Platzierungen seit 1998</h2>", resource);
        image.setCaptionAsHtml(true);
        vL.addComponents(image);

        resource = new ThemeResource("images/2.png");
        image = new Image("", resource);
        vL.addComponents(image);

        resource = new ThemeResource("images/3.png");
        image = new Image("", resource);
        vL.addComponents(image);

        resource = new ThemeResource("images/4.png");
        image = new Image("", resource);
        vL.addComponents(image);

        resource = new ThemeResource("images/5.png");
        image = new Image("<h2>Top 10 Platzierungen</h2>", resource);
        image.setCaptionAsHtml(true);
        vL.addComponents(image);

        resource = new ThemeResource("images/6.png");
        image = new Image("", resource);
        vL.addComponents(image);

        resource = new ThemeResource("images/7.png");
        image = new Image("<h2>Aktuelle Weltrangliste</h2>", resource);
        image.setCaptionAsHtml(true);
        vL.addComponents(image);

        resource = new ThemeResource("images/8.png");
        image = new Image("", resource);
        vL.addComponents(image);

        resource = new ThemeResource("images/9.png");
        image = new Image("<h2>Teamstatistik 2016</h2>", resource);
        image.setCaptionAsHtml(true);
        vL.addComponents(image);

        this.addComponents(vL);
    }
}
