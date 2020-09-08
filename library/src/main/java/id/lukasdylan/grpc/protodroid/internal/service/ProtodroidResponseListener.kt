package id.lukasdylan.grpc.protodroid.internal.service

import io.grpc.Metadata
import io.grpc.Status

internal interface ProtodroidResponseListener {
    fun onUpdateHeaderState(header: Metadata?)
    fun onUpdateResponseBodyState(responseBody: String)
    fun onUpdateStatusState(status: Status?, header: Metadata?)
    fun onFinalState()
}