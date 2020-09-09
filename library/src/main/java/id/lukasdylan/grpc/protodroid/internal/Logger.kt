package id.lukasdylan.grpc.protodroid.internal

import timber.log.Timber

internal fun printLogRequest(lastState: DataState) {
    val stringBuilder = StringBuilder(".")
        .appendLine()
        .appendLine("----- Service URL: -----")
        .appendLine(lastState.serviceUrl)
        .appendLine("----- Service Name: -----")
        .appendLine(lastState.serviceName)
        .appendLine("----- Request Header: -----")
        .appendLine(lastState.requestHeader)
        .appendLine("----- Request Body: -----")
        .appendLine(lastState.requestBody)
    Timber.tag("GRPC").d(stringBuilder.toString())
}

internal fun printLogFullResponse(lastState: DataState) {
    val stringBuilder = StringBuilder(".")
        .appendLine()
        .appendLine("----- Service URL: -----")
        .appendLine(lastState.serviceUrl)
        .appendLine("----- Service Name: -----")
        .appendLine(lastState.serviceName)
        .appendLine("----- Response Header: -----")
        .appendLine(lastState.responseHeader)
        .appendLine("----- Response Body: -----")
        .appendLine(lastState.responseBody)
    Timber.tag("GRPC").d(stringBuilder.toString())
}
