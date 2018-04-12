package de.tipgame.ui.view.home;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import de.tipgame.ui.navigation.NavigationManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView
public class HomeView extends HomeViewDesign implements View {

    private final NavigationManager navigationManager;

    @Autowired
    public HomeView(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    @PostConstruct
    public void init() {
        setResponsive(true);
        Label label = new Label();
        label.setValue("Hello World");
        this.addComponent(label);
    }

}
