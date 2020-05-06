/*
 * Copyright (c) Alexey Barykin 2020.
 */

package ru.poas.patientassistant.client.patient.ui.drugs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.DrugsItemBinding
import ru.poas.patientassistant.client.patient.domain.DrugItem
import ru.poas.patientassistant.client.utils.DateUtils.databaseSimpleDateFormat
import java.util.*

class DrugsAdapter(private val viewModel: DrugsViewModel) :
    ListAdapter<DrugItem, DrugsAdapter.ViewHolder>(DrugDiffCallback()) {

    inner class ViewHolder(private val binding: DrugsItemBinding): RecyclerView.ViewHolder(binding.root) {
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

                /*
                    TODO it doesn't consider situation when local device date is different from server date
                 */
                if (databaseSimpleDateFormat.format(Date()) == item.dateOfDrugReception &&
                            item.realDateTimeOfMedicationReception == null) {
                    drugAcceptButton.visibility = View.VISIBLE
                    drugAcceptButton.setOnClickListener {
                        viewModel.confirmDrug(item.id, item.drugUnitId)
                    }
                }
                else drugAcceptButton.visibility = View.GONE
            }
        }
    }

    private fun createViewHolderFrom(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DrugsItemBinding.inflate(
            layoutInflater, parent,false
        )
        return ViewHolder(binding)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context)
            .inflate(R.layout.drugs_item, parent, false)
        return createViewHolderFrom(parent)
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
            return oldItem == newItem
        }
    }
}