package id.lukasdylan.grpc.protodroid

import androidx.annotation.Keep
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDataEntity
import io.grpc.Metadata
import io.grpc.Status

/**
 * Created by Lukas Dylan on 05/08/20.
 */
@Keep
data class DataState(
    val serviceUrl: String = "",
    val serviceName: String = "",
    val requestHeader: String? = null,
    val responseHeader: String? = null,
    val requestBody: String = "",
    val responseBody: String = "",
    val statusCode: Int? = null,
    val statusLevel: StatusLevel? = StatusLevel.UNDEFINED,
    val statusName: String? = null,
    val statusDescription: String? = null,
    val statusErrorCause: String? = null,
    val createdTime: Long = System.currentTimeMillis()
) {
    enum class StatusLevel {
        UNDEFINED,
        OK,
        ERROR
    }
}

internal fun DataState.transformToEntity(): ProtodroidDataEntity {
    return ProtodroidDataEntity(
        serviceUrl = this.serviceUrl,
        serviceName = this.serviceName,
        requestHeader = this.requestHeader.orEmpty(),
        responseHeader = this.responseHeader.orEmpty(),
        requestBody = this.requestBody,
        responseBody = this.responseBody,
        statusCode = this.statusCode ?: -1,
        statusLevel = this.statusLevel?.ordinal ?: DataState.StatusLevel.UNDEFINED.ordinal,
        statusName = this.statusName.orEmpty(),
        statusDescription = this.statusDescription.orEmpty(),
        statusErrorCause = this.statusErrorCause.orEmpty(),
        createdAt = this.createdTime,
        lastUpdatedAt = System.currentTimeMillis()
    )
}