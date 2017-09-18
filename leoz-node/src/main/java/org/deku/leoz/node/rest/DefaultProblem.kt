package org.deku.leoz.node.rest

import org.zalando.problem.Exceptional
import org.zalando.problem.Status
import org.zalando.problem.StatusType
import org.zalando.problem.ThrowableProblem
import java.net.URI
import javax.ws.rs.core.Response

/**
 * Default problem kt implementation
 * Created by masc on 28/03/2017.
 */
class DefaultProblem(
        type: URI = URI.create("about:blank"),
        title: String? = null,
        status: Status = Status.BAD_REQUEST,
        detail: String? = null,
        instance: URI? = null,
        cause: ThrowableProblem? = null,
        parameters: Map<String, Any>? = null
) :
        org.zalando.problem.AbstractThrowableProblem(
                type,
                title,
                status,
                detail,
                instance,
                cause,
                parameters
        ) {

    /**
     * Constructor for use with JAX-RS (Response.Status)
     */
    constructor(
            type: URI = URI.create("about:blank"),
            title: String? = null,
            status: Response.StatusType,
            detail: String? = null,
            instance: URI? = null,
            cause: ThrowableProblem? = null,
            parameters: Map<String, Any>? = null
    ) :
            this(
                    type = type,
                    title = title,
                    status = Status.values().first { it.statusCode == status.statusCode },
                    detail = detail,
                    instance = instance,
                    cause = cause,
                    parameters = parameters
            )

    override fun getCause(): Exceptional? {
        return super.cause
    }
}
