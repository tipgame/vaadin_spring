package de.tipgame.ui.view.tipps;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import de.tipgame.ui.navigation.NavigationManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

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

	@Autowired
	public TippsView(NavigationManager navigationManager) {
		this.navigationManager = navigationManager;
	}

	@PostConstruct
	public void init() {
		setResponsive(true);

	}



	@Override
	public void enter(ViewChangeEvent event) {

	}


}
