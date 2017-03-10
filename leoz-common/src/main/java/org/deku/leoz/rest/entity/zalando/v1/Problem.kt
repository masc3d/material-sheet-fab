package org.deku.leoz.rest.entity.zalando.v1

import io.swagger.annotations.ApiModel

/**
 * Created by 27694066 on 02.03.2017.
 */
@ApiModel(value = "Problem", description = "Service error")
class Problem(
        val httpStatus: Int?,
        val code: Int?,
        val message: String) {

    constructor(httpStatus: Int, message: String) : this(httpStatus, null, message) {
    }

    constructor(httpStatus: Int, e: Exception) : this(httpStatus, e.message!!) {
    }
}