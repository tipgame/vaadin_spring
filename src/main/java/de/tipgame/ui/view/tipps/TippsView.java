package de.tipgame.ui.view.tipps;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import de.tipgame.app.security.SecurityUtils;
import de.tipgame.backend.data.entity.UserEntity;
import de.tipgame.backend.service.GameMatchService;
import de.tipgame.backend.service.UserMatchConnectionService;
import de.tipgame.backend.service.UserService;
import de.tipgame.backend.utils.TipgameUtils;
import de.tipgame.ui.navigation.NavigationManager;
import de.tipgame.ui.view.tipps.component.TippsCustomComponent;
import de.tipgame.ui.view.tipps.component.TippsCustomComponentForPrelimGroups;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The tipps view showing statistics about sales and deliveries.
 * <p>
 * Created as a single View class because the logic is so simple that using a
 * pattern like MVP would add much overhead for little gain. If more complexity
 * is added to the class, you should consider splitting out a presenter.
 */
@SpringView
public class TippsView extends TippsViewDesign implements View {

    private NavigationManager navigationManager;
    private final GameMatchService gameMatchService;
    private UserMatchConnectionService userMatchConnectionService;
    private UserService userService;

    @Autowired
    public TippsView(NavigationManager navigationManager,
                     GameMatchService gameMatchService,
                     UserMatchConnectionService userMatchConnectionService,
                     UserService userService) {
        this.gameMatchService = gameMatchService;
        this.navigationManager = navigationManager;
        this.userMatchConnectionService = userMatchConnectionService;
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        setResponsive(true);
        buildTippsLayout();
    }

    private void buildTippsLayout() {
        List<String> rounds = gameMatchService.getDistinctRounds();
        Accordion tippsAccordionBaseLayout = new Accordion();
        for (String round : rounds) {

            if (round.equalsIgnoreCase("Vorrunde")) {
                buildLayoutForPrelimGroups(tippsAccordionBaseLayout);
            } else {
                TippsCustomComponent tippsCustomComponent = new TippsCustomComponent(
                        userMatchConnectionService,
                        gameMatchService,
                        round);
                tippsAccordionBaseLayout.addTab(tippsCustomComponent.init(), round);
            }
        }

        selectFirstEntryOnTippsView(tippsAccordionBaseLayout);
        tippsAccordionBaseLayout.addSelectedTabChangeListener(e -> {
            if (e.getTabSheet().getSelectedTab() instanceof HorizontalLayout) {
                HorizontalLayout vL = (HorizontalLayout) e.getTabSheet().getSelectedTab();
                Grid component = (Grid) vL.getComponent(0);
                component.select(component.getDataCommunicator().fetchItemsWithRange(0, 1).get(0));
            }
        });
        buildAdditionalTippsLayout(tippsAccordionBaseLayout);
        this.addComponent(tippsAccordionBaseLayout);
    }

    private void selectFirstEntryOnTippsView(Accordion tippsAccordionBaseLayout) {
        if(tippsAccordionBaseLayout.getTab(0) != null) {
            HorizontalLayout vL = (HorizontalLayout) tippsAccordionBaseLayout.getTab(0).getComponent();
            Grid component = (Grid) vL.getComponent(0);
            component.select(component.getDataCommunicator().fetchItemsWithRange(0, 1).get(0));
        }
    }

    private void buildLayoutForPrelimGroups(Accordion tippsAccordionBaseLayout) {
        List<String> distinctPrelimGroups = gameMatchService.getDistinctPrelimGroups();

        for (String prelimGroup : distinctPrelimGroups) {
            TippsCustomComponentForPrelimGroups tippsCustomComponentForPrelimGroups = new TippsCustomComponentForPrelimGroups(
                    userMatchConnectionService,
                    gameMatchService,
                    prelimGroup);
            tippsAccordionBaseLayout.addTab(tippsCustomComponentForPrelimGroups.init(), "Gruppe " + prelimGroup);
        }
    }

    private void buildAdditionalTippsLayout(Accordion tippsAccordionBaseLayout) {
        VerticalLayout vL = new VerticalLayout();
        buildAdditionalTippGermany(vL);
        buildAdditionalTippChampionshipWinner(vL);
        tippsAccordionBaseLayout.addTab(vL, "Zusatztipps");
    }

    private void buildAdditionalTippChampionshipWinner(VerticalLayout vL) {
        HorizontalLayout hL = new HorizontalLayout();
        ComboBox<String> championshipWinner = createComboboxTippChampionshipWinner();
        championshipWinner.setCaption("Wer wird Weltmeister?");
        championshipWinner.setEnabled(!TipgameUtils.isTimeToDisable("13.06.2018 23:55"));
        hL.addComponent(championshipWinner);

        Button saveTippChampionshipWinner = new Button("Tipp speichern", VaadinIcons.CHECK);
        saveTippChampionshipWinner.setEnabled(!TipgameUtils.isTimeToDisable("13.06.2018 23:55"));
        saveTippChampionshipWinner.addClickListener(e -> saveAdditionalTippChampionshipWinner(championshipWinner));
        hL.addComponent(saveTippChampionshipWinner);
        hL.setComponentAlignment(saveTippChampionshipWinner, Alignment.MIDDLE_LEFT);
        vL.addComponent(hL);
    }

    private void buildAdditionalTippGermany(VerticalLayout vL) {
        HorizontalLayout hL = new HorizontalLayout();
        ComboBox<String> tippGermany = createComboboxTippGermany();
        tippGermany.setCaption("Wie weit kommt Deutschland?");
        tippGermany.setEnabled(!TipgameUtils.isTimeToDisable("13.06.2018 23:55"));
        hL.addComponent(tippGermany);

        Button saveTippGermany = new Button("Tipp speichern", VaadinIcons.CHECK);
        saveTippGermany.addClickListener(e -> saveAdditionalTippGermany(tippGermany));
        saveTippGermany.setEnabled(!TipgameUtils.isTimeToDisable("13.06.2018 23:55"));
        hL.addComponent(saveTippGermany);
        hL.setComponentAlignment(saveTippGermany, Alignment.MIDDLE_LEFT);
        vL.addComponent(hL);
    }

    private void saveAdditionalTippGermany(ComboBox<String> tippGermany) {
        UserEntity currentUser = SecurityUtils.getCurrentUser(userService);
        if (tippGermany.getSelectedItem().isPresent()) {
            currentUser.setGermanyTipp(tippGermany.getSelectedItem().get());
            userService.saveUser(currentUser);
        }
        new Notification("Zusatztipp gespeichert!",
                "",
                Notification.Type.HUMANIZED_MESSAGE, false)
                .show(Page.getCurrent());
    }

    private void saveAdditionalTippChampionshipWinner(ComboBox<String> championshipWinner) {
        UserEntity currentUser = SecurityUtils.getCurrentUser(userService);
        if (championshipWinner.getSelectedItem().isPresent()) {
            currentUser.setWinnerTipp(championshipWinner.getSelectedItem().get());
            userService.saveUser(currentUser);
        }
        new Notification("Zusatztipp gespeichert!",
                "",
                Notification.Type.HUMANIZED_MESSAGE, false)
                .show(Page.getCurrent());
    }

    private ComboBox<String> createComboboxTippGermany() {
        ComboBox<String> tippGermany = new ComboBox<>();
        tippGermany.setItems(
                "Vorrunde",
                "Achtelfinale",
                "Viertelfinale",
                "Halbfinale",
                "Finale",
                "Weltmeister");
        UserEntity currentUser = SecurityUtils.getCurrentUser(userService);
        if ((currentUser.getGermanyTipp() != null) && (!currentUser.getGermanyTipp().isEmpty()))
            tippGermany.setSelectedItem(currentUser.getGermanyTipp());
        return tippGermany;
    }

    private ComboBox<String> createComboboxTippChampionshipWinner() {
        ComboBox<String> championshipWinner = new ComboBox<>();
        final List<String> teams = Stream.of(
                "Russland",
                "Saudi Arabien",
                "Ägypten",
                "Uruguay",
                "Portugal",
                "Spanien",
                "Marokko",
                "Iran",
                "Frankreich",
                "Australien",
                "Peru",
                "Dänemark",
                "Argentinien",
                "Island",
                "Kroatien",
                "Nigeria",
                "Brasilien",
                "Schweiz",
                "Costa Rica",
                "Serbien",
                "Deutschland",
                "Mexiko",
                "Schweden",
                "Südkorea",
                "Belgien",
                "Panama",
                "Tunesien",
                "England",
                "Polen",
                "Senegal",
                "Kolumbien",
                "Japan").sorted().collect(Collectors.toList());
        championshipWinner.setItems(teams);

        UserEntity currentUser = SecurityUtils.getCurrentUser(userService);
        if ((currentUser.getWinnerTipp() != null) && (!currentUser.getWinnerTipp().isEmpty()))
            championshipWinner.setSelectedItem(currentUser.getWinnerTipp());
        return championshipWinner;
    }
}
