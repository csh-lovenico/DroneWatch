package tech.tennoji.dronewatch.subscription

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tech.tennoji.dronewatch.databinding.SubscriptionListItemBinding

class SubscriptionItemListener(val listener: (area: String) -> Unit) {
    fun onClick(area: String) {
        listener(area)
    }
}

class SubscriptionItemViewHolder(private val binding: SubscriptionListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(area: String, listener: SubscriptionItemListener) {
        binding.area = area
        binding.listener = listener
    }
}

class SubscriptionListDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

}

class SubscriptionItemAdapter(private val listener: SubscriptionItemListener) :
    ListAdapter<String, SubscriptionItemViewHolder>(SubscriptionListDiffCallback()) {

    private lateinit var binding: SubscriptionListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionItemViewHolder {
        binding =
            SubscriptionListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubscriptionItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubscriptionItemViewHolder, position: Int) {
        val area = getItem(position)
        holder.bind(area, listener)
    }

}