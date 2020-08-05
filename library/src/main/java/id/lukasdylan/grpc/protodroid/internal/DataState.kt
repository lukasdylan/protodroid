package id.lukasdylan.grpc.protodroid.internal

import androidx.annotation.Keep
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDataEntity
import io.grpc.Metadata
import io.grpc.Status

/**
 * Created by Lukas Dylan on 05/08/20.
 */
@Keep
internal data class DataState(
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

internal fun DataState.transformToEntity(updateExisting: Boolean = false): ProtodroidDataEntity {
    return if (!updateExisting) {
        ProtodroidDataEntity(
            serviceUrl = this.serviceUrl,
            serviceName = this.serviceName,
            requestHeader = this.requestHeader?.toString().orEmpty(),
            responseHeader = this.responseHeader?.toString().orEmpty(),
            requestBody = this.requestBody,
            responseBody = this.responseBody,
            statusCode = this.status?.code?.value() ?: -1,
            statusName = this.status?.code?.name.orEmpty(),
            statusDescription = this.status?.description.orEmpty(),
            statusErrorCause = this.status?.cause?.message.orEmpty(),
            createdAt = this.createdTime,
            lastUpdatedAt = System.currentTimeMillis()
        )
    } else {
        ProtodroidDataEntity(
            id = this.dataId,
            serviceUrl = this.serviceUrl,
            serviceName = this.serviceName,
            requestHeader = this.requestHeader?.toString().orEmpty(),
            responseHeader = this.responseHeader?.toString().orEmpty(),
            requestBody = this.requestBody,
            responseBody = this.responseBody,
            statusCode = this.status?.code?.value() ?: -1,
            statusName = this.status?.code?.name.orEmpty(),
            statusDescription = this.status?.description.orEmpty(),
            statusErrorCause = this.status?.cause?.message.orEmpty(),
            createdAt = this.createdTime,
            lastUpdatedAt = System.currentTimeMillis()
        )
    }
}