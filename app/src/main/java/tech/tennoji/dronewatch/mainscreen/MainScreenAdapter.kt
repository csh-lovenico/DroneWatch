package tech.tennoji.dronewatch.mainscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tech.tennoji.dronewatch.databinding.StatusListItemBinding
import tech.tennoji.dronewatch.network.FenceStatus


class MainListItemListener(val listener: (area: String) -> Unit) {
    fun onClick(area: String) {
        listener(area)
    }
}

class MainListViewHolder(private val binding: StatusListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(status: FenceStatus, listener: MainListItemListener) {
        binding.status = status
        binding.listener = listener
        binding.areaDroneCount.text = String.format("Drones in this area: %d", status.number)
    }
}

class MainListDataDiffCallback : DiffUtil.ItemCallback<FenceStatus>() {
    override fun areItemsTheSame(oldItem: FenceStatus, newItem: FenceStatus): Boolean {
        return oldItem.name == newItem.name && oldItem.number == newItem.number
    }

    override fun areContentsTheSame(oldItem: FenceStatus, newItem: FenceStatus): Boolean {
        return oldItem.name == newItem.name && oldItem.number == newItem.number
    }

}

class MainListAdapter(
    private val listener: MainListItemListener
) : ListAdapter<FenceStatus, MainListViewHolder>(MainListDataDiffCallback()) {

    private lateinit var binding: StatusListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListViewHolder {
        binding = StatusListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainListViewHolder, position: Int) {
        val status = getItem(position)
        holder.bind(status, listener)
    }

}

