package org.deku.leoz.fx.components

import com.dooapp.fxform.FXForm
import com.dooapp.fxform.filter.IncludeFilter
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TabPane
import javafx.scene.layout.Pane
import org.deku.leoz.fx.FormSkin
import org.deku.leoz.rest.entities.internal.v1.Station

import java.net.URL
import java.util.ResourceBundle

/**
 * Created by masc on 23.09.14.
 */
class DepotDetailsController : Initializable {
    @FXML
    private val mTabPane: TabPane? = null
    @FXML
    private val mMainPane: Pane? = null
    @FXML
    private val mOpsPane: Pane? = null
    @FXML
    private val mAccountingPane: Pane? = null
    @FXML
    private val mTransferPane: Pane? = null
    @FXML
    private val mPermissionsPane: Pane? = null

    private var mFxFormMain: FXForm<Any>? = null
    private var mFxFormOps: FXForm<Any>? = null
    private var mFxFormAccounting: FXForm<Any>? = null
    private var mFxFormTransfer: FXForm<Any>? = null
    private var mFxFormPermissions: FXForm<Any>? = null
    var station: Station? = null
        set(station) {
            this.station = station
            this.updateTab()
        }

    private fun updateTab() {
        var form: FXForm<Any>? = null
        when (mTabPane!!.selectionModel.selectedIndex) {
            0 -> form = mFxFormMain
            1 -> form = mFxFormOps
            2 -> form = mFxFormAccounting
            3 -> form = mFxFormTransfer
            4 -> form = mFxFormPermissions
        }
        form!!.source = station
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        mTabPane!!.selectionModel.selectedItemProperty().addListener { obj, oldvalue, newValue -> this.updateTab() }
        mFxFormMain = FXForm()

        mFxFormMain!!.skin = FormSkin(mFxFormMain)
        mFxFormMain!!.addFilters(IncludeFilter("depotNr", "depotMatchcode", "address1", "address2", "lkz", "plz", "ort", "strasse"))
        mMainPane!!.children.add(mFxFormMain)

        mFxFormOps = FXForm()
        mFxFormOps!!.skin = FormSkin(mFxFormOps)
        mFxFormOps!!.addFilters(IncludeFilter("aktivierungsdatum", "deaktivierungsdatum", "istGueltig", "masterDepot", "ebSdgDepot", "ebDepotAd", "ebGen", "ebUmvDepot", "comCode", "qualitaet", "ladehilfeLinie",
                "ladehilfeWas", "ladehilfeKg", "ladehilfeAb", "strang", "multiBag", "bagKontingent", "bagBemerkung", "bagCo"))
        mOpsPane!!.children.add(mFxFormOps)

        mFxFormAccounting = FXForm()
        mFxFormAccounting!!.skin = FormSkin(mFxFormAccounting)
        mFxFormAccounting!!.addFilters(IncludeFilter("firmenverbund", "kondition", "konditionAbD", "konditionLd", "zahlungsbedingungen", "zahlungsbedingungenR", "verbundenesU", "debitorNr", "kreditorNr", "cod1",
                "masterVertrag", "ustId", "ekStNr", "hanReg", /*"rName1", "rName2",*/ "rlkz" /*,"rOrt", "rStrasse"*/))
        mAccountingPane!!.children.add(mFxFormAccounting)

        mFxFormTransfer = FXForm()
        mFxFormTransfer!!.skin = FormSkin(mFxFormTransfer)
        mFxFormTransfer!!.addFilters(IncludeFilter("intRoutingLkz", "exportEmail", "exportFtpServer", "exportFtpUser", "exportFtpPwd", "exportToGlo", "exportToXml", "qualiMail", "umRoutung", "xmlStammdaten", /*"eMailPas",*/
                "paXml", "paPdf", "paDruck", "rgGutschrift", "rgRechnung", "mentorDepotNr", "trzProz", "rup", "password", "smspwd"))
        mTransferPane!!.children.add(mFxFormTransfer)

        mFxFormPermissions = FXForm()
        mFxFormPermissions!!.skin = FormSkin(mFxFormPermissions)
        mFxFormPermissions!!.addFilters(IncludeFilter("nnOk", "valOk", "maxValwert", "maxHoeherhaftung", "maxWarenwert"))
        mPermissionsPane!!.children.add(mFxFormPermissions)
    }
}
