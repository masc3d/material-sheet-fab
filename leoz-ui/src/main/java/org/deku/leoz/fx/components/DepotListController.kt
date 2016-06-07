package org.deku.leoz.fx.components

import com.sun.javafx.collections.ImmutableObservableList
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.event.EventHandler
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
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leoz.Main
import org.deku.leoz.WebserviceFactory
import org.deku.leoz.bridge.LeoBridge
import org.deku.leoz.bridge.MessageFactory
import org.deku.leoz.fx.Controller
import org.deku.leoz.rest.entities.internal.v1.Station

import java.net.URL
import java.util.Arrays
import java.util.EventListener
import java.util.ResourceBundle
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by masc on 22.09.14.
 */
class DepotListController : Controller(), Initializable {
    private val mLogger = LogFactory.getLog(this.javaClass)

    @FXML
    private val mSearchText: TextField? = null
    @FXML
    private lateinit var mDepotTableView: TableView<Station>
    @FXML
    private val mDepotTableMatchcodeColumn: TableColumn<Any, Any>? = null
    @FXML
    private val mDepotTableCompany1Column: TableColumn<Any, Any>? = null
    @FXML
    private val mDepotTableCompany2Column: TableColumn<Any, Any>? = null
    @FXML
    private val mDepotTableCountryColumn: TableColumn<Any, Any>? = null
    @FXML
    private val mDepotTableZipCodeColumn: TableColumn<Any, Any>? = null
    @FXML
    private val mDepotTableCityColumn: TableColumn<Any, Any>? = null
    @FXML
    private val mDepotTableStreetColumn: TableColumn<Any, Any>? = null

    private var mStations: ObservableList<Station> = ImmutableObservableList()

    private val mQueryTaskExecutor = Executors.newFixedThreadPool(3)
    private var mQueryTask: Task<ObservableList<Station>>? = null
    private var mRequestedDepotId: Int? = null
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
                                *WebserviceFactory.depotService().find(mSearchText!!.text)))
            } catch (e: Exception) {
                mLogger.error(e.message, e)
                throw e
            } finally {
                Platform.runLater { Main.instance().mainController.releaseProgressIndicator() }
            }
        }

        override fun succeeded() {
            mStations = this.value
            mDepotTableView.items = mStations
            if (mRequestedDepotId != null)
                selectDepot(mRequestedDepotId)
            mSearchText!!.styleClass.remove("leoz-error")
        }

        override fun failed() {
            mSearchText!!.styleClass.add("leoz-error")
        }
    }

    fun onSearchTextChanged(text: String) {
        this.startQuery()
    }

    /**
     * Start remote depotlist query
     */
    private fun startQuery() {
        if (mQueryTask != null) {
            mQueryTask!!.cancel(true)
        }

        mQueryTask = QueryTask()
        mQueryTaskExecutor.execute(mQueryTask!!)
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        mSearchText!!.textProperty().addListener { obj, oldValue, newValue -> onSearchTextChanged(newValue) }

        mDepotTableView.selectionModel.selectedItemProperty().addListener { obj, oldValue, newValue ->
            if (listener != null)
                listener!!.onDepotListItemSelected(newValue)
        }

        // Bind depotlist columns
        // TODO. needs strong typing!
        mDepotTableMatchcodeColumn!!.setCellValueFactory(PropertyValueFactory<Any, Any>("depotNr"))
        mDepotTableCompany1Column!!.setCellValueFactory(PropertyValueFactory<Any, Any>("address1"))
        mDepotTableCompany2Column!!.setCellValueFactory(PropertyValueFactory<Any, Any>("address2"))
        mDepotTableCountryColumn!!.setCellValueFactory(PropertyValueFactory<Any, Any>("lkz"))
        mDepotTableZipCodeColumn!!.setCellValueFactory(PropertyValueFactory<Any, Any>("plz"))
        mDepotTableCityColumn!!.setCellValueFactory(PropertyValueFactory<Any, Any>("ort"))
        mDepotTableStreetColumn!!.setCellValueFactory(PropertyValueFactory<Any, Any>("strasse"))

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

        mDepotTableMatchcodeColumn.setCellFactory(cellFactory)
        mDepotTableCompany1Column.setCellFactory(cellFactory)
        mDepotTableCompany2Column.setCellFactory(cellFactory)
        mDepotTableCountryColumn.setCellFactory(cellFactory)
        mDepotTableZipCodeColumn.setCellFactory(cellFactory)
        mDepotTableCityColumn.setCellFactory(cellFactory)
        mDepotTableStreetColumn.setCellFactory(cellFactory)

        this.startQuery()
    }

    private fun selectDepot(id: Int?) {
        for (i in mStations.indices) {
            val d = mStations[i]
            if (d.depotNr == id) {
                mDepotTableView.selectionModel.select(d)
                mDepotTableView.scrollTo(d)
                mDepotTableView.requestFocus()
            }
        }
        mRequestedDepotId = null
    }

    fun requestDepotSelection(id: Int?) {
        mRequestedDepotId = id
        if (mSearchText!!.text.length == 0 && mStations.size > 0) {
            this.selectDepot(id)
        } else {
            mSearchText.text = ""
            this.startQuery()
        }
    }

    public override fun onActivation() {
        if (!mDepotTableView.isFocused)
            mSearchText!!.requestFocus()
    }

    override fun close() {
        super.close()
        mQueryTaskExecutor.shutdown()
    }
}
