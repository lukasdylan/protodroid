package id.lukasdylan.grpc.protodroid

import android.content.Context
import id.lukasdylan.grpc.protodroid.internal.service.ProtodroidClientCall
import io.grpc.*

/**
 * Created by Lukas Dylan on 05/08/20.
 */
open class ProtodroidInterceptor(context: Context) : ClientInterceptor {

    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<ReqT, RespT> {
        return ProtodroidClientCall(method, callOptions, next)
    }
}