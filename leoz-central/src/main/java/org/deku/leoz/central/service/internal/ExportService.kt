package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.service.internal.BagService
import org.deku.leoz.service.internal.ExportService
import org.deku.leoz.service.internal.LoadinglistService
import org.deku.leoz.service.internal.ParcelServiceV1
import org.deku.leoz.service.internal.UserService
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.DefaultProblem
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Named
@Path("internal/v1/export")
class ExportService : org.deku.leoz.service.internal.ExportService {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    @Inject
    private lateinit var userService: UserService

    @Inject
    private lateinit var parcelService: ParcelServiceV1

    @Inject
    private lateinit var bagService: BagService

    override fun export(scanCode: String, loadingListNo: Long, stationNo: Int): Boolean {
        return parcelService.export(scanCode, loadingListNo, stationNo)
    }

    override fun getLoadedParcels2ExportByStationNo(stationNo: Int): List<ParcelServiceV1.Order2Export> {
        return parcelService.getLoadedParcels2ExportByStationNo(stationNo)
    }

    override fun getNewLoadinglistNo(): LoadinglistService.Loadinglist {
        return parcelService.getNewLoadinglistNo()
    }

    override fun getParcels2ExportByStationNo(stationNo: Int): List<ParcelServiceV1.Order2Export> {
        return parcelService.getParcels2ExportByStationNo(stationNo)
    }

    override fun getParcels2ExportInBagByStationNo(stationNo: Int): List<ParcelServiceV1.Order2Export> {
        return parcelService.getParcels2ExportInBagByStationNo(stationNo)
    }
}