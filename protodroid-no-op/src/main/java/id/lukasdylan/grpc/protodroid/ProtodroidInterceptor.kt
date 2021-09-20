package id.lukasdylan.grpc.protodroid

import android.content.Context
import id.lukasdylan.grpc.protodroid.internal.service.ProtodroidClientCall
import io.grpc.*

/**
 * Created by Lukas Dylan on 05/08/20.
 */
@Suppress("UNUSED_PARAMETER")
open class ProtodroidInterceptor internal constructor(
    private val builder: Builder
) : ClientInterceptor {

    constructor(context: Context) : this(Builder(context))

    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<ReqT, RespT> {
        return ProtodroidClientCall(method, callOptions, next)
    }

    class Builder constructor(context: Context) {
        internal var protodroid = Protodroid.getInstance(context.applicationContext)

        fun notifySuccessful(notify: Boolean) = apply {}

        fun loggingEnabled(enabled: Boolean) = apply {}

        fun build(): ProtodroidInterceptor {
            return ProtodroidInterceptor(this)
        }
    }
}