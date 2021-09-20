package id.lukasdylan.grpc.protodroid

@Suppress("unused")
data class ProtodroidDataState(
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