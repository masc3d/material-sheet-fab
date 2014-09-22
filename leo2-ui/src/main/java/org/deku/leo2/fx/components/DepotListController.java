package org.deku.leo2.fx.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.deku.leo2.WebserviceFactory;
import org.deku.leo2.rest.v1.entities.Depot;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by masc on 22.09.14.
 */
public class DepotListController implements Initializable {
    @FXML
    TextField mSearchText;
    @FXML
    TableView<Depot> mDepotTableView;
    @FXML
    TableColumn<Depot, String> mDepotTableMatchcodeColumn;
    @FXML
    TableColumn<Depot, String> mDepotTableCompany1Column;
    @FXML
    TableColumn<Depot, String> mDepotTableCompany2Column;
    @FXML
    TableColumn<Depot, String> mDepotTableCountryColumn;
    @FXML
    TableColumn<Depot, String> mDepotTablePostalcodeColumn;
    @FXML
    TableColumn<Depot, String> mDepotTableCityColumn;
    @FXML
    TableColumn<Depot, String> mDepotTableStreetColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mSearchText.textProperty().addListener( (obj, oldValue, newValue) -> {
            onSearchTextChanged(newValue);
        });

        // Bind pojo properties
        mDepotTableMatchcodeColumn.setCellValueFactory(new PropertyValueFactory<Depot, String>("depotMatchcode"));
        mDepotTableCompany1Column.setCellValueFactory(new PropertyValueFactory<Depot, String>("firma1"));
        mDepotTableCompany2Column.setCellValueFactory(new PropertyValueFactory<Depot, String>("firma2"));
        mDepotTableCountryColumn.setCellValueFactory(new PropertyValueFactory<Depot, String>("lkz"));
        mDepotTablePostalcodeColumn.setCellValueFactory(new PropertyValueFactory<Depot, String>("plz"));
        mDepotTableCityColumn.setCellValueFactory(new PropertyValueFactory<Depot, String>("ort"));
        mDepotTableStreetColumn.setCellValueFactory(new PropertyValueFactory<Depot, String>("strasse"));

        List<Depot> depots = Arrays.asList(WebserviceFactory.depotService().get());

        ObservableList<Depot> oDepots = FXCollections.observableArrayList(depots);
        mDepotTableView.setItems(oDepots);
    }

    public void onSearchTextChanged(String text) {

    }
}
