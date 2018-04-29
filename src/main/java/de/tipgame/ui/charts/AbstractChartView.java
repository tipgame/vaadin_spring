package de.tipgame.ui.charts;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;

public abstract class AbstractChartView extends VerticalLayout implements ChartView {

    @PostConstruct
    public void postConstruct() {
        setSizeFull();
        setMargin(true);
        Component layout = getChart();
        addComponent(layout);
        setComponentAlignment(layout, Alignment.TOP_CENTER);
    }

    public void enter(ViewChangeEvent event) {

    }

}