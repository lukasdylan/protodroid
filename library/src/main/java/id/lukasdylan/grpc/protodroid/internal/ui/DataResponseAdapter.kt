package id.lukasdylan.grpc.protodroid.internal.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.lukasdylan.grpc.protodroid.R
import id.lukasdylan.grpc.protodroid.internal.ProtodroidDataDiffCallback
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDataEntity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_protodroid_response.*
import java.text.SimpleDateFormat
import java.util.*


class DataResponseAdapter(private val listener: (ProtodroidDataEntity) -> Unit) :
    ListAdapter<ProtodroidDataEntity, DataResponseViewHolder>(ProtodroidDataDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataResponseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_protodroid_response, parent, false)
        return DataResponseViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: DataResponseViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }
}

class DataResponseViewHolder(
    override val containerView: View,
    private val listener: (ProtodroidDataEntity) -> Unit
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

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

    @SuppressLint("SetTextI18n")
    fun bind(item: ProtodroidDataEntity) {
        val services = item.serviceName.split("/")
        tv_service_name?.text = services.getOrElse(1) {
            item.serviceName
        }
        tv_status?.text = when (item.statusCode) {
            STATUS_CODE_ON_PROGRESS -> {
                tv_status?.setTextColor(onProgressColorText)
                "On Progress..."
            }
            STATUS_CODE_OK -> {
                tv_status?.setTextColor(successColorText)
                "${item.statusName} (${item.statusCode})"
            }
            else -> {
                tv_status?.setTextColor(failedColorText)
                "${item.statusName} (${item.statusCode})"
            }
        }
        tv_last_updated?.text = "Last Updated: ${getFormattedDate(item.lastUpdatedAt)}"
        root_layout?.setOnClickListener {
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