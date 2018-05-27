package org.deku.leoz.ui.fx.components

import com.dooapp.fxform.FXForm
import com.dooapp.fxform.filter.IncludeFilter
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TabPane
import javafx.scene.layout.Pane
import org.deku.leoz.service.internal.entity.Station
import org.deku.leoz.ui.fx.FormSkin
import java.net.URL
import java.util.*

/**
 * Created by masc on 23.09.14.
 */
class DepotDetailsController : Initializable {
    @FXML
    private lateinit var fxTabPane: TabPane
    @FXML
    private lateinit var fxMainPane: Pane
    @FXML
    private lateinit var fxOpsPane: Pane
    @FXML
    private lateinit var fxAccountingPane: Pane
    @FXML
    private lateinit var fxTransferPane: Pane
    @FXML
    private lateinit var fxPermissionsPane: Pane

    private lateinit var fxFormMain: FXForm<Any>
    private lateinit var fxFormOps: FXForm<Any>
    private lateinit var fxFormAccounting: FXForm<Any>
    private lateinit var fxFormTransfer: FXForm<Any>
    private lateinit var fxFormPermissions: FXForm<Any>

    var station: Station? = null
        set(station) {
            field = station
            this.updateTab()
        }

    private fun updateTab() {
        var form: FXForm<Any>? = null
        when (fxTabPane.selectionModel.selectedIndex) {
            0 -> form = fxFormMain
            1 -> form = fxFormOps
            2 -> form = fxFormAccounting
            3 -> form = fxFormTransfer
            4 -> form = fxFormPermissions
        }
        form!!.source = station
    }

    //todo some Fieldnames are not up to date ;)

    override fun initialize(location: URL, resources: ResourceBundle) {
        fxTabPane.selectionModel.selectedItemProperty().addListener { _, _, _ -> this.updateTab() }
        fxFormMain = FXForm()

        fxFormMain.skin = FormSkin(fxFormMain)
        fxFormMain.addFilters(IncludeFilter("depotNr", "depotMatchcode", "address1", "address2", "lkz", "plz", "ort", "strasse"))
        fxMainPane.children.add(fxFormMain)

        fxFormOps = FXForm()
        fxFormOps.skin = FormSkin(fxFormOps)
        fxFormOps.addFilters(IncludeFilter("aktivierungsdatum", "deaktivierungsdatum", "istGueltig", "masterDepot", "ebSdgDepot", "ebDepotAd", "ebGen", "ebUmvDepot", "comCode", "qualitaet", "ladehilfeLinie",
                "ladehilfeWas", "ladehilfeKg", "ladehilfeAb", "strang", "multiBag", "bagKontingent", "bagBemerkung", "bagCo"))
        fxOpsPane.children.add(fxFormOps)

        fxFormAccounting = FXForm()
        fxFormAccounting.skin = FormSkin(fxFormAccounting)
        fxFormAccounting.addFilters(IncludeFilter("firmenverbund", "kondition", "konditionAbD", "konditionLd", "zahlungsbedingungen", "zahlungsbedingungenR", "verbundenesU", "debitorNr", "kreditorNr", "cod1",
                "masterVertrag", "ustId", "ekStNr", "hanReg", /*"rName1", "rName2",*/ "rlkz" /*,"rOrt", "rStrasse"*/))
        fxAccountingPane.children.add(fxFormAccounting)

        fxFormTransfer = FXForm()
        fxFormTransfer.skin = FormSkin(fxFormTransfer)
        fxFormTransfer.addFilters(IncludeFilter("intRoutingLkz", "exportEmail", "exportFtpServer", "exportFtpUser", "exportFtpPwd", "exportToGlo", "exportToXml", "qualiMail", "umRoutung", "xmlStammdaten", /*"eMailPas",*/
                "paXml", "paPdf", "paDruck", "rgGutschrift", "rgRechnung", "mentorDepotNr", "trzProz", "rup", "password", "smspwd"))
        fxTransferPane.children.add(fxFormTransfer)

        fxFormPermissions = FXForm()
        fxFormPermissions.skin = FormSkin(fxFormPermissions)
        fxFormPermissions.addFilters(IncludeFilter("nnOk", "valOk", "maxValwert", "maxHoeherhaftung", "maxWarenwert"))
        fxPermissionsPane.children.add(fxFormPermissions)
    }
}
