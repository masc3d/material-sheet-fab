package org.deku.leoz.node.web.ui

import com.vaadin.annotations.Theme
import com.vaadin.annotations.Title
import com.vaadin.data.util.BeanItemContainer
import com.vaadin.server.ThemeResource
import com.vaadin.server.VaadinRequest
import com.vaadin.spring.annotation.SpringUI
import com.vaadin.ui.*
import org.apache.commons.logging.LogFactory
import org.deku.leoz.node.data.entities.master.Station
import org.deku.leoz.node.data.repositories.master.StationRepository
import javax.inject.Inject

/**
 * Main UI
 */
@Theme("deku")
@SpringUI(path = "/")
class MainUI : UI() {
    @Override
    override protected fun init(request: VaadinRequest) {
        setContent(Label("DEKU UI."));
    }
}


/**
 * Depots UI
 */
@Title("Depots")
@Theme("deku")
@SpringUI(path = "/depots/")
public class DepotsUI : UI() {
    private val log = LogFactory.getLog(this.javaClass)

    @Inject
    private lateinit var stationRepository: StationRepository

    /** Filter text field */
    val filter = TextField()
    val grid = Grid()

    @Override
    override protected fun init(request: VaadinRequest) {
        configureComponents();

        // Build layout
        val title = Label("Depots")
        title.setSizeUndefined()
        title.styleName = "title"
        val logo = Image("", ThemeResource("img/logo.png"))
        val titleBar = HorizontalLayout(logo, title)
        titleBar.styleName = "title-bar"
        titleBar.defaultComponentAlignment = Alignment.MIDDLE_CENTER
        titleBar.setComponentAlignment(title, Alignment.MIDDLE_CENTER)

        val actionBarLayout = HorizontalLayout(filter);
        actionBarLayout.setWidth("100%");
        filter.setWidth("100%");
        actionBarLayout.setExpandRatio(filter, 1.0F);

        val main = VerticalLayout(titleBar, actionBarLayout, grid);
        main.setSizeFull();
        grid.setSizeFull();
        main.setExpandRatio(grid, 1.0F);

        val hLayout = HorizontalLayout(main)
        hLayout.setSizeFull();
        hLayout.setExpandRatio(main, 1.0F);

        val mainLayout = CustomLayout("main")
        mainLayout.setSizeFull()

        val mainPanel = Panel("main-panel")
        mainPanel.content = mainLayout
        mainLayout.addComponent(hLayout, "main-container")

        // Split and allow resizing
        setContent(hLayout);
    }

    private fun configureComponents() {
        filter.textChangeEventMode = AbstractTextField.TextChangeEventMode.EAGER
        filter.setInputPrompt("Filter depots..");
        filter.addTextChangeListener({ e -> refresh(e.getText()) })

        grid.setContainerDataSource(BeanItemContainer(Station::class.java));
        grid.setColumnOrder(
                "stationNr",
                "address1",
                "address2",
                "sector",
                "street",
                "houseNr",
                "zip",
                "city",
                "country",
                "servicePhone1",
                "servicePhone2",
                "contactPerson1",
                "contactPerson2");
        grid.removeColumn("billingAddress1")
        grid.removeColumn("billingAddress2")
        grid.removeColumn("billingCity")
        grid.removeColumn("billingCountry")
        grid.removeColumn("billingHouseNr")
        grid.removeColumn("billingStreet")
        grid.removeColumn("billingZip")
        grid.removeColumn("posLat")
        grid.removeColumn("posLong")
        grid.removeColumn("strang")
        grid.removeColumn("uStId")

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        refresh();
    }

    fun refresh() {
        refresh(filter.getValue());
    }

    private fun refresh(stringFilter: String) {
        grid.setContainerDataSource(BeanItemContainer(
                Station::class.java, stationRepository.findWithQuery(stringFilter)));
    }
}
