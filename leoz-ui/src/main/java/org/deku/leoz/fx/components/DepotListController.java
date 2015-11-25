package org.deku.leoz.fx.components;

import com.sun.javafx.collections.ImmutableObservableList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.deku.leoz.Main;
import org.deku.leoz.WebserviceFactory;
import org.deku.leoz.bridge.LeoBridge;
import org.deku.leoz.bridge.MessageFactory;
import org.deku.leoz.fx.Controller;
import org.deku.leoz.rest.entities.internal.v1.Station;

import java.net.URL;
import java.util.Arrays;
import java.util.EventListener;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by masc on 22.09.14.
 */
public class DepotListController extends Controller implements Initializable {
    @FXML
    private TextField mSearchText;
    @FXML
    private TableView<Station> mDepotTableView;
    @FXML
    private TableColumn mDepotTableMatchcodeColumn;
    @FXML
    private TableColumn mDepotTableCompany1Column;
    @FXML
    private TableColumn mDepotTableCompany2Column;
    @FXML
    private TableColumn mDepotTableCountryColumn;
    @FXML
    private TableColumn mDepotTableZipCodeColumn;
    @FXML
    private TableColumn mDepotTableCityColumn;
    @FXML
    private TableColumn mDepotTableStreetColumn;

    private ObservableList<Station> mStations = new ImmutableObservableList<Station>();

    private ExecutorService mQueryTaskExecutor = Executors.newFixedThreadPool(3);
    private Task<ObservableList<Station>> mQueryTask;
    private Integer mRequestedDepotId = null;
    private Listener mListener;

    public interface Listener extends EventListener {
        void onDepotListItemSelected(Station station);
    }

    /**
     * Query task
     */
    private class QueryTask extends Task<ObservableList<Station>> {
        @Override
        protected ObservableList<Station> call() throws Exception {
            Platform.runLater(() -> Main.instance().getMainController().requestProgressIndicator());

            try {
                // Invoke depot webservice and deliver as observable depot list
                return FXCollections.observableArrayList(
                        Arrays.asList(
                                WebserviceFactory.depotService().find(mSearchText.getText())));
            } finally {
                Platform.runLater(() -> Main.instance().getMainController().releaseProgressIndicator());
            }
        }

        @Override
        protected void succeeded() {
            mStations = this.getValue();
            mDepotTableView.setItems(mStations);
            if (mRequestedDepotId != null)
                selectDepot(mRequestedDepotId);
            mSearchText.getStyleClass().remove("leoz-error");
        }

        @Override
        protected void failed() {
            mSearchText.getStyleClass().add("leoz-error");
        }
    }
    
    public Listener getListener() {
        return mListener;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void onSearchTextChanged(String text) {
        this.startQuery();
    }

    /**
     * Start remote depotlist query
     */
    private void startQuery() {
        if (mQueryTask != null) {
            mQueryTask.cancel(true);
        }

        mQueryTask = new QueryTask();
        mQueryTaskExecutor.execute(mQueryTask);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mSearchText.textProperty().addListener((obj, oldValue, newValue) -> {
            onSearchTextChanged(newValue);
        });

        mDepotTableView.getSelectionModel().selectedItemProperty().addListener((obj, oldValue, newValue) -> {
            if (mListener != null)
                mListener.onDepotListItemSelected(newValue);
        });

        // Bind depotlist columns
        mDepotTableMatchcodeColumn.setCellValueFactory(new PropertyValueFactory<Station, String>("depotMatchcode"));
        mDepotTableCompany1Column.setCellValueFactory(new PropertyValueFactory<Station, String>("firma1"));
        mDepotTableCompany2Column.setCellValueFactory(new PropertyValueFactory<Station, String>("firma2"));
        mDepotTableCountryColumn.setCellValueFactory(new PropertyValueFactory<Station, String>("lkz"));
        mDepotTableZipCodeColumn.setCellValueFactory(new PropertyValueFactory<Station, String>("plz"));
        mDepotTableCityColumn.setCellValueFactory(new PropertyValueFactory<Station, String>("ort"));
        mDepotTableStreetColumn.setCellValueFactory(new PropertyValueFactory<Station, String>("strasse"));

        // Cell factory for cell specific behaviour handling
        Callback<TableColumn, TableCell> cellFactory =
                new Callback<TableColumn, TableCell>() {
                    public TableCell call(TableColumn p) {
                        TableCell tc = new TextFieldTableCell<>();
                        tc.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                if (event.getClickCount() > 1) {
                                    TableCell cell = (TableCell) event.getSource();
                                    Station station = (Station) cell.getTableRow().getItem();
                                    try {
                                        LeoBridge.instance().sendMessage(MessageFactory.createViewDepotMessage(station));
                                    } catch (Exception e) {
                                        Main.instance().showError("Could not send message to leo1");
                                    }
                                }
                            }
                        });
                        return tc;
                    }
                };

        mDepotTableMatchcodeColumn.setCellFactory(cellFactory);
        mDepotTableCompany1Column.setCellFactory(cellFactory);
        mDepotTableCompany2Column.setCellFactory(cellFactory);
        mDepotTableCountryColumn.setCellFactory(cellFactory);
        mDepotTableZipCodeColumn.setCellFactory(cellFactory);
        mDepotTableCityColumn.setCellFactory(cellFactory);
        mDepotTableStreetColumn.setCellFactory(cellFactory);

        this.startQuery();
    }

    private void selectDepot(Integer id) {
        for (int i = 0; i < mStations.size(); i++) {
            Station d = mStations.get(i);
            if (d.getDepotNr().equals(id)) {
                mDepotTableView.getSelectionModel().select(d);
                mDepotTableView.scrollTo(d);
                mDepotTableView.requestFocus();
            }
        }
        mRequestedDepotId = null;
    }

    public void requestDepotSelection(Integer id) {
        mRequestedDepotId = id;
        if (mSearchText.getText().length() == 0 && mStations.size() > 0) {
            this.selectDepot(id);
        } else {
            mSearchText.setText("");
            this.startQuery();
        }
    }

    @Override
    public void onActivation() {
        if (!mDepotTableView.isFocused())
            mSearchText.requestFocus();
    }

    @Override
    public void close() {
        super.close();
        mQueryTaskExecutor.shutdown();
    }
}
