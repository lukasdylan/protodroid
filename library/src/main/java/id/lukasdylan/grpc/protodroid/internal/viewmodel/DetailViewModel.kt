package id.lukasdylan.grpc.protodroid.internal.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDataEntity
import id.lukasdylan.grpc.protodroid.internal.repository.InternalProtodroidRepository
import java.text.SimpleDateFormat
import java.util.*

class DetailViewModel(repository: InternalProtodroidRepository, dataId: Long) : ViewModel() {

    private val detailResponse = repository.fetchSingleData(dataId)
    val overviewInfo: LiveData<List<Pair<String, String>>> = Transformations.map(detailResponse) {
        return@map it.transformToOverviewInformation()
    }
    val requestInfo: LiveData<List<Pair<String, String>>> = Transformations.map(detailResponse) {
        return@map it.transformToRequestInformation()
    }
    val responseInfo: LiveData<List<Pair<String, String>>> = Transformations.map(detailResponse) {
        return@map it.transformToResponseInformation()
    }

    private fun ProtodroidDataEntity.transformToOverviewInformation(): List<Pair<String, String>> {
        val data = mutableListOf<Pair<String, String>>()
        this.serviceUrl.takeIf { it.isNotBlank() }?.let {
            data.add("Service URL" to it)
        }
        this.serviceName.takeIf { it.isNotBlank() }?.let {
            data.add("Service Name" to it)
        }
        data.add("Created Time" to getFormattedDate(this.createdAt))
        data.add("Last Updated Time" to getFormattedDate(this.lastUpdatedAt))
        val lengthOfRequest = this.lastUpdatedAt - this.createdAt
        data.add("Length Time of Request" to "$lengthOfRequest ms")
        when (this.statusCode) {
            -1 -> {
                data.add("Status" to "On Progress...")
            }
            0 -> {
                data.add("Status" to "${this.statusName} (${this.statusCode})")
            }
            else -> {
                data.add("Status" to "${this.statusName} (${this.statusCode})")
                this.statusDescription.takeIf { it.isNotBlank() }?.let {
                    data.add("Status Description" to it)
                }
                this.statusErrorCause.takeIf { it.isNotBlank() }?.let {
                    data.add("Status Error Caused" to it)
                }
            }
        }
        return data
    }

    private fun ProtodroidDataEntity.transformToRequestInformation(): List<Pair<String, String>> {
        val data = mutableListOf<Pair<String, String>>()
        this.requestHeader.takeIf { it.isNotBlank() }?.let {
            data.add("Header" to it)
        }
        this.requestBody.takeIf { it.isNotBlank() }?.let {
            data.add("Request Body" to it)
        }
        return data
    }

    private fun ProtodroidDataEntity.transformToResponseInformation(): List<Pair<String, String>> {
        val data = mutableListOf<Pair<String, String>>()
        this.responseHeader.takeIf { it.isNotBlank() }?.let {
            data.add("Header" to it)
        }
        this.responseBody.takeIf { it.isNotBlank() }?.let {
            data.add("Response Body" to it)
        }
        return data
    }

    private fun getFormattedDate(timeInMillis: Long): String {
        val formatter = SimpleDateFormat("d MMM yyyy HH:mm:ss", Locale.getDefault())
        return formatter.format(Date(timeInMillis))
    }
}

internal class DetailViewModelFactory(
    private val repository: InternalProtodroidRepository,
    private val dataId: Long
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(repository, dataId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}