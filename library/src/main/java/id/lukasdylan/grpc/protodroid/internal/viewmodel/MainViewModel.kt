package id.lukasdylan.grpc.protodroid.internal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.lukasdylan.grpc.protodroid.internal.repository.InternalProtodroidRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

internal class MainViewModel(private val repository: InternalProtodroidRepository) : ViewModel(),
    CoroutineScope by MainScope() {

    val dataResponseLiveData = repository.fetchAllData()

    fun deleteAllData() {
        launch(Dispatchers.IO) {
            repository.deleteAllData()
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
