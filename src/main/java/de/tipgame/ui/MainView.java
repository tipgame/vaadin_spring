package de.tipgame.ui;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewLeaveAction;
import com.vaadin.spring.access.SecuredViewAccessControl;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import de.tipgame.ui.navigation.NavigationManager;
import de.tipgame.ui.view.admin.AdminView;
import de.tipgame.ui.view.home.HomeView;
import de.tipgame.ui.view.tipps.TippsView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


@SpringViewDisplay
@UIScope
public class MainView extends MainViewDesign implements ViewDisplay {

    private final Map<Class<? extends View>, Button> navigationButtons = new HashMap<>();
    private final NavigationManager navigationManager;
    private final SecuredViewAccessControl viewAccessControl;

    @Autowired
    public MainView(NavigationManager navigationManager, SecuredViewAccessControl viewAccessControl) {
        this.navigationManager = navigationManager;
        this.viewAccessControl = viewAccessControl;
    }

    @PostConstruct
    public void init() {
        attachNavigation(home, HomeView.class);
        attachNavigation(tipps, TippsView.class);
        attachNavigation(admin, AdminView.class);

        logout.addClickListener(e -> logout());
    }

    private void attachNavigation(Button navigationButton, Class<? extends View> targetView) {
        boolean hasAccessToView = viewAccessControl.isAccessGranted(targetView);
        navigationButton.setVisible(hasAccessToView);

        if (hasAccessToView) {
            navigationButtons.put(targetView, navigationButton);
            navigationButton.addClickListener(e -> navigationManager.navigateTo(targetView));
        }
    }

    @Override
    public void showView(View view) {
        content.removeAllComponents();
        content.addComponent(view.getViewComponent());

        navigationButtons.forEach((viewClass, button) -> button.setStyleName("selected", viewClass == view.getClass()));

        Button menuItem = navigationButtons.get(view.getClass());
        String viewName = "";
        if (menuItem != null) {
            viewName = menuItem.getCaption();
        }
        activeViewName.setValue(viewName);
    }

    /**
     * Logs the user out after ensuring the currently open view has no unsaved
     * changes.
     */
    public void logout() {
        ViewLeaveAction doLogout = () -> {
            UI ui = getUI();
            ui.getSession().getSession().invalidate();
            ui.getPage().reload();
        };

        navigationManager.runAfterLeaveConfirmation(doLogout);
    }

}
