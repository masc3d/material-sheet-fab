package org.deku.leoz.node.rest

import org.zalando.problem.Exceptional
import org.zalando.problem.ThrowableProblem
import java.net.URI
import javax.ws.rs.core.Response

/**
 * Created by masc on 28/03/2017.
 */
class DefaultProblem(
        type: URI = URI.create("about:blank"),
        title: String? = null,
        status: Response.StatusType = Response.Status.BAD_REQUEST,
        detail: String? = null,
        instance: URI? = null,
        cause: ThrowableProblem? = null,
        parameters: Map<String, Any>? = null)
    : org.zalando.problem.AbstractThrowableProblem(
        type,
        title,
        status,
        detail,
        instance,
        cause,
        parameters) {

    override fun getCause(): Exceptional? {
        return super.cause
    }
}