package ru.poas.patientassistant.client.ui.main

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.calendar_button
import kotlinx.android.synthetic.main.activity_main.view.*
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.RecommendationsFragmentBinding
import ru.poas.patientassistant.client.viewmodel.main.RecommendationsViewModel
import java.util.*

class RecommendationsFragment : Fragment() {

    private lateinit var viewModel: RecommendationsViewModel
    private lateinit var binding: RecommendationsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.recommendations_fragment, container, false)

        val cldr = Calendar.getInstance()
        val day = cldr.get(Calendar.DAY_OF_MONTH)
        val month = cldr.get(Calendar.MONTH)
        val year = cldr.get(Calendar.YEAR)

        val picker = DatePickerDialog(activity!!,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            }, year, month, day)

        activity!!.toolbar.apply {
            title = "Рекомендации"
        }
        activity!!.calendar_button.visibility = View.VISIBLE
        activity!!.calendar_button.setOnClickListener {
            picker.show()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RecommendationsViewModel::class.java)
        // TODO: Use a ViewModel
    }


}
