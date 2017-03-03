package org.deku.leoz.rest.entity.v1

/**
 * Created by 27694066 on 02.03.2017.
 */
@io.swagger.annotations.ApiModel(value = "Problem", description = "Service error")
class Problem(
        val httpStatus: Int?,
        val code: Int?,
        val message: String) {

    constructor(httpStatus: Int, message: String) : this(httpStatus, null, message) {
    }

    constructor(httpStatus: Int, e: Exception) : this(httpStatus, e.message!!) {
    }
}