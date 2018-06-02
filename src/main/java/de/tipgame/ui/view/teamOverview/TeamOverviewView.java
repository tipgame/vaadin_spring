package de.tipgame.ui.view.teamOverview;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
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
    }
}
