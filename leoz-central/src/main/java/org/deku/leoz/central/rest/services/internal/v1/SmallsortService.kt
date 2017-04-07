package org.deku.leoz.central.rest.services.internal.v1

import org.deku.leoz.node.rest.ServiceException
import org.deku.leoz.rest.entity.internal.v1.OutgoingBag
import org.deku.leoz.rest.service.internal.v1.SmallsortService
import sx.rs.auth.ApiKey
import javax.inject.Named
import javax.ws.rs.Path
import org.deku.leoz.util.*


/**
 * Created by 27694066 on 20.02.2017.
 */
@Named
@ApiKey(false)
@Path("internal/v1/smallsort")
class SmallsortService : SmallsortService {

    override fun closebag(outgoingBag: OutgoingBag): Boolean {

        //TODO Check if Client is authorized. (Use API Key?)

        if(outgoingBag.bagReference.isNullOrEmpty()) {
            throw ServiceException(SmallsortService.ErrorCode.BAG_REFERENCE_MISSING)
        }

        if(outgoingBag.leadSeal.isNullOrEmpty()) {
            throw ServiceException(SmallsortService.ErrorCode.LEAD_SEAL_MSSING)
        }

        if(outgoingBag.bagReference!!.length < 12) {
            throw ServiceException(SmallsortService.ErrorCode.BAG_REFERENCE_MISSING_CHECK_DIGIT)
        }

        if(outgoingBag.leadSeal!!.length < 12) {
            throw ServiceException(SmallsortService.ErrorCode.LEAD_SEAL_MISSING_CHECK_DIGIT)
        }

        if(!checkCheckDigit(outgoingBag.bagReference!!)) {
            throw ServiceException(SmallsortService.ErrorCode.BAG_REFERENCE_WRONG_CHECK_DIGIT)
        }

        if(!checkCheckDigit(outgoingBag.leadSeal!!)) {
            throw ServiceException(SmallsortService.ErrorCode.LEAD_SEAL_WRONG_CHECK_DIGIT)
        }

        //TODO

        throw NotImplementedError()
    }
/**
    //TODO: In leoz-common integrieren
    fun checkCheckDigit(fullOrderNo: Double): Boolean{
        val stringOrderNo: String = fullOrderNo.toString()
        return checkCheckDigit(
                stringOrderNo.substring(0, stringOrderNo.length - 1).toDouble(),
                stringOrderNo.substring(stringOrderNo.length - 1).toInt())
    }

    //TODO: In leoz-common integrieren
    fun checkCheckDigit(orderNo: Double, checkDigit: Int): Boolean{
        return (getCheckDigit(orderNo) == checkDigit)
    }

    //TODO: In leoz-common integrieren
    public fun getCheckDigit(orderNo: Double): Int{

        return getCheckDigit(orderNo, intArrayOf(1, 3))

    }

    //TODO: In leoz-common integrieren
    fun getCheckDigit(orderNo: Double, multiplicator: IntArray): Int{

        var count: Int = 0
        var checkDigit: Int = 0

        if(multiplicator.size != 2)
            return -1

        for (c: Char in orderNo.toString().toCharArray()){
            val digit: Int = c.toInt()
            if(count%2 == 0){
                checkDigit += multiplicator[0] * digit
            }else{
                checkDigit += multiplicator[1] * digit
            }
            count++
        }

        val nextMultiple: Int = (Math.floor((count.toDouble() + 10/2) / 10) * 10).toInt()
        val result: Int = nextMultiple - count

        if(result < 0)
            return -result
        else
            return result
    }
 **/
}