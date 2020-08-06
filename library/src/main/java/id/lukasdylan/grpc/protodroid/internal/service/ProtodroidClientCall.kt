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
        state = state.copy(
            requestBody = message.toString(),
            createdTime = System.currentTimeMillis()
        )
        printLogRequest(state)
        super.sendMessage(message)
    }

    override fun start(responseListener: Listener<ResponseObject>?, headers: Metadata?) {
        launch {
            val id = withContext(Dispatchers.IO) {
                repository?.saveNewData(state)
            } ?: DEFAULT_DATA_ID
            state = state.copy(dataId = id)
        }

        state = state.copy(requestHeader = headers)

        val listener = ProtodroidClientCallListener(
            responseListener = responseListener,
            updateStateListener = this
        )

        super.start(listener, headers)
    }

    override fun onUpdateHeaderState(header: Metadata?) {
        state = state.copy(responseHeader = header)
    }

    override fun onUpdateResponseBodyState(responseBody: String) {
        state = state.copy(responseBody = responseBody)
    }

    override fun onUpdateStatusState(status: Status?) {
        state = state.copy(status = status)
    }

    override fun onFinalState() {
        launch {
            printLogFullResponse(state)
            val services = state.serviceName.split("/")
            protodroidNotificationListener?.sendNotification(
                title = services.getOrElse(1) {
                    state.serviceName
                },
                message = "${state.status?.code?.name} (${state.status?.code})"
            )
            withContext(Dispatchers.IO) {
                repository?.updateNewData(state)
            }
        }
    }

    companion object {
        private const val DEFAULT_DATA_ID = -1L
    }
}