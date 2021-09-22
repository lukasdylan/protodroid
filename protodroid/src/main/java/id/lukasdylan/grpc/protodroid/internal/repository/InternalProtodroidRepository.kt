package id.lukasdylan.grpc.protodroid.internal.repository

import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDao
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal interface InternalProtodroidRepository {
    suspend fun fetchAllData(): Flow<List<ProtodroidDataEntity>>
    suspend fun fetchSingleData(dataId: Long): Flow<ProtodroidDataEntity>
    suspend fun deleteAllData()
    suspend fun getFilteredData(): Flow<List<ProtodroidDataEntity>>
    fun updateFilter(filterType: FilterType)

    val filterList: MutableList<FilterType>

    enum class FilterType {
        Errors,
        Unique
    }
}

internal class InternalProtodroidRepositoryImpl(
    private val dao: ProtodroidDao,
    defaultUniqueErrors: Boolean = false
) :
    InternalProtodroidRepository {

    override val filterList = if (defaultUniqueErrors)
        mutableListOf(
            InternalProtodroidRepository.FilterType.Unique,
            InternalProtodroidRepository.FilterType.Errors
        )
    else
        mutableListOf()

    /**
     * Filter data with no duplicates or successful responses by default
     */
    override suspend fun getFilteredData(): Flow<List<ProtodroidDataEntity>> {
        return dao.fetchAllData()
            .map { getErrors(it) }
            .map { getDistinct(it) }
    }

    private fun getErrors(list: List<ProtodroidDataEntity>): List<ProtodroidDataEntity> {
        return if (filterList.contains(InternalProtodroidRepository.FilterType.Errors)) {
            list.filter { data -> data.statusCode != 0 }
        } else {
            list
        }
    }

    private fun getDistinct(list: List<ProtodroidDataEntity>): List<ProtodroidDataEntity> {
        return if (filterList.contains(InternalProtodroidRepository.FilterType.Unique)) {
            list.distinctBy { data -> data.serviceName + data.serviceUrl + data.statusCode }
        } else {
            list
        }
    }

    override fun updateFilter(filterType: InternalProtodroidRepository.FilterType) {
        if (filterList.contains(filterType)) {
            filterList.remove(filterType)
        } else {
            filterList.add(filterType)
        }
    }

    override suspend fun deleteAllData() {
        withContext(Dispatchers.IO) {
            dao.deleteAllData()
        }
    }

    override suspend fun fetchAllData(): Flow<List<ProtodroidDataEntity>> = dao.fetchAllData()

    override suspend fun fetchSingleData(dataId: Long): Flow<ProtodroidDataEntity> =
        dao.fetchSingleDataById(dataId)
}
