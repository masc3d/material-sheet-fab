package org.deku.leo2.fx.components;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.deku.leo2.Main;
import org.deku.leo2.WebserviceFactory;
import org.deku.leo2.fx.Controller;
import org.deku.leo2.rest.v1.entities.Depot;

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
    private TableView<Depot> mDepotTableView;
    @FXML
    private TableColumn<Depot, String> mDepotTableMatchcodeColumn;
    @FXML
    private TableColumn<Depot, String> mDepotTableCompany1Column;
    @FXML
    private TableColumn<Depot, String> mDepotTableCompany2Column;
    @FXML
    private TableColumn<Depot, String> mDepotTableCountryColumn;
    @FXML
    private TableColumn<Depot, String> mDepotTableZipCodeColumn;
    @FXML
    private TableColumn<Depot, String> mDepotTableCityColumn;
    @FXML
    private TableColumn<Depot, String> mDepotTableStreetColumn;

    private ExecutorService mQueryTaskExecutor = Executors.newFixedThreadPool(3);
    private Task<ObservableList<Depot>> mQueryTask;

    private Listener mListener;

    public interface Listener extends EventListener {
        void onDepotListItemSelected(Depot depot);
    }

    /**
     * Query task
     */
    private class QueryTask extends Task<ObservableList<Depot>> {
        @Override
        protected ObservableList<Depot> call() throws Exception {
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
            mDepotTableView.setItems(this.getValue());
            mSearchText.getStyleClass().remove("leo2-error");
        }

        @Override
        protected void failed() {
            mSearchText.getStyleClass().add("leo2-error");
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
        mDepotTableMatchcodeColumn.setCellValueFactory(new PropertyValueFactory<Depot, String>("depotMatchcode"));
        mDepotTableCompany1Column.setCellValueFactory(new PropertyValueFactory<Depot, String>("firma1"));
        mDepotTableCompany2Column.setCellValueFactory(new PropertyValueFactory<Depot, String>("firma2"));
        mDepotTableCountryColumn.setCellValueFactory(new PropertyValueFactory<Depot, String>("lkz"));
        mDepotTableZipCodeColumn.setCellValueFactory(new PropertyValueFactory<Depot, String>("plz"));
        mDepotTableCityColumn.setCellValueFactory(new PropertyValueFactory<Depot, String>("ort"));
        mDepotTableStreetColumn.setCellValueFactory(new PropertyValueFactory<Depot, String>("strasse"));

        this.startQuery();
    }

    @Override
    public void onActivation() {
        mSearchText.requestFocus();
    }

    @Override
    public void dispose() {
        super.dispose();
        mQueryTaskExecutor.shutdown();
    }
}
