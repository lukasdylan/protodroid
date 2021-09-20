package id.lukasdylan.grpc.protodroid

import android.content.Context

@Suppress("unused")
class Protodroid private constructor(context: Context) {

    var loggingEnabled: Boolean = false
    var notifySuccessful: Boolean = true

    suspend fun saveNewData(dataState: ProtodroidDataState) {}

    fun initialize() {}

    companion object : SingletonHolder<Protodroid, Context>(::Protodroid)
}