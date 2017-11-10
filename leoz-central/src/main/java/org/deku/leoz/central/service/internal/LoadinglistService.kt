package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.service.internal.BagService
import org.deku.leoz.service.internal.ParcelServiceV1
import org.deku.leoz.service.internal.UserService
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path

@Named
@Path("internal/v1/loadinglist")
class LoadinglistService:org.deku.leoz.service.internal.LoadinglistService {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    @Inject
    private lateinit var userService: UserService

    @Inject
    private lateinit var parcelService:ParcelServiceV1

    @Inject
    private lateinit var bagService:BagService

    override fun getNewLoadinglistNo(): Long {
        val user = userService.get()
        return parcelService.getNewLoadinglistNo()
    }

    override fun getNewBagLoadinglistNo(): Long {
        val user=userService.get()
        return bagService.getNewBagLoadinglistNo()
    }

    override fun getParcels2ExportByLoadingList(loadinglistNo: Long): List<ParcelServiceV1.Order2Export> {
        val user = userService.get()
        return parcelService.getParcels2ExportByLoadingList(loadinglistNo)
    }
}