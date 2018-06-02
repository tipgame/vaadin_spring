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

        ThemeResource resource = new ThemeResource("images/Statistic_1.png");
        Image image = new Image("Platzierungen", resource);
        vL.addComponent(image);

        ThemeResource resource1 = new ThemeResource("images/Statistic_2.png");
        Image image1 = new Image("Top 10 Platzierungen", resource1);
        vL.addComponent(image1);

        ThemeResource resource2 = new ThemeResource("images/Statistic_3.png");
        Image image2 = new Image("Punktestatistik", resource2);
        vL.addComponent(image2);

        ThemeResource resource3 = new ThemeResource("images/Statistic_4.png");
        Image image3 = new Image("Teamstatistik", resource3);
        vL.addComponent(image3);

        this.addComponentsAndExpand(vL);
    }
}
