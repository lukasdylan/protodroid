package id.lukasdylan.grpc.protodroid

import android.content.Context
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDao
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDatabase
import id.lukasdylan.grpc.protodroid.internal.repository.ProtodroidRepository
import id.lukasdylan.grpc.protodroid.internal.repository.ProtodroidRepositoryImpl
import id.lukasdylan.grpc.protodroid.internal.service.ProtodroidNotificationListener
import id.lukasdylan.grpc.protodroid.internal.service.ProtodroidNotificationListenerImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Lukas Dylan on 05/08/20.
 */
class Protodroid private constructor(context: Context) {

    var loggingEnabled: Boolean = false
    var notifySuccessful: Boolean = true
    var defaultUniqueErrors: Boolean = false

    internal val protodroidDao: ProtodroidDao by lazy {
        val database = ProtodroidDatabase.initDatabase(context)
        return@lazy database.protodroidDao()
    }

    private val repository: ProtodroidRepository by lazy {
        ProtodroidRepositoryImpl(protodroidDao)
    }

    private val notificationListener: ProtodroidNotificationListener by lazy {
        ProtodroidNotificationListenerImpl(context)
    }

    suspend fun saveNewData(dataState: DataState) {
        val id = withContext(Dispatchers.IO) {
            repository.saveNewData(dataState)
        }
        if (id != -1L && (notifySuccessful || dataState.statusLevel != DataState.StatusLevel.OK)) {
            notificationListener.sendNotification(
                title = dataState.serviceName.split("/").lastOrNull() ?: dataState.serviceName,
                message = "${dataState.statusName} (${dataState.statusCode})",
                dataId = id,
                serviceName = dataState.serviceName
            )
        }
    }

    companion object : SingletonHolder<Protodroid, Context>(::Protodroid)
}