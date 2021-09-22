package id.lukasdylan.grpc.protodroid.internal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDataEntity
import id.lukasdylan.grpc.protodroid.internal.repository.InternalProtodroidRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal class MainViewModel(private val repository: InternalProtodroidRepository) : ViewModel() {

    private val _dataResponse = MutableStateFlow<List<ProtodroidDataEntity>>(emptyList())
    val dataResponse: Flow<List<ProtodroidDataEntity>> = _dataResponse

    val filterList = repository.filterList

    init {
        viewModelScope.launch {
            repository.getFilteredData().collect {
                _dataResponse.emit(it)
            }
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
        }
    }

    fun updateFilter(filterType: InternalProtodroidRepository.FilterType) {
        viewModelScope.launch {
            repository.updateFilter(filterType)
            _dataResponse.emit(repository.getFilteredData().first())
        }
    }
}

internal class MainViewModelFactory(private val repository: InternalProtodroidRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
