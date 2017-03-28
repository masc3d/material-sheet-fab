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
import sx.time.toTimestamp
import java.util.*
//import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.BadRequestException
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import org.deku.leoz.util.*
import sx.time.toLocalDate
import java.sql.Date

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

        if(!checkCheckDigit(bagInitRequest.bagId!!)) {
            throw ServiceException(BagService.ErrorCode.BAG_ID_WRONG_CHECK_DIGIT)
        }

        if(!checkCheckDigit(bagInitRequest.whiteSeal!!)) {
            throw ServiceException(BagService.ErrorCode.WHITE_SEAL_WRONG_CHECK_DIGIT)
        }
        if(!checkCheckDigit(bagInitRequest.yellowSeal!!)) {
            throw ServiceException(BagService.ErrorCode.YELLOW_SEAL_WRONG_CHECK_DIGIT)
        }
        //TODO



        try {
            var dtWork:LocalDate=java.time.LocalDateTime.now().minusHours((6)).toLocalDate()
            var result = dslContext.selectCount().from(Tables.TBLHUBLINIENPLAN).where(Tables.TBLHUBLINIENPLAN.ISTLIFE.equal(-1)).and(Tables.TBLHUBLINIENPLAN.ARBEITSDATUM.equal(dtWork.toTimestamp())).fetch()
            //val result = dslContext.selectCount().from(Tables.TBLHUBLINIENPLAN).where(Tables.TBLHUBLINIENPLAN.ISTLIFE.equal(-1)).fetch()
            if (result.getValue(0,0)==0){
                //dtWork=nextWerktag(dtWork.addDays(-1),"100","DE","36285")
            }else{
                //nach Feierabend und Tagesabschluss schon die bags für den nächsten Tag initialisieren oder am Wochenende
                //dtWork=nextwerktag(dtWork,"100","DE","36285"
                dtWork= getNextDeliveryDate()
            }
            val dblStatus:Double=5.0
            //val dt:java.util.Date=dtWork.toDate()
            val dt:java.sql.Date=java.sql.Date.valueOf(dtWork);

            /**
            result=dslContext.selectCount().from(Tables.SSO_S_MOVEPOOL).where(Tables.SSO_S_MOVEPOOL.LASTDEPOT.eq(bagInitRequest.depotNr!!.toDouble())).and(Tables.SSO_S_MOVEPOOL.STATUS.eq(dblStatus)).and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m")).and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt)).fetch()
            if(result.getValue(0,0)!=0){
                throw ServiceException(BagService.ErrorCode.BAG_FOR_DEPOT_ALREADY_EXISTS)
            }
            **/

            var iResultCount:Int=dslContext.fetchCount(Tables.SSO_S_MOVEPOOL,Tables.SSO_S_MOVEPOOL.LASTDEPOT.eq(bagInitRequest.depotNr!!.toDouble()).and(Tables.SSO_S_MOVEPOOL.STATUS.eq(dblStatus)).and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m")).and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt)))
            if(iResultCount>0){
                throw ServiceException(BagService.ErrorCode.BAG_FOR_DEPOT_ALREADY_EXISTS)
            }
//status_time wieder raus, timestamp on update
            val dblBagID:Double=bagInitRequest.bagId!!.substring(0,11).toDouble()
            val dblWhiteSeal:Double=bagInitRequest.whiteSeal!!.substring(0,11).toDouble()
            val dblYellowSeal:Double=bagInitRequest.yellowSeal!!.substring(0,11).toDouble()
            val dblNull:Double?=null

            iResultCount=dslContext.update(Tables.SSO_S_MOVEPOOL).set(Tables.SSO_S_MOVEPOOL.ORDERDEPOT2HUB, dblNull).set(Tables.SSO_S_MOVEPOOL.ORDERHUB2DEPOT, dblNull).set(Tables.SSO_S_MOVEPOOL.SEAL_NUMBER_GREEN,dblWhiteSeal).set(Tables.SSO_S_MOVEPOOL.SEAL_NUMBER_YELLOW,dblYellowSeal).set(Tables.SSO_S_MOVEPOOL.STATUS,dblStatus).set(Tables.SSO_S_MOVEPOOL.INIT_STATUS,1).set(Tables.SSO_S_MOVEPOOL.LASTDEPOT,bagInitRequest.depotNr!!.toDouble()).set(Tables.SSO_S_MOVEPOOL.WORK_DATE,dt).set(Tables.SSO_S_MOVEPOOL.MOVEPOOL,"m").where(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(dblBagID)).execute()
            if (iResultCount==0){

            }
        }
        catch(e:ServiceException){
            throw e
        }
        catch(e: Exception) {
            throw BadRequestException(e.message)
        }


        throw NotImplementedError()
        }




    private val log = LoggerFactory.getLogger(this.javaClass)


}
