package ru.poas.patientassistant.client.patient.ui.drugs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.DrugsItemBinding
import ru.poas.patientassistant.client.patient.domain.DrugItem

class DrugsAdapter: ListAdapter<DrugItem, DrugsAdapter.ViewHolder>(
    DrugDiffCallback()
) {

    class ViewHolder(private val binding: DrugsItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DrugItem) {
            with(binding) {

                drugDosageType.text = item.doseTypeName
                drugDose.text =
                    if (item.dose % 1 == 0.toDouble()) {
                        item.dose.toInt().toString()
                    }
                    else
                        item.dose.toString()
                drugTitle.text = item.name
                drugTime.text = item.timeOfDrugReception.take(5)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DrugsItemBinding.inflate(
                    layoutInflater, parent,false
                )
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context)
            .inflate(R.layout.drugs_item, parent, false)
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class DrugDiffCallback: DiffUtil.ItemCallback<DrugItem>() {
        override fun areItemsTheSame(oldItem: DrugItem, newItem: DrugItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: DrugItem, newItem: DrugItem): Boolean {
            return oldItem.dose == newItem.dose
                    && oldItem.doseTypeName == newItem.doseTypeName
                    && oldItem.timeOfDrugReception == newItem.timeOfDrugReception
                    && oldItem.description == newItem.description
                    && oldItem.manufacturer == newItem.manufacturer
        }
    }
}