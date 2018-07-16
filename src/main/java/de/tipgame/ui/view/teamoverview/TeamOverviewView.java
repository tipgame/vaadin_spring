package de.tipgame.ui.view.teamoverview;

import com.vaadin.navigator.View;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView
public class TeamOverviewView extends TeamOverviewViewDesign implements View {

    @Autowired
    public TeamOverviewView() {
    }

    @PostConstruct
    public void init() {
        setResponsive(true);
        this.setSizeFull();

        VerticalLayout vL = new VerticalLayout();

        ThemeResource resource = new ThemeResource("images/team_1.png");
        Image image = new Image("<h2>Teams 2018</h2>", resource);
        image.setCaptionAsHtml(true);
        vL.addComponents(image);

        resource = new ThemeResource("images/team_2.png");
        image = new Image("", resource);
        vL.addComponents(image);

        this.addComponent(vL);

    }
}
