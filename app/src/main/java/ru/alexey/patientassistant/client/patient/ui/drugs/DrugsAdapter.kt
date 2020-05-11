/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.ui.drugs

import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.alexey.patientassistant.client.R
import ru.alexey.patientassistant.client.databinding.DrugsItemBinding
import ru.alexey.patientassistant.client.patient.domain.DrugItem
import ru.alexey.patientassistant.client.utils.DateUtils.isDateIsGreatOrEqualThatCurrent

class DrugsAdapter(private val viewModel: DrugsViewModel) :
    ListAdapter<DrugItem, DrugsAdapter.ViewHolder>(DrugDiffCallback()) {

    inner class ViewHolder(private val binding: DrugsItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DrugItem) {
            with(binding) {
                //If drug isn't accepted before
                if (item.realDateTimeOfMedicationReception == null) {
                    drugIsAcceptedImg.visibility = GONE
                }
                else
                {
                    drugIsAcceptedImg.visibility = VISIBLE
                }

                //If drug's acceptance is necessary today
                if (item.realDateTimeOfMedicationReception == null &&
                    viewModel.currentServerDate == item.dateOfDrugReception &&
                    isDateIsGreatOrEqualThatCurrent(item.dateOfDrugReception + 'T' + item.timeOfDrugReception))
                {
                    drugAcceptButton.visibility = VISIBLE
                    drugAcceptButton.isClickable = true
                    drugAcceptButton.setOnClickListener {
                        viewModel.confirmDrug(item.id)
                    }
                } else {
                    drugAcceptButton.isClickable = false
                    drugAcceptButton.visibility = GONE
                }

                //Set text from item
                drugDosageType.text = item.doseTypeName
                drugDose.text =
                    if (item.dose % 1 == 0.toDouble()) {
                        item.dose.toInt().toString()
                    }
                    else
                        item.dose.toString()
                drugTitle.text = item.name
                drugTime.text = item.timeOfDrugReception.take(5)
                if (item.manufacturer.isBlank()) {
                    drugManufacturer.text = ""
                    drugManufacturer.visibility = GONE
                } else {
                    drugManufacturer.text = item.manufacturer
                    drugManufacturer.visibility = VISIBLE
                }
                drugDescription.text = item.description
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