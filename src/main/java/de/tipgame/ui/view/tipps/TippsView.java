package de.tipgame.ui.view.tipps;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import de.tipgame.backend.data.dtos.GameMatchDto;
import de.tipgame.backend.data.dtos.User;
import de.tipgame.backend.data.entity.GameMatchEntity;
import de.tipgame.backend.repository.MatchRepository;
import de.tipgame.ui.navigation.NavigationManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

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
    private MatchRepository matchRepository;

    @Autowired
	public TippsView(NavigationManager navigationManager, MatchRepository matchRepository) {

	    this.navigationManager = navigationManager;
        this.matchRepository = matchRepository;
    }

	@PostConstruct
	public void init() {
		setResponsive(true);
		buildTippsAccordionLayout();
	}

	private void buildTippsAccordionLayout() {
        List<GameMatchEntity> matches = matchRepository.findAllByOrderByPrelimGroupAscKickOffAsc();

        Accordion baseAccordionLayout = new Accordion();
        List<GameMatchDto> matchesPerPrelimGroup = new ArrayList<>();
        String prelimGroup = "";
        HorizontalLayout prelimGroupLayout = new HorizontalLayout();

        for(GameMatchEntity match : matches) {

            if(!prelimGroup.equalsIgnoreCase(match.getPrelimGroup())) {

                if(!matchesPerPrelimGroup.isEmpty()) {
                    getBasicTippLayout(baseAccordionLayout, matchesPerPrelimGroup, prelimGroup, prelimGroupLayout);
                }
                prelimGroupLayout = new HorizontalLayout();
                prelimGroup = match.getPrelimGroup();
                matchesPerPrelimGroup = new ArrayList<>();
            }

            matchesPerPrelimGroup.add(buildGameMatchDto(match));
        }

        getBasicTippLayout(baseAccordionLayout, matchesPerPrelimGroup, prelimGroup, prelimGroupLayout);

        this.addComponent(baseAccordionLayout);

    }

    private void getBasicTippLayout(Accordion baseAccordionLayout,
                                    List<GameMatchDto> matchesPerPrelimGroup,
                                    String prelimGroup,
                                    HorizontalLayout prelimGroupLayout) {
        baseAccordionLayout.addTab(prelimGroupLayout, "Gruppe: " + prelimGroup);
        Grid grid = addGridToGroupLayout(matchesPerPrelimGroup);
        prelimGroupLayout.addComponent(grid);
        prelimGroupLayout.setExpandRatio(grid, 70);
        VerticalLayout tippForm = getTippForm();
        prelimGroupLayout.addComponent(tippForm);
        prelimGroupLayout.setExpandRatio(tippForm, 30);
    }

    private VerticalLayout getTippForm() {
        VerticalLayout verticalLayout = new VerticalLayout();

        FormLayout formLayout = new FormLayout();
        formLayout.addComponent(new TextField());
        formLayout.addComponent(new TextField());
        formLayout.addComponent(new Button());

        verticalLayout.addComponent(formLayout);

        return verticalLayout;
    }

    private GameMatchDto buildGameMatchDto(GameMatchEntity match) {
        GameMatchDto gameMatchDto = new GameMatchDto();
        gameMatchDto.setFixture(match.getHomeTeamShortName()+ " : " + match.getAwayTeamShortName());
        gameMatchDto.setKickOff(match.getKickOff().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(new Locale("de"))));
        gameMatchDto.setTipp(":");
        return gameMatchDto;
    }

    private Grid addGridToGroupLayout(List<GameMatchDto> machtes) {
        Grid<GameMatchDto> grid = new Grid<>();
        grid.setWidth(100, Unit.PERCENTAGE);
        grid.setHeight(100, Unit.PERCENTAGE);
        grid.setItems(machtes);
        grid.addColumn(GameMatchDto::getKickOff).setCaption("Anpfiff");
        grid.addColumn(GameMatchDto::getFixture).setCaption("Begegnung");
        grid.addColumn(GameMatchDto::getTipp).setCaption("Dein Tipp");

        return grid;
    }

}
