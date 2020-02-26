package ru.poas.patientassistant.client.ui.main.glossary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.activity_main.*
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.GlossaryDetailsFragmentBinding

class GlossaryDetailsFragment : Fragment() {

    private val args: GlossaryDetailsFragmentArgs by navArgs()

    private lateinit var binding: GlossaryDetailsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.glossary_details_fragment,
            container,
            false
        )
        init()
        return binding.root
    }

    private fun init() {
        with(binding) {
            requireActivity().toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
            requireActivity().toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Safe Args (Navigation Architecture) to pass data
         * from Glossary to the specific item
         */
        val titleView: TextView = view.findViewById(R.id.title)
        titleView.text = args.title

        val contentView: TextView = view.findViewById(R.id.content)
        contentView.text = args.content
    }
}