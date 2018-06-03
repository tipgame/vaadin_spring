package de.tipgame.ui.view.tipps;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import de.tipgame.app.security.SecurityUtils;
import de.tipgame.backend.data.entity.UserEntity;
import de.tipgame.backend.service.DisableElementsService;
import de.tipgame.backend.service.GameMatchService;
import de.tipgame.backend.service.UserMatchConnectionService;
import de.tipgame.backend.service.UserService;
import de.tipgame.ui.navigation.NavigationManager;
import de.tipgame.ui.view.tipps.component.TippsCustomComponent;
import de.tipgame.ui.view.tipps.component.TippsCustomComponentForPrelimGroups;
import de.tipgame.ui.view.tipps.component.TippsEditor;
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

    private final GameMatchService gameMatchService;
    private UserMatchConnectionService userMatchConnectionService;
    private UserService userService;
    private Accordion tippsAccordionGroupPhase = new Accordion();
    private Accordion tippsAccordionKOPhase = new Accordion();
    private DisableElementsService disableElementsService;

    @Autowired
    public TippsView(NavigationManager navigationManager,
                     GameMatchService gameMatchService,
                     UserMatchConnectionService userMatchConnectionService,
                     UserService userService,
                     DisableElementsService disableElementsService) {
        this.gameMatchService = gameMatchService;
        this.userMatchConnectionService = userMatchConnectionService;
        this.userService = userService;
        this.disableElementsService = disableElementsService;
    }

    @PostConstruct
    public void init() {
        setResponsive(true);
        VerticalLayout verticalBaseLayout = new VerticalLayout();
        buildTippsLayout();
        tippsAccordionGroupPhase.setCaption("Gruppenphase");
        tippsAccordionKOPhase.setCaption("KO-Runde");
        verticalBaseLayout.addComponents(tippsAccordionGroupPhase);
        verticalBaseLayout.addComponent(tippsAccordionKOPhase);
        Accordion additionalTippsAccordionLayout = new Accordion();
        additionalTippsAccordionLayout.setCaption("Zusätzliche Tipps");
        buildAdditionalTippsLayout(additionalTippsAccordionLayout);
        verticalBaseLayout.addComponent(additionalTippsAccordionLayout);
        this.addComponent(verticalBaseLayout);
    }

    private void buildTippsLayout() {
        List<String> rounds = gameMatchService.getDistinctRounds();
        for (String round : rounds) {

            if (round.equalsIgnoreCase("Vorrunde")) {
                buildLayoutForPrelimGroups(tippsAccordionGroupPhase);
            } else {
                TippsCustomComponent tippsCustomComponent = new TippsCustomComponent(
                        userMatchConnectionService,
                        gameMatchService,
                        round);
                tippsAccordionKOPhase.addTab(tippsCustomComponent.init(), round);
            }
        }

        selectFirstEntryOnTippsView(tippsAccordionGroupPhase);
        selectFirstEntryOnTippsView(tippsAccordionKOPhase);

        tippsAccordionGroupPhase.addSelectedTabChangeListener(this::getEditorToEditTipp);
        tippsAccordionKOPhase.addSelectedTabChangeListener(this::getEditorToEditTipp);
    }

    private void getEditorToEditTipp(TabSheet.SelectedTabChangeEvent e) {
        VerticalLayout vL = (VerticalLayout) e.getTabSheet().getSelectedTab();
        if(vL.getComponent(0) instanceof  Grid) {
            Grid grid = (Grid) vL.getComponent(0);
            try {
                grid.select(grid.getDataCommunicator().fetchItemsWithRange(0, 1).get(0));
            } catch (Exception e1)
            {
                if(vL.getComponent(1) instanceof TippsEditor) {
                    TippsEditor tippsEditor = (TippsEditor) vL.getComponent(1);
                    tippsEditor.editTipp(null);
                }
            }

        }
    }

    private void selectFirstEntryOnTippsView(Accordion tippsAccordionLayout) {
        if (tippsAccordionLayout.getTab(0) != null) {
            VerticalLayout vL = (VerticalLayout) tippsAccordionLayout.getTab(0).getComponent();
            Grid component = (Grid) vL.getComponent(0);
            component.select(component.getDataCommunicator().fetchItemsWithRange(0, 1).get(0));
        }
    }

    private void buildLayoutForPrelimGroups(Accordion tippsAccordionLayout) {
        List<String> distinctPrelimGroups = gameMatchService.getDistinctPrelimGroups();

        for (String prelimGroup : distinctPrelimGroups) {
            TippsCustomComponentForPrelimGroups tippsCustomComponentForPrelimGroups = new TippsCustomComponentForPrelimGroups(
                    userMatchConnectionService,
                    gameMatchService,
                    prelimGroup);
            tippsAccordionLayout.addTab(tippsCustomComponentForPrelimGroups.init(), "Gruppe " + prelimGroup);
        }
    }

    private void buildAdditionalTippsLayout(Accordion tippsAccordionLayout) {
        VerticalLayout vL = new VerticalLayout();
        buildAdditionalTippGermany(vL);
        buildAdditionalTippChampionshipWinner(vL);
        tippsAccordionLayout.addTab(vL, "Zusatztipps");
    }

    private void buildAdditionalTippChampionshipWinner(VerticalLayout vL) {

        ComboBox<String> championshipWinner = createComboboxTippChampionshipWinner();
        championshipWinner.setCaption("Wer wird Weltmeister?");
        championshipWinner.setEnabled(!disableElementsService.isTimeToDisableElement("tippChampionshipWinner"));

        Button saveTippChampionshipWinner = new Button("Tipp speichern", VaadinIcons.CHECK);
        saveTippChampionshipWinner.setEnabled(!disableElementsService.isTimeToDisableElement("tippChampionshipWinner"));
        saveTippChampionshipWinner.addClickListener(e -> saveAdditionalTippChampionshipWinner(championshipWinner));

        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        ResponsiveRow row = responsiveLayout.addRow();

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.addComponent(championshipWinner);
        verticalLayout.setMargin(false);

        row
                .addColumn()
                .withDisplayRules(12, 5, 4, 3)
                .withComponent(verticalLayout);

        verticalLayout = new VerticalLayout();
        verticalLayout.addComponent(saveTippChampionshipWinner);
        verticalLayout.setComponentAlignment(saveTippChampionshipWinner, Alignment.BOTTOM_LEFT);
        verticalLayout.setMargin(false);
        row
                .addColumn()
                .withDisplayRules(12, 1, 1, 1)
                .withComponent(verticalLayout);

        vL.addComponent(responsiveLayout);
    }

    private void buildAdditionalTippGermany(VerticalLayout vL) {

        ComboBox<String> tippGermany = createComboboxTippGermany();
        tippGermany.setCaption("Wie weit kommt Deutschland?");
        tippGermany.setEnabled(!disableElementsService.isTimeToDisableElement("tippGermany"));

        Button saveTippGermany = new Button("Tipp speichern", VaadinIcons.CHECK);
        saveTippGermany.addClickListener(e -> saveAdditionalTippGermany(tippGermany));
        saveTippGermany.setEnabled(!disableElementsService.isTimeToDisableElement("tippGermany"));

        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        ResponsiveRow row = responsiveLayout.addRow();

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.addComponent(tippGermany);
        verticalLayout.setMargin(false);

        row
                .addColumn()
                .withDisplayRules(12, 5, 4, 3)
                .withComponent(verticalLayout);

        verticalLayout = new VerticalLayout();
        verticalLayout.addComponent(saveTippGermany);
        verticalLayout.setComponentAlignment(saveTippGermany, Alignment.BOTTOM_LEFT);
        verticalLayout.setMargin(false);
        row
                .addColumn()
                .withDisplayRules(12, 1, 1, 1)
                .withComponent(verticalLayout);

        vL.addComponent(responsiveLayout);
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
