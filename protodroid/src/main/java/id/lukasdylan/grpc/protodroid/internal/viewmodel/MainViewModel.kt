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

    private val _filterListFlow: MutableStateFlow<List<FilterType>> = if (defaultUniqueErrors)
        MutableStateFlow(
            mutableListOf(
                FilterType.Unique,
                FilterType.Errors
            )
        )
    else
        MutableStateFlow(mutableListOf())
    val filterListFlow = _filterListFlow

    init {
        viewModelScope.launch {
            repository.fetchAllData().combine(filterListFlow) { dataLogList, filterList ->
                dataLogList.applyFilters(filterList)
            }.collect { finalList ->
                _dataResponse.emit(finalList)
            }
        }
    }

    private fun List<ProtodroidDataEntity>.applyFilters(filterList: List<FilterType>): List<ProtodroidDataEntity> {
        return this.getErrors(filterList).getDistinct(filterList)
    }

    fun deleteAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
        }
    }

    private fun List<ProtodroidDataEntity>.getErrors(list: List<FilterType>): List<ProtodroidDataEntity> {
        return if (list.contains(FilterType.Errors)) {
            this.filter { data -> data.statusCode != 0 }
        } else {
            this
        }
    }

    private fun List<ProtodroidDataEntity>.getDistinct(list: List<FilterType>): List<ProtodroidDataEntity> {
        return if (list.contains(FilterType.Unique)) {
            this.distinctBy { data -> data.serviceName + data.serviceUrl + data.statusCode }
        } else {
            this
        }
    }

    fun updateFilter(filterType: FilterType) {
        viewModelScope.launch {
            val currentFilterList = filterListFlow.first() as MutableList
            if (currentFilterList.contains(filterType)) {
                currentFilterList.remove(filterType)
            } else {
                currentFilterList.add(filterType)
            }
            _filterListFlow.emit(currentFilterList)

            // Fetch all data again, otherwise we won't be able to re-add filtered out items
            _dataResponse.emit(
                repository
                    .fetchAllData()
                    .first()
                    .applyFilters(currentFilterList)
            )
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
