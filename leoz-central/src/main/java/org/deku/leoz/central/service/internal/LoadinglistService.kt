package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.model.DekuUnitNumber
import org.deku.leoz.model.LoadinglistType
import org.deku.leoz.model.UnitNumber
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
@Path("internal/v1/loadinglist")
class LoadinglistService : org.deku.leoz.service.internal.LoadinglistService {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    @Inject
    private lateinit var userService: UserService

    @Inject
    private lateinit var exportService: ExportService

    override fun getNewLoadinglistNo(): LoadinglistService.Loadinglist {
        userService.get()

        return exportService.getNewLoadinglistNo()
    }

    override fun getNewBagLoadinglistNo(): LoadinglistService.Loadinglist {
        userService.get()

        return exportService.getNewBagLoadinglistNo()
    }

    override fun getParcels2ExportByLoadingList(loadinglistNo: Long): LoadinglistService.Loadinglist? {
        userService.get()


        val un= DekuUnitNumber.parseLabel(loadinglistNo.toString())
        when {
            un.hasError -> {
                throw DefaultProblem(
                        status = Response.Status.NOT_FOUND,
                        title = "Wrong check digit"
                )
            }
        }
        if (un.value.type != UnitNumber.Type.Parcel)

            throw DefaultProblem(
                    status = Response.Status.NOT_FOUND,
                    title = "Loadinglist not valid"
            )

        val orders = exportService.getParcels2ExportByLoadingList(un.value.value.toLong())
        if (orders.count() == 0)
            throw DefaultProblem(
                    status = Response.Status.NOT_FOUND,
                    title = "No orders found"
            )
        val loadinglist = LoadinglistService.Loadinglist(loadinglistNo = un.value.value.toLong(), orders = orders)

        return loadinglist
    }

}