package id.lukasdylan.grpc.protodroid.internal.repository

import id.lukasdylan.grpc.protodroid.ProtodroidDataState
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDao
import id.lukasdylan.grpc.protodroid.transformToEntity

/**
 * Created by Lukas Dylan on 05/08/20.
 */
interface ProtodroidRepository {
    suspend fun saveNewData(state: ProtodroidDataState): Long
}

internal class ProtodroidRepositoryImpl(private val dao: ProtodroidDao) : ProtodroidRepository {
    override suspend fun saveNewData(state: ProtodroidDataState): Long {
        return try {
            dao.insertData(state.transformToEntity())
        } catch (ex: Exception) {
            ex.printStackTrace()
            -1L
        }
    }
}