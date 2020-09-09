package id.lukasdylan.grpc.protodroid.internal.repository

import id.lukasdylan.grpc.protodroid.internal.DataState
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDao
import id.lukasdylan.grpc.protodroid.internal.transformToEntity

/**
 * Created by Lukas Dylan on 05/08/20.
 */
internal interface ProtodroidRepository {
    suspend fun saveNewData(state: DataState): Long
    fun initialize() { /*do nothing*/
    }
}

internal class ProtodroidRepositoryImpl(private val dao: ProtodroidDao) : ProtodroidRepository {
    override suspend fun saveNewData(state: DataState): Long {
        return dao.insertData(state.transformToEntity())
    }
}