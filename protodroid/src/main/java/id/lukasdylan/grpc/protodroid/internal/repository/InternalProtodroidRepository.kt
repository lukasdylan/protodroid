package id.lukasdylan.grpc.protodroid.internal.repository

import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDao
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal interface InternalProtodroidRepository {
    suspend fun fetchAllData(): Flow<List<ProtodroidDataEntity>>
    suspend fun fetchSingleData(dataId: Long): Flow<ProtodroidDataEntity>
    suspend fun deleteAllData()
}

internal class InternalProtodroidRepositoryImpl(private val dao: ProtodroidDao) :
    InternalProtodroidRepository {

    override suspend fun deleteAllData() {
        withContext(Dispatchers.IO) {
            dao.deleteAllData()
        }
    }

    override suspend fun fetchAllData(): Flow<List<ProtodroidDataEntity>> = dao.fetchAllData()

    override suspend fun fetchSingleData(dataId: Long): Flow<ProtodroidDataEntity> =
        dao.fetchSingleDataById(dataId)
}
