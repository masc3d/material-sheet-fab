package org.deku.leoz.node.web.ui

import com.vaadin.annotations.Theme
import com.vaadin.annotations.Title
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.server.ThemeResource
import com.vaadin.server.VaadinRequest
import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.spring.annotation.SpringUI
import com.vaadin.ui.*
import org.deku.leoz.node.data.jpa.MstStation
import org.deku.leoz.node.data.jpa.QMstStation
import org.deku.leoz.node.data.repository.master.StationRepository
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Main UI
 */
@Theme("deku")
@SpringUI(path = "/")
class MainUI : UI() {
    @Override
    override fun init(request: VaadinRequest) {
        content = Label("DEKU UI.")
    }
}


/**
 * Depots UI
 */
@Title("Depots")
@Theme("deku")
@SpringUI(path = "/depots/") class DepotsUI : UI() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var stationRepository: StationRepository

    /** Filter text field */
    val filter = TextField()
    val grid = Grid<MstStation>(MstStation::class.java)

    @Override
    override fun init(request: VaadinRequest) {
        configureComponents()

        // Build layout
        val title = Label("Depots")
        title.setSizeUndefined()
        title.styleName = "title"
        val logo = Image("", ThemeResource("img/logo.png"))
        val titleBar = HorizontalLayout(logo, title)
        titleBar.styleName = "title-bar"
        titleBar.defaultComponentAlignment = Alignment.MIDDLE_CENTER
        titleBar.setComponentAlignment(title, Alignment.MIDDLE_CENTER)

        val actionBarLayout = HorizontalLayout(filter)
        actionBarLayout.setWidth("100%")
        filter.setWidth("100%")
        actionBarLayout.setExpandRatio(filter, 1.0F)

        val main = VerticalLayout(titleBar, actionBarLayout, grid)
        main.setSizeFull()
        grid.setSizeFull()
        main.setExpandRatio(grid, 1.0F)

        val hLayout = HorizontalLayout(main)
        hLayout.setSizeFull()
        hLayout.setExpandRatio(main, 1.0F)

        val mainLayout = CustomLayout("main")
        mainLayout.setSizeFull()

        val mainPanel = Panel("main-panel")
        mainPanel.content = mainLayout
        mainLayout.addComponent(hLayout, "main-container")

        // Split and allow resizing
        content = hLayout
    }

    private fun configureComponents() {
        filter.valueChangeMode = ValueChangeMode.EAGER
        filter.placeholder = "Filter depots.."
        filter.addValueChangeListener({ e -> refresh(e.value) })

        val qStation = QMstStation.mstStation
        grid.dataProvider = ListDataProvider<MstStation>(listOf())
        grid.setColumnOrder(
                qStation.stationNr.metadata.name,
                qStation.address1.metadata.name,
                qStation.address2.metadata.name,
                qStation.sector.metadata.name,
                qStation.street.metadata.name,
                qStation.houseNr.metadata.name,
                qStation.city.metadata.name,
                qStation.country.metadata.name,
                qStation.servicePhone1.metadata.name,
                qStation.servicePhone2.metadata.name,
                qStation.contactPerson1.metadata.name,
                qStation.contactPerson2.metadata.name)
        grid.removeColumn(qStation.billingAddress1.metadata.name)
        grid.removeColumn(qStation.billingAddress2.metadata.name)
        grid.removeColumn(qStation.billingCity.metadata.name)
        grid.removeColumn(qStation.billingCountry.metadata.name)
        grid.removeColumn(qStation.billingHouseNr.metadata.name)
        grid.removeColumn(qStation.billingStreet.metadata.name)
        grid.removeColumn(qStation.billingZip.metadata.name)
        grid.removeColumn(qStation.posLat.metadata.name)
        grid.removeColumn(qStation.posLong.metadata.name)
        grid.removeColumn(qStation.strang.metadata.name)
        grid.removeColumn(qStation.ustid.metadata.name)

        grid.setSelectionMode(Grid.SelectionMode.SINGLE)
        refresh()
    }

    fun refresh() {
        refresh(filter.value)
    }

    private fun refresh(stringFilter: String) {
        grid.dataProvider = ListDataProvider<MstStation>(stationRepository.findWithQuery(stringFilter))
    }
}
