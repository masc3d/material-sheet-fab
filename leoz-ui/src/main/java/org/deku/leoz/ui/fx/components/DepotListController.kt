package org.deku.leoz.ui.fx.components

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.instanceOrNull
import com.github.salomonbrys.kodein.lazy
import com.sun.javafx.collections.ImmutableObservableList
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.input.MouseEvent
import javafx.util.Callback
import org.deku.leoz.ui.bridge.LeoBridge
import org.deku.leoz.ui.bridge.MessageFactory
import org.deku.leoz.ui.fx.Controller
import org.deku.leoz.rest.entities.internal.v1.Station
import org.deku.leoz.ui.*
import org.deku.leoz.ui.event.BusyNotifier
import org.deku.leoz.ui.event.BusyNotifier.*
import org.deku.leoz.ui.event.ErrorNotifier
import org.deku.leoz.ui.event.Event
import org.deku.leoz.ui.event.busy
import org.slf4j.LoggerFactory
import rx.Observable
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.synchronized
import rx.subjects.PublishSubject
import rx.subjects.Subject
import java.net.URL
import java.util.*
import java.util.concurrent.Executors

/**
 * Created by masc on 22.09.14.
 */
class DepotListController : Controller(), Initializable, BusyNotifier, ErrorNotifier {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @FXML
    private lateinit var fxSearchText: TextField
    @FXML
    private lateinit var fxDepotTableView: TableView<Station>
    @FXML
    private lateinit var fxDepotTableMatchcodeColumn: TableColumn<Any, Any>
    @FXML
    private lateinit var fxDepotTableCompany1Column: TableColumn<Any, Any>
    @FXML
    private lateinit var fxDepotTableCompany2Column: TableColumn<Any, Any>
    @FXML
    private lateinit var fxDepotTableCountryColumn: TableColumn<Any, Any>
    @FXML
    private lateinit var fxDepotTableZipCodeColumn: TableColumn<Any, Any>
    @FXML
    private lateinit var fxDepotTableCityColumn: TableColumn<Any, Any>
    @FXML
    private lateinit var fxDepotTableStreetColumn: TableColumn<Any, Any>

    /** LeoBridge instance */
    private val leoBridge: LeoBridge by Kodein.global.lazy.instance()

    private var stations: ObservableList<Station> = ImmutableObservableList()

    private val queryTaskExecutor = Executors.newFixedThreadPool(3)
    private var queryTask: Task<ObservableList<Station>>? = null
    private var requestedDepotId: Int? = null

    // BusyNotifier implementation
    override val ovBusy by lazy { PublishSubject<Event<Boolean>>() }

    // ErrorNotifier implementation
    override val ovError by lazy { PublishSubject<Exception>() }

    // On item selected event
    val ovItemSelected by lazy { PublishSubject<Station>() }

    /**
     * Query task
     */
    private inner class QueryTask : Task<ObservableList<Station>>() {
        @Throws(Exception::class)
        override fun call(): ObservableList<Station> {
            busy {
                try {
                    // Invoke depot webservice and deliver as observable depot list
                    return FXCollections.observableArrayList(
                            Arrays.asList(
                                    *WebserviceFactory.depotService().find(fxSearchText.text)))
                } catch (e: Exception) {
                    log.error(e.message)
                    throw e
                }
            }
        }

        override fun succeeded() {
            val controller = this@DepotListController
            controller.stations = this.value
            controller.fxDepotTableView.items = stations
            if (controller.requestedDepotId != null)
                selectDepot(controller.requestedDepotId)
            controller.fxSearchText.styleClass.remove("leoz-error")
        }

        override fun failed() {
            fxSearchText.styleClass.add("leoz-error")
        }
    }

    fun onSearchTextChanged(text: String) {
        this.startQuery()
    }

    /**
     * Start remote depotlist query
     */
    private fun startQuery() {
        if (queryTask != null) {
            queryTask!!.cancel(true)
        }

        queryTask = QueryTask()
        queryTaskExecutor.execute(queryTask!!)
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        fxSearchText.textProperty().addListener { obj, oldValue, newValue -> onSearchTextChanged(newValue) }

        fxDepotTableView.selectionModel.selectedItemProperty().addListener { obj, oldValue, newValue ->
            this.ovItemSelected.onNext(newValue)
        }

        // Bind depotlist columns
        // TODO. needs strong typing!
        fxDepotTableMatchcodeColumn.cellValueFactory = PropertyValueFactory<Any, Any>("depotNr")
        fxDepotTableCompany1Column.cellValueFactory = PropertyValueFactory<Any, Any>("address1")
        fxDepotTableCompany2Column.cellValueFactory = PropertyValueFactory<Any, Any>("address2")
        fxDepotTableCountryColumn.cellValueFactory = PropertyValueFactory<Any, Any>("lkz")
        fxDepotTableZipCodeColumn.cellValueFactory = PropertyValueFactory<Any, Any>("plz")
        fxDepotTableCityColumn.cellValueFactory = PropertyValueFactory<Any, Any>("ort")
        fxDepotTableStreetColumn.cellValueFactory = PropertyValueFactory<Any, Any>("strasse")

        // Cell factory for cell specific behaviour handling
        val cellFactory = Callback<javafx.scene.control.TableColumn<kotlin.Any, kotlin.Any>, javafx.scene.control.TableCell<kotlin.Any, kotlin.Any>> {
            val tc = TextFieldTableCell<Any, Any>()
            tc.addEventFilter(MouseEvent.MOUSE_CLICKED) { event ->
                if (event.clickCount > 1) {
                    val cell = event.source as TableCell<Any, Any>
                    val station = cell.tableRow.item as Station
                    try {
                        this.leoBridge.sendMessage(MessageFactory.createViewDepotMessage(station))
                    } catch (e: Exception) {
                        this.ovError.onNext(Exception("Could not send message to leo1"))
                    }
                }
            }
            tc
        }

        fxDepotTableMatchcodeColumn.cellFactory = cellFactory
        fxDepotTableCompany1Column.cellFactory = cellFactory
        fxDepotTableCompany2Column.cellFactory = cellFactory
        fxDepotTableCountryColumn.cellFactory = cellFactory
        fxDepotTableZipCodeColumn.cellFactory = cellFactory
        fxDepotTableCityColumn.cellFactory = cellFactory
        fxDepotTableStreetColumn.cellFactory = cellFactory
    }

    private fun selectDepot(id: Int?) {
        for (i in this.stations.indices) {
            val d = this.stations[i]
            if (d.depotNr == id) {
                fxDepotTableView.selectionModel.select(d)
                fxDepotTableView.scrollTo(d)
                fxDepotTableView.requestFocus()
            }
        }
        this.requestedDepotId = null
    }

    fun requestDepotSelection(id: Int?) {
        this.requestedDepotId = id
        if (fxSearchText.text.length == 0 && this.stations.size > 0) {
            this.selectDepot(id)
        } else {
            fxSearchText.text = ""
            this.startQuery()
        }
    }

    public override fun onActivation() {
        if (!fxDepotTableView.isFocused)
            fxSearchText.requestFocus()

        this.startQuery()
    }

    override fun close() {
        super.close()
        this.queryTaskExecutor.shutdown()
    }
}
