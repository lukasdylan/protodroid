package id.lukasdylan.grpc.protodroid.internal.database

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
internal data class ProtodroidDataEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "service_url")
    val serviceUrl: String,

    @ColumnInfo(name = "service_name")
    val serviceName: String,

    @ColumnInfo(name = "request_header")
    val requestHeader: String,

    @ColumnInfo(name = "response_header")
    val responseHeader: String,

    @ColumnInfo(name = "request_body")
    val requestBody: String,

    @ColumnInfo(name = "response_body")
    val responseBody: String,

    @ColumnInfo(name = "status_code")
    val statusCode: Int,

    @ColumnInfo(name = "status_level")
    val statusLevel: Int,

    @ColumnInfo(name = "status_name")
    val statusName: String,

    @ColumnInfo(name = "status_desc")
    val statusDescription: String,

    @ColumnInfo(name = "status_error_cause")
    val statusErrorCause: String,

    @ColumnInfo(name = "create_timestamp")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "update_timestamp")
    val lastUpdatedAt: Long = System.currentTimeMillis()
)
