package org.deku.leoz.fx.components;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.filter.IncludeFilter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import org.deku.leoz.fx.FormSkin;
import org.deku.leoz.rest.entities.internal.v1.Station;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by masc on 23.09.14.
 */
public class DepotDetailsController implements Initializable {
    @FXML
    private TabPane mTabPane;
    @FXML
    private Pane mMainPane;
    @FXML
    private Pane mOpsPane;
    @FXML
    private Pane mAccountingPane;
    @FXML
    private Pane mTransferPane;
    @FXML
    private Pane mPermissionsPane;

    private FXForm mFxFormMain;
    private FXForm mFxFormOps;
    private FXForm mFxFormAccounting;
    private FXForm mFxFormTransfer;
    private FXForm mFxFormPermissions;
    private Station mStation;

    public Station getStation() {
        return mStation;
    }

    public void setStation(Station station) {
        mStation = station;
        this.updateTab();
    }

    private void updateTab() {
        FXForm form = null;
        switch(mTabPane.getSelectionModel().getSelectedIndex()) {
            case 0:
                form = mFxFormMain;
                break;
            case 1:
                form = mFxFormOps;
                break;
            case 2:
                form = mFxFormAccounting;
                break;
            case 3:
                form = mFxFormTransfer;
                break;
            case 4:
                form = mFxFormPermissions;
                break;
        }
        form.setSource(mStation);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mTabPane.getSelectionModel().selectedItemProperty().addListener((obj, oldvalue, newValue) -> {
            this.updateTab();
        });
        mFxFormMain = new FXForm();

        mFxFormMain.setSkin(new FormSkin(mFxFormMain));
        mFxFormMain.addFilters(new IncludeFilter("depotNr", "depotMatchcode", "firma1", "firma2", "lkz", "ort", "strasse", "telefon1", "telefon2", "mobil", "telefax", "nottelefon1", "nottelefon2", "email",
                "anprechpartner1", "anprechpartner2", "webemail", "webadresse", "region", "coloader", "webshopInit"));
        mMainPane.getChildren().add(mFxFormMain);

        mFxFormOps = new FXForm();
        mFxFormOps.setSkin(new FormSkin(mFxFormOps));
        mFxFormOps.addFilters(new IncludeFilter("aktivierungsdatum", "deaktivierungsdatum", "istGueltig", "masterDepot", "ebSdgDepot", "ebDepotAd", "ebGen", "ebUmvDepot", "comCode", "qualitaet", "ladehilfeLinie",
                "ladehilfeWas", "ladehilfeKg", "ladehilfeAb", "strang", "multiBag", "bagKontingent", "bagBemerkung", "bagCo"));
        mOpsPane.getChildren().add(mFxFormOps);

        mFxFormAccounting = new FXForm();
        mFxFormAccounting.setSkin(new FormSkin(mFxFormAccounting));
        mFxFormAccounting.addFilters(new IncludeFilter("firmenverbund", "kondition", "konditionAbD", "konditionLd", "zahlungsbedingungen", "zahlungsbedingungenR", "verbundenesU", "debitorNr", "kreditorNr", "cod1",
                "masterVertrag", "ustId", "ekStNr", "hanReg", /*"rName1", "rName2",*/ "rlkz" /*,"rOrt", "rStrasse"*/));
        mAccountingPane.getChildren().add(mFxFormAccounting);

        mFxFormTransfer = new FXForm();
        mFxFormTransfer.setSkin(new FormSkin(mFxFormTransfer));
        mFxFormTransfer.addFilters(new IncludeFilter("intRoutingLkz", "exportEmail", "exportFtpServer", "exportFtpUser", "exportFtpPwd", "exportToGlo", "exportToXml", "qualiMail", "umRoutung", "xmlStammdaten", /*"eMailPas",*/
                "paXml", "paPdf", "paDruck", "rgGutschrift", "rgRechnung", "mentorDepotNr", "trzProz", "rup", "password", "smspwd"));
        mTransferPane.getChildren().add(mFxFormTransfer);

        mFxFormPermissions= new FXForm();
        mFxFormPermissions.setSkin(new FormSkin(mFxFormPermissions));
        mFxFormPermissions.addFilters(new IncludeFilter("nnOk", "valOk", "maxValwert", "maxHoeherhaftung", "maxWarenwert"));
        mPermissionsPane.getChildren().add(mFxFormPermissions);
    }
}
