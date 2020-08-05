package id.lukasdylan.grpc.protodroid

import android.content.Context
import io.grpc.*

/**
 * Created by Lukas Dylan on 05/08/20.
 */
open class ProtodroidInterceptor @JvmOverloads constructor(
    applicationContext: Context,
    private val additionalHeader: Map<String, String> = emptyMap()
) : ClientInterceptor {

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>?,
        callOptions: CallOptions?,
        next: Channel?
    ): ClientCall<ReqT, RespT> {
        TODO("Not yet implemented")
    }

}