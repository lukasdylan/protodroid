package id.lukasdylan.grpc.protodroid.internal.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import id.lukasdylan.grpc.protodroid.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_protodroid_detail_response.*

class InformationAdapter : RecyclerView.Adapter<InformationViewHolder>() {

    private var data: List<Pair<String, String>> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): InformationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_protodroid_detail_response, parent, false)
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

class InformationViewHolder(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {

    private val params: PrecomputedTextCompat.Params = TextViewCompat.getTextMetricsParams(tv_value)

    init {
        tv_value?.setTextIsSelectable(true)
    }

    fun bind(item: Pair<String, String>) {
        tv_label?.text = item.first
        tv_value?.setTextFuture(PrecomputedTextCompat.getTextFuture(item.second, params, null))
    }
}
