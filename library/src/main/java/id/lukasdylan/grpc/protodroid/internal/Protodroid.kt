package id.lukasdylan.grpc.protodroid.internal

import android.content.Context
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDao
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDatabase
import id.lukasdylan.grpc.protodroid.internal.repository.ProtodroidRepository
import id.lukasdylan.grpc.protodroid.internal.repository.ProtodroidRepositoryImpl
import id.lukasdylan.grpc.protodroid.internal.service.ProtodroidNotificationListener
import id.lukasdylan.grpc.protodroid.internal.service.ProtodroidNotificationListenerImpl

/**
 * Created by Lukas Dylan on 05/08/20.
 */
internal class Protodroid private constructor(context: Context) {

    val protodroidDao: ProtodroidDao by lazy {
        val database = ProtodroidDatabase.initDatabase(context)
        return@lazy database.protodroidDao()
    }

    val repository: ProtodroidRepository by lazy {
        ProtodroidRepositoryImpl(protodroidDao)
    }

    val notificationListener: ProtodroidNotificationListener by lazy {
        ProtodroidNotificationListenerImpl(context)
    }

    @Suppress("unused")
    fun initialize() {
        // force to create repository
        repository.initialize()
    }

    companion object : SingletonHolder<Protodroid, Context>(::Protodroid)
}