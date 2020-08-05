package id.lukasdylan.grpc.protodroid

import android.content.Context
import id.lukasdylan.grpc.protodroid.internal.Protodroid
import id.lukasdylan.grpc.protodroid.internal.repository.ProtodroidRepository
import id.lukasdylan.grpc.protodroid.internal.service.ProtodroidClientCall
import id.lukasdylan.grpc.protodroid.internal.service.ProtodroidNotificationListener
import io.grpc.*

/**
 * Created by Lukas Dylan on 05/08/20.
 */
open class ProtodroidInterceptor(applicationContext: Context) : ClientInterceptor {

    private var repository: ProtodroidRepository? = null
    private var notificationListener: ProtodroidNotificationListener? = null

    init {
        if (BuildConfig.DEBUG) {
            val protodroidInstance = Protodroid.getInstance(applicationContext)
            repository = protodroidInstance.repository
            notificationListener = protodroidInstance.notificationListener
        }
    }

    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<ReqT, RespT> {
        return ProtodroidClientCall(
            method = method,
            callOptions = callOptions,
            channel = next,
            repository = repository,
            protodroidNotificationListener = notificationListener
        )
    }
}