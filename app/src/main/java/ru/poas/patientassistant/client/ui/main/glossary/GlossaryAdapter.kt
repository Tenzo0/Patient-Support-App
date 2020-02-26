package ru.poas.patientassistant.client.ui.main.glossary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.GlossaryFragmentItemBinding
import ru.poas.patientassistant.client.vo.Glossary

/**
 * Adapter which adapt glossary entities from database
 * to the [RecyclerView] with Data Binging
 */
class GlossaryAdapter(private val clickListener: ItemClickListener) :
    ListAdapter<Glossary, GlossaryAdapter.ViewHolder>(DefinitionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context)
            .inflate(R.layout.glossary_fragment_item, parent, false)
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    //Hold view with databinding and click listener for each item
    class ViewHolder private constructor(private val binding: GlossaryFragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Glossary, clickListener: ItemClickListener) {
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
                return ViewHolder(binding)
            }
        }
    }
}

/**
 * Handle each item click
 */
class ItemClickListener(val clickListener: (glossary: Glossary) -> Unit) {
    fun onClick(glossary: Glossary) = clickListener(glossary)
}

/**
 * Diff util for callback in case of difference of definitions in dictionary
 */
class DefinitionDiffCallback : DiffUtil.ItemCallback<Glossary>() {
    override fun areItemsTheSame(oldItem: Glossary, newItem: Glossary): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Glossary, newItem: Glossary): Boolean {
        return oldItem == newItem
    }

}
