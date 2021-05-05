package id.lukasdylan.grpc.protodroid.internal.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import id.lukasdylan.grpc.protodroid.R
import id.lukasdylan.grpc.protodroid.internal.ui.bindItem

class InformationAdapter : RecyclerView.Adapter<InformationViewHolder>() {

    private var data: List<Pair<String, String>> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): InformationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.protodroid_item_protodroid_detail_response, parent, false)
        return InformationViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: InformationViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun updateData(data: List<Pair<String, String>>) {
        this.data = data
        notifyDataSetChanged()
    }

}

class InformationViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {

    private val tvValue by bindItem<MaterialTextView>(R.id.tv_value)
    private val tvLabel by bindItem<MaterialTextView>(R.id.tv_label)

    private val params: PrecomputedTextCompat.Params = TextViewCompat.getTextMetricsParams(tvValue)

    init {
        tvValue?.setTextIsSelectable(true)
    }

    fun bind(item: Pair<String, String>) {
        tvLabel?.text = item.first
        tvValue?.setTextFuture(PrecomputedTextCompat.getTextFuture(item.second, params, null))
    }
}
