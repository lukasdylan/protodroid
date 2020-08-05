package id.lukasdylan.grpc.protodroid.internal

import androidx.recyclerview.widget.DiffUtil
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDataEntity

object ProtodroidDataDiffCallback : DiffUtil.ItemCallback<ProtodroidDataEntity>() {

    override fun areItemsTheSame(
        oldItem: ProtodroidDataEntity,
        newItem: ProtodroidDataEntity
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ProtodroidDataEntity,
        newItem: ProtodroidDataEntity
    ): Boolean {
        return oldItem == newItem
    }
}
