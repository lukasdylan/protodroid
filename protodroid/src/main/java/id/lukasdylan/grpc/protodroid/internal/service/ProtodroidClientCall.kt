package id.lukasdylan.grpc.protodroid.internal.service

import id.lukasdylan.grpc.protodroid.ProtodroidDataState
import id.lukasdylan.grpc.protodroid.Protodroid
import id.lukasdylan.grpc.protodroid.internal.printLogFullResponse
import id.lukasdylan.grpc.protodroid.internal.printLogRequest
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
    private val protodroid: Protodroid
) : ForwardingClientCall.SimpleForwardingClientCall<RequestObject, ResponseObject>(
    channel.newCall(method, callOptions)
), ProtodroidResponseListener {

    private var state = ProtodroidDataState(
        serviceUrl = channel.authority().orEmpty(),
        serviceName = method.fullMethodName.orEmpty()
    )

    override fun sendMessage(message: RequestObject) {
        state = state.copy(requestBody = message.toString())
        if (protodroid.loggingEnabled) state.printLogRequest()
        super.sendMessage(message)
    }

    override fun start(responseListener: Listener<ResponseObject>?, headers: Metadata?) {
        state = state.copy(requestHeader = headers.toString())
        val listener = ProtodroidClientCallListener(
            responseListener = responseListener,
            updateStateListener = this
        )
        super.start(listener, headers)
    }

    override fun onUpdateHeaderState(header: Metadata?) {
        if (state.responseHeader == null) {
            state = state.copy(responseHeader = header.toString())
        }
    }

    override fun onUpdateResponseBodyState(responseBody: String) {
        state = state.copy(responseBody = responseBody)
    }

    override fun onUpdateStatusState(status: Status?, header: Metadata?) {
        val statusLevel = if (status?.code == Status.Code.OK)
            ProtodroidDataState.StatusLevel.OK
        else
            ProtodroidDataState.StatusLevel.ERROR

        state = state.copy(
            statusCode = status?.code?.value(),
            statusLevel = statusLevel,
            statusDescription = status?.description,
            statusName = status?.code?.name,
            statusErrorCause = status?.cause?.message
        )
        if (state.responseHeader == null) {
            state = state.copy(responseHeader = header.toString())
        }
    }

    override fun onFinalState() {
        val finalState = state
        if (protodroid.loggingEnabled) finalState.printLogFullResponse()
        GlobalScope.launch {
            protodroid.saveNewData(finalState)
        }
    }
}