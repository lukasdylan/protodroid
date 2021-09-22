package id.lukasdylan.grpc.protodroid.internal

import id.lukasdylan.grpc.protodroid.ProtodroidDataState
import timber.log.Timber

internal fun ProtodroidDataState.printLogRequest() {
    val stringBuilder = StringBuilder(".")
        .appendLine()
        .appendLine("----- Service URL: -----")
        .appendLine(serviceUrl)
        .appendLine("----- Service Name: -----")
        .appendLine(serviceName)
        .appendLine("----- Request Header: -----")
        .appendLine(requestHeader)
        .appendLine("----- Request Body: -----")
        .appendLine(requestBody)
    Timber.tag("GRPC").d(stringBuilder.toString())
}

internal fun ProtodroidDataState.printLogFullResponse() {
    val stringBuilder = StringBuilder(".")
        .appendLine()
        .appendLine("----- Service URL: -----")
        .appendLine(serviceUrl)
        .appendLine("----- Service Name: -----")
        .appendLine(serviceName)
        .appendLine("----- Response Header: -----")
        .appendLine(responseHeader)
        .appendLine("----- Response Body: -----")
        .appendLine(responseBody)
    Timber.tag("GRPC").d(stringBuilder.toString())
}
