package id.lukasdylan.grpc.protodroid

import android.content.Context
import id.lukasdylan.grpc.protodroid.internal.service.ProtodroidClientCall
import io.grpc.*

/**
 * Created by Lukas Dylan on 05/08/20.
 */
open class ProtodroidInterceptor internal constructor(
    private val builder: Builder
) : ClientInterceptor {

    constructor(context: Context) : this(Builder(context))

    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<ReqT, RespT> {
        return ProtodroidClientCall(
            method = method,
            callOptions = callOptions,
            channel = next,
            protodroid = builder.protodroid
        )
    }

    class Builder constructor(context: Context) {
        private var notifySuccessful: Boolean = true
        private var loggingEnabled: Boolean = false
        private var defaultUniqueErrors: Boolean = false
        internal var protodroid = Protodroid.getInstance(context.applicationContext)


        fun notifySuccessful(notify: Boolean) = apply {
            notifySuccessful = notify
        }

        fun loggingEnabled(enabled: Boolean) = apply {
            loggingEnabled = enabled
        }

        fun defaultUniqueErrors(enabled: Boolean) = apply {
            defaultUniqueErrors = enabled
        }

        fun build(): ProtodroidInterceptor {
            protodroid.loggingEnabled = loggingEnabled
            protodroid.notifySuccessful = notifySuccessful
            protodroid.defaultUniqueErrors = defaultUniqueErrors
            return ProtodroidInterceptor(this)
        }
    }
}