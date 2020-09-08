package id.lukasdylan.grpc.protodroid.internal

import timber.log.Timber

internal fun printLogRequest(lastState: DataState) {
    val stringBuilder = StringBuilder(".")
        .appendln()
        .appendln("----- Service URL: -----")
        .appendln(lastState.serviceUrl)
        .appendln("----- Service Name: -----")
        .appendln(lastState.serviceName)
        .appendln("----- Request Header: -----")
        .appendln(lastState.requestHeader)
        .appendln("----- Request Body: -----")
        .appendln(lastState.requestBody)
    Timber.tag("GRPC").d(stringBuilder.toString())
}

internal fun printLogFullResponse(lastState: DataState) {
    val stringBuilder = StringBuilder(".")
        .appendln()
        .appendln("----- Service URL: -----")
        .appendln(lastState.serviceUrl)
        .appendln("----- Service Name: -----")
        .appendln(lastState.serviceName)
        .appendln("----- Response Header: -----")
        .appendln(lastState.responseHeader)
        .appendln("----- Response Body: -----")
        .appendln(lastState.responseBody)
    Timber.tag("GRPC").d(stringBuilder.toString())
}
