package ru.poas.patientassistant.client.patient.ui.glossary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.GlossaryFragmentItemBinding
import ru.poas.patientassistant.client.patient.vo.GlossaryItem

/**
 * Adapter which adapt glossary entities from database
 * to the [RecyclerView] with Data Binging
 */
class GlossaryAdapter(private val clickListener: ItemClickListener) :
    ListAdapter<GlossaryItem, GlossaryAdapter.ViewHolder>(
        GlossaryDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context)
            .inflate(R.layout.glossary_fragment_item, parent, false)
        return ViewHolder.from(
            parent
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    //Hold view with databinding and click listener for each item
    class ViewHolder private constructor(private val binding: GlossaryFragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GlossaryItem, clickListener: ItemClickListener) {
            binding.glossaryItem = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GlossaryFragmentItemBinding.inflate(
                    layoutInflater, parent, false
                )
                return ViewHolder(
                    binding
                )
            }
        }
    }
}

/**
 * Handle each item click
 */
class ItemClickListener(val clickListener: (glossaryItem: GlossaryItem) -> Unit) {
    fun onClick(glossaryItem: GlossaryItem) = clickListener(glossaryItem)
}

/**
 * Diff util for callback in case of difference of definitions in dictionary
 */
class GlossaryDiffCallback : DiffUtil.ItemCallback<GlossaryItem>() {
    override fun areItemsTheSame(oldItem: GlossaryItem, newItem: GlossaryItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: GlossaryItem, newItem: GlossaryItem): Boolean {
        return oldItem == newItem
    }

}
