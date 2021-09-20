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
@DelicateCoroutinesApi
internal class ProtodroidClientCall<RequestObject, ResponseObject>(
    method: MethodDescriptor<RequestObject, ResponseObject>,
    callOptions: CallOptions,
    channel: Channel,
    private val repository: ProtodroidRepository,
    private val notificationListener: ProtodroidNotificationListener
) : ForwardingClientCall.SimpleForwardingClientCall<RequestObject, ResponseObject>(
    channel.newCall(method, callOptions)
), ProtodroidResponseListener {

    private var state = DataState(
        serviceUrl = channel.authority().orEmpty(),
        serviceName = method.fullMethodName.orEmpty()
    )

    override fun sendMessage(message: RequestObject) {
        state = state.copy(requestBody = message.toString())
        state.printLogRequest()
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
        GlobalScope.launch(Dispatchers.IO) {
            val finalState = state
            finalState.printLogFullResponse()

            val id = repository.saveNewData(finalState)
            if (id != -1L) {
                notificationListener.sendNotification(
                    title = finalState.serviceName.split("/").getOrElse(1) {
                        finalState.serviceName
                    },
                    message = "${finalState.status?.code?.name} (${finalState.status?.code?.value()})",
                    dataId = id,
                    serviceName = finalState.serviceName,
                )
            }
        }
    }
}