package de.tipgame.ui.view.tipps;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import de.tipgame.backend.data.dtos.User;
import de.tipgame.backend.data.entity.GameMatchEntity;
import de.tipgame.backend.repository.MatchRepository;
import de.tipgame.ui.navigation.NavigationManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        Map<String, List<GameMatchEntity> > gameMatchMap = new HashMap<>();
        Accordion baseAccordionLayout = new Accordion();
        List<GameMatchEntity> matchesPerPrelimGroup = new ArrayList<>();
        String prelimGroup = "";
        VerticalLayout prelimGroupLayout = new VerticalLayout();

        for(GameMatchEntity match : matches) {
            if(!prelimGroup.equalsIgnoreCase(match.getPrelimGroup())) {
                prelimGroupLayout = new VerticalLayout();
                prelimGroup = match.getPrelimGroup();
                gameMatchMap.put(match.getPrelimGroup(), matchesPerPrelimGroup);
                matchesPerPrelimGroup = new ArrayList<>();
            }

            gameMatchMap.get(prelimGroup).add(match);
            baseAccordionLayout.addTab(prelimGroupLayout, "Gruppe: " + match.getPrelimGroup());
        }

        this.addComponent(baseAccordionLayout);


    }

}
