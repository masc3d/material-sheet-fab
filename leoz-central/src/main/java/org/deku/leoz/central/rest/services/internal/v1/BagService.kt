package org.deku.leoz.central.rest.services.internal.v1

import sx.packager.BundleRepository
import org.deku.leoz.central.Application
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.service.update.UpdateInfo
import org.deku.leoz.service.update.UpdateInfoRequest
import org.deku.leoz.central.data.repository.NodeJooqRepository
import org.deku.leoz.node.data.jpa.QMstBundleVersion
import org.deku.leoz.node.data.repository.master.BundleVersionRepository
import org.deku.leoz.node.rest.ServiceException
import org.deku.leoz.node.rest.service.internal.v1.BundleService
import org.deku.leoz.rest.entity.internal.v1.BagInitRequest
import org.deku.leoz.rest.service.internal.v1.BagService
import org.joda.time.Hours
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import sx.rs.ApiKey
import sx.time.toDate
import java.util.*
//import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.BadRequestException
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import java.time.LocalDate

/**
 * Bundle service (leoz-central)
 * Created by masc on 01/11/2016.
 **/
@Named
@ApiKey(false)
@Path("internal/v1/bag")
class BagService : BagService {

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    override fun get(id: String): String {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return "String Bag:"+id
    }

    override fun initialize(bagInitRequest: BagInitRequest): Boolean {
        if(bagInitRequest.bagId.isNullOrEmpty()) {
            throw ServiceException(BagService.ErrorCode.BAG_ID_MISSING)
        }

        if(bagInitRequest.whiteSeal.isNullOrEmpty()) {
            throw ServiceException(BagService.ErrorCode.WHITE_SEAL_MISSING)
        }
        if(bagInitRequest.yellowSeal.isNullOrEmpty()) {
            throw ServiceException(BagService.ErrorCode.YELLOW_SEAL_MISSING)
        }
        if(bagInitRequest.depotNr==null) {
            throw ServiceException(BagService.ErrorCode.DEPOT_NR_MISSING)
        }

        if(bagInitRequest.bagId!!.length < 12) {
            throw ServiceException(BagService.ErrorCode.BAG_ID_MISSING_CHECK_DIGIT)
        }

        if(bagInitRequest.whiteSeal!!.length < 12) {
            throw ServiceException(BagService.ErrorCode.WHITE_SEAL_MISSING_CHECK_DIGIT)
        }
        if(bagInitRequest.yellowSeal!!.length < 12) {
            throw ServiceException(BagService.ErrorCode.YELLOW_SEAL_MISSING_CHECK_DIGIT)
        }
        if(bagInitRequest.depotNr!! <= 0||bagInitRequest.depotNr!!>999){
            throw ServiceException(BagService.ErrorCode.DEPOT_NR_NOT_VALID)
        }
/**
        if(!checkCheckDigit(bagInitRequest.bagId!!.toDouble())) {
            throw ServiceException(BagService.ErrorCode.BAG_ID_WRONG_CHECK_DIGIT)
        }

        if(!checkCheckDigit(bagInitRequest.whiteSeal!!.toDouble())) {
            throw ServiceException(BagService.ErrorCode.WHITE_SEAL_WRONG_CHECK_DIGIT)
        }
**/
        //TODO

        throw NotImplementedError()
/**
        try {

            val result = dslContext.selectCount().from(Tables.TBLHUBLINIENPLAN).where(Tables.TBLHUBLINIENPLAN.ISTLIFE.eq(-1).and(Tables.TBLHUBLINIENPLAN.ARBEITSDATUM.equal((java.time.LocalDate.now().toDate())))
        } catch(e: Exception) {
            throw BadRequestException(e.message)
        }**/
        }




    private val log = LoggerFactory.getLogger(this.javaClass)


}
