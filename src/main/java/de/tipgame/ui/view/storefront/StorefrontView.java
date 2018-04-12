package de.tipgame.ui.view.storefront;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import de.tipgame.ui.navigation.NavigationManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView
public class StorefrontView extends de.tipgame.ui.view.storefront.StorefrontViewDesign implements View {

    private static final String PARAMETER_SEARCH = "search";

    private static final String PARAMETER_INCLUDE_PAST = "includePast";

    private final NavigationManager navigationManager;

    @Autowired
    public StorefrontView(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    @PostConstruct
    public void init() {

    }

}
