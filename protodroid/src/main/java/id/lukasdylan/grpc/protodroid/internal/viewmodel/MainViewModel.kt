package id.lukasdylan.grpc.protodroid.internal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.lukasdylan.grpc.protodroid.internal.FilterType
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDataEntity
import id.lukasdylan.grpc.protodroid.internal.repository.InternalProtodroidRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

internal class MainViewModel(
    private val repository: InternalProtodroidRepository,
    defaultUniqueErrors: Boolean
) : ViewModel() {

    private val _dataResponse = MutableStateFlow<List<ProtodroidDataEntity>>(emptyList())
    val dataResponse: Flow<List<ProtodroidDataEntity>> = _dataResponse

    private val _filterListFlow: MutableStateFlow<List<FilterType>> = if (defaultUniqueErrors)
        MutableStateFlow(FilterType.values().toList())
    else
        MutableStateFlow(emptyList())
    val filterListFlow: Flow<List<FilterType>> = _filterListFlow

    init {
        viewModelScope.launch {
            repository.fetchAllData().combine(filterListFlow) { dataLogList, filterList ->
                dataLogList filterWith filterList
            }.collect { finalList ->
                _dataResponse.emit(finalList)
            }
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
        }
    }

    private infix fun List<ProtodroidDataEntity>.filterWith(filters: List<FilterType>): List<ProtodroidDataEntity> {
        return when {
            filters.contains(FilterType.Errors) -> {
                Timber.tag("Error FilterType").d("Have error filter")
                this.filter { data -> data.statusCode != 0 }
            }
            filters.contains(FilterType.Unique) -> {
                Timber.tag("Unique FilterType").d("Have unique filter")
                this.distinctBy { data -> data.serviceName + data.serviceUrl + data.statusCode }
            }
            else -> {
                Timber.tag("No FilterType").d("No Filter")
                this
            }
        }.also {
            Timber.tag("Final List").d(it.joinToString(", "))
        }
    }

    fun updateFilter(filterType: FilterType) {
        viewModelScope.launch {
            val currentFilterList = filterListFlow.first().toMutableList()
            if (currentFilterList.contains(filterType)) {
                currentFilterList.remove(filterType)
            } else {
                currentFilterList.add(filterType)
            }
            _filterListFlow.emit(currentFilterList)
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
