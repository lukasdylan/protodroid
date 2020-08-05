package id.lukasdylan.grpc.protodroid.internal

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Created by Lukas Dylan on 05/08/20.
 */
internal class ProtodroidClientCall<RequestObject, ResponseObject>(
    method: MethodDescriptor<RequestObject, ResponseObject>,
    callOptions: CallOptions,
    channel: Channel,
    private val additionalHeader: Map<String, String>
) : ForwardingClientCall.SimpleForwardingClientCall<RequestObject, ResponseObject>(
    channel.newCall(method, callOptions)
), CoroutineScope by MainScope() {

    private var state = ProtodroidDataState(
        serviceUrl = channel.authority().orEmpty(),
        serviceName = method.fullMethodName.orEmpty()
    )

    override fun sendMessage(message: RequestObject) {
        state = state.copy(
            requestBody = message.toString(),
            createdTime = System.currentTimeMillis()
        )
        printLogRequest(state)
        super.sendMessage(message)
    }

    override fun start(responseListener: Listener<ResponseObject>?, headers: Metadata?) {
        launch {
            state = state.copy(dataId = 0)
        }
        additionalHeader.forEach {
            val customHeadKey = Metadata.Key.of(it.key, Metadata.ASCII_STRING_MARSHALLER)
            headers?.put(customHeadKey, it.value)
        }

        state = state.copy(requestHeader = headers)

        super.start(responseListener, headers)
    }

    companion object {
        private const val DEFAULT_DATA_ID = -1L
    }
}