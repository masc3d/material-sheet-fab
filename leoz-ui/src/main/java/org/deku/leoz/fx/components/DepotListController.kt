package org.deku.leoz.fx.components

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
import org.apache.commons.logging.LogFactory
import org.deku.leoz.Main
import org.deku.leoz.WebserviceFactory
import org.deku.leoz.bridge.LeoBridge
import org.deku.leoz.bridge.MessageFactory
import org.deku.leoz.fx.Controller
import org.deku.leoz.rest.entities.internal.v1.Station
import java.net.URL
import java.util.*
import java.util.concurrent.Executors

/**
 * Created by masc on 22.09.14.
 */
class DepotListController : Controller(), Initializable {
    private val mLogger = LogFactory.getLog(this.javaClass)

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

    private var stations: ObservableList<Station> = ImmutableObservableList()

    private val queryTaskExecutor = Executors.newFixedThreadPool(3)
    private var queryTask: Task<ObservableList<Station>>? = null
    private var requestedDepotId: Int? = null
    var listener: Listener? = null

    interface Listener : EventListener {
        fun onDepotListItemSelected(station: Station)
    }

    /**
     * Query task
     */
    private inner class QueryTask : Task<ObservableList<Station>>() {
        @Throws(Exception::class)
        override fun call(): ObservableList<Station> {
            Platform.runLater { Main.instance().mainController.requestProgressIndicator() }

            try {
                // Invoke depot webservice and deliver as observable depot list
                return FXCollections.observableArrayList(
                        Arrays.asList(
                                *WebserviceFactory.depotService().find(fxSearchText.text)))
            } catch (e: Exception) {
                mLogger.error(e.message, e)
                throw e
            } finally {
                Platform.runLater { Main.instance().mainController.releaseProgressIndicator() }
            }
        }

        override fun succeeded() {
            stations = this.value
            fxDepotTableView.items = stations
            if (requestedDepotId != null)
                selectDepot(requestedDepotId)
            fxSearchText.styleClass.remove("leoz-error")
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
            if (listener != null)
                listener!!.onDepotListItemSelected(newValue)
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
                        LeoBridge.instance().sendMessage(MessageFactory.createViewDepotMessage(station))
                    } catch (e: Exception) {
                        Main.instance().showError("Could not send message to leo1")
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

        this.startQuery()
    }

    private fun selectDepot(id: Int?) {
        for (i in stations.indices) {
            val d = stations[i]
            if (d.depotNr == id) {
                fxDepotTableView.selectionModel.select(d)
                fxDepotTableView.scrollTo(d)
                fxDepotTableView.requestFocus()
            }
        }
        requestedDepotId = null
    }

    fun requestDepotSelection(id: Int?) {
        requestedDepotId = id
        if (fxSearchText.text.length == 0 && stations.size > 0) {
            this.selectDepot(id)
        } else {
            fxSearchText.text = ""
            this.startQuery()
        }
    }

    public override fun onActivation() {
        if (!fxDepotTableView.isFocused)
            fxSearchText.requestFocus()
    }

    override fun close() {
        super.close()
        queryTaskExecutor.shutdown()
    }
}
