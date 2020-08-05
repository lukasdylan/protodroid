package id.lukasdylan.grpc.protodroid.internal

import androidx.annotation.Keep
import io.grpc.Metadata
import io.grpc.Status

/**
 * Created by Lukas Dylan on 05/08/20.
 */
@Keep
internal data class ProtodroidDataState(
    val dataId: Long = -1,
    val serviceUrl: String = "",
    val serviceName: String = "",
    val requestHeader: Metadata? = null,
    val responseHeader: Metadata? = null,
    val requestBody: String = "",
    val responseBody: String = "",
    val status: Status? = null,
    val createdTime: Long = 0L
)