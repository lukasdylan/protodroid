package id.lukasdylan.grpc.protodroid.internal.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import id.lukasdylan.grpc.protodroid.R
import id.lukasdylan.grpc.protodroid.internal.ProtodroidDataDiffCallback
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDataEntity
import java.text.SimpleDateFormat
import java.util.*


class DataResponseAdapter(private val listener: (ProtodroidDataEntity) -> Unit) :
    ListAdapter<ProtodroidDataEntity, DataResponseViewHolder>(ProtodroidDataDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataResponseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.protodroid_item_protodroid_response, parent, false)
        return DataResponseViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: DataResponseViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }
}

class DataResponseViewHolder(
    containerView: View,
    private val listener: (ProtodroidDataEntity) -> Unit
) : RecyclerView.ViewHolder(containerView) {

    private val successColorText: Int =
        ContextCompat.getColor(
            containerView.context,
            android.R.color.holo_green_dark
        )

    private val failedColorText: Int =
        ContextCompat.getColor(
            containerView.context,
            android.R.color.holo_red_dark
        )

    private val onProgressColorText: Int =
        ContextCompat.getColor(
            containerView.context,
            android.R.color.holo_blue_dark
        )

    private val tvServiceName by bindItem<MaterialTextView>(R.id.tv_service_name)
    private val tvStatus by bindItem<MaterialTextView>(R.id.tv_status)
    private val tvLastUpdated by bindItem<MaterialTextView>(R.id.tv_last_updated)
    private val rootLayout by bindItem<ConstraintLayout>(R.id.root_layout)

    @SuppressLint("SetTextI18n")
    fun bind(item: ProtodroidDataEntity) {
        val services = item.serviceName.split("/")
        tvServiceName?.text = services.getOrElse(1) {
            item.serviceName
        }
        tvStatus?.text = when (item.statusCode) {
            STATUS_CODE_ON_PROGRESS -> {
                tvStatus?.setTextColor(onProgressColorText)
                "On Progress..."
            }
            STATUS_CODE_OK -> {
                tvStatus?.setTextColor(successColorText)
                "${item.statusName} (${item.statusCode})"
            }
            else -> {
                tvStatus?.setTextColor(failedColorText)
                "${item.statusName} (${item.statusCode})"
            }
        }
        tvLastUpdated?.text = "Last Updated: ${getFormattedDate(item.lastUpdatedAt)}"
        rootLayout?.setOnClickListener {
            listener.invoke(item)
        }
    }

    private fun getFormattedDate(timeInMillis: Long): String {
        val formatter = SimpleDateFormat("d MMM yyyy HH:mm:ss.SSS", Locale.getDefault())
        return formatter.format(Date(timeInMillis))
    }

    companion object {
        private const val STATUS_CODE_ON_PROGRESS = -1
        private const val STATUS_CODE_OK = 0
    }
}