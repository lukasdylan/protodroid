package id.lukasdylan.grpc.protodroid.internal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDataEntity
import id.lukasdylan.grpc.protodroid.internal.repository.InternalProtodroidRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal class MainViewModel(
    private val repository: InternalProtodroidRepository,
    defaultUniqueErrors: Boolean
) : ViewModel() {

    enum class FilterType {
        Errors,
        Unique
    }

    private val _dataResponse = MutableStateFlow<List<ProtodroidDataEntity>>(emptyList())
    val dataResponse: Flow<List<ProtodroidDataEntity>> = _dataResponse

    val filterList = if (defaultUniqueErrors)
        mutableListOf(
            FilterType.Unique,
            FilterType.Errors
        )
    else
        mutableListOf()

    init {
        viewModelScope.launch {
            getFilteredData().collect {
                _dataResponse.emit(it)
            }
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
        }
    }

    /**
     * Filter data with no duplicates or successful responses by default
     */
    private suspend fun getFilteredData(): Flow<List<ProtodroidDataEntity>> {
        return repository.fetchAllData()
            .map { getErrors(it) }
            .map { getDistinct(it) }
    }

    private fun getErrors(list: List<ProtodroidDataEntity>): List<ProtodroidDataEntity> {
        return if (filterList.contains(FilterType.Errors)) {
            list.filter { data -> data.statusCode != 0 }
        } else {
            list
        }
    }

    private fun getDistinct(list: List<ProtodroidDataEntity>): List<ProtodroidDataEntity> {
        return if (filterList.contains(FilterType.Unique)) {
            list.distinctBy { data -> data.serviceName + data.serviceUrl + data.statusCode }
        } else {
            list
        }
    }

    fun updateFilter(filterType: FilterType) {
        if (filterList.contains(filterType)) {
            filterList.remove(filterType)
        } else {
            filterList.add(filterType)
        }

        viewModelScope.launch {
            _dataResponse.emit(getFilteredData().first())
        }
    }
}

internal class MainViewModelFactory(
    private val repository: InternalProtodroidRepository,
    private val defaultUniqueErrors: Boolean
) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository, defaultUniqueErrors) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
