package id.lukasdylan.grpc.protodroid.internal.service

import id.lukasdylan.grpc.protodroid.internal.DataState
import id.lukasdylan.grpc.protodroid.internal.printLogFullResponse
import id.lukasdylan.grpc.protodroid.internal.printLogRequest
import id.lukasdylan.grpc.protodroid.internal.repository.ProtodroidRepository
import io.grpc.*
import kotlinx.coroutines.*

/**
 * Created by Lukas Dylan on 05/08/20.
 */
internal class ProtodroidClientCall<RequestObject, ResponseObject>(
    method: MethodDescriptor<RequestObject, ResponseObject>,
    callOptions: CallOptions,
    channel: Channel,
    private val repository: ProtodroidRepository?,
    private val protodroidNotificationListener: ProtodroidNotificationListener?
) : ForwardingClientCall.SimpleForwardingClientCall<RequestObject, ResponseObject>(
    channel.newCall(method, callOptions)
), CoroutineScope by MainScope(), ProtodroidResponseListener {

    private var state = DataState(
        serviceUrl = channel.authority().orEmpty(),
        serviceName = method.fullMethodName.orEmpty()
    )

    override fun sendMessage(message: RequestObject) {
        state = state.copy(requestBody = message.toString())
        printLogRequest(state)
        super.sendMessage(message)
    }

    override fun start(responseListener: Listener<ResponseObject>?, headers: Metadata?) {
        state = state.copy(requestHeader = headers)
        val listener = ProtodroidClientCallListener(
            responseListener = responseListener,
            updateStateListener = this
        )
        super.start(listener, headers)
    }

    override fun onUpdateHeaderState(header: Metadata?) {
        if (state.responseHeader == null) {
            state = state.copy(responseHeader = header)
        }
    }

    override fun onUpdateResponseBodyState(responseBody: String) {
        state = state.copy(responseBody = responseBody)
    }

    override fun onUpdateStatusState(status: Status?, header: Metadata?) {
        state = state.copy(status = status)
        if (state.responseHeader == null) {
            state = state.copy(responseHeader = header)
        }
    }

    override fun onFinalState() {
        printLogFullResponse(state)
        launch {
            val id = withContext(Dispatchers.IO) {
                repository?.saveNewData(state)
            }
            protodroidNotificationListener?.sendNotification(
                title = state.serviceName.split("/").getOrElse(1) {
                    state.serviceName
                },
                message = "${state.status?.code?.name} (${state.status?.code?.value()})",
                dataId = id ?: 0L,
                serviceName = state.serviceName,
                serviceGroup = state.serviceName
                    .split("/")
                    .getOrNull(0)?.replace("proto.", "")
                    .orEmpty()
            )
        }
    }
}