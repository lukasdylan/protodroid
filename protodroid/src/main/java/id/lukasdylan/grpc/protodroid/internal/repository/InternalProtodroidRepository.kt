package id.lukasdylan.grpc.protodroid.internal.repository

import androidx.lifecycle.LiveData
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDao
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDataEntity

interface InternalProtodroidRepository {
    fun fetchAllData(): LiveData<List<ProtodroidDataEntity>>
    fun fetchSingleData(dataId: Long): LiveData<ProtodroidDataEntity>
    suspend fun deleteAllData()
}

internal class InternalProtodroidRepositoryImpl(private val dao: ProtodroidDao) : InternalProtodroidRepository {

    override suspend fun deleteAllData() {
        dao.deleteAllData()
    }

    override fun fetchAllData(): LiveData<List<ProtodroidDataEntity>> = dao.fetchAllData()

    override fun fetchSingleData(dataId: Long): LiveData<ProtodroidDataEntity> =
        dao.fetchSingleDataById(dataId)
}
