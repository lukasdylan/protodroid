package id.lukasdylan.grpc.protodroid.internal.service

import io.grpc.ClientCall
import io.grpc.Metadata
import io.grpc.Status

internal class ProtodroidClientCallListener<T>(
    private val responseListener: ClientCall.Listener<T>?,
    private val updateStateListener: ProtodroidResponseListener
) : ClientCall.Listener<T>() {

    override fun onMessage(message: T) {
        updateStateListener.onUpdateResponseBodyState(message.toString())
        responseListener?.onMessage(message)
    }

    override fun onHeaders(headers: Metadata?) {
        updateStateListener.onUpdateHeaderState(headers)
        responseListener?.onHeaders(headers)
    }

    override fun onClose(status: Status?, trailers: Metadata?) {
        updateStateListener.onUpdateStatusState(status, trailers)
        updateStateListener.onFinalState()
        responseListener?.onClose(status, trailers)
    }

    override fun onReady() {
        responseListener?.onReady()
    }
}
