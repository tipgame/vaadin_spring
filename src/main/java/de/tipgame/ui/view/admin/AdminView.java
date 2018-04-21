package de.tipgame.ui.view.admin;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import de.tipgame.backend.data.Role;
import de.tipgame.backend.service.GameMatchService;
import de.tipgame.ui.view.admin.component.AdminViewCustomComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import javax.annotation.PostConstruct;

@Secured(Role.ADMIN)
@SpringView
public class AdminView extends AdminViewDesign implements View {
    private final GameMatchService gameMatchService;

    @Autowired
    public AdminView(GameMatchService gameMatchService) {
        this.gameMatchService = gameMatchService;
    }

    @PostConstruct
    public void init() {
        setResponsive(true);
        createLayout();
    }

    private void createLayout() {
        AdminViewCustomComponent adminViewCustomComponent = new AdminViewCustomComponent(gameMatchService);
        this.addComponent(adminViewCustomComponent.init());
    }

}
