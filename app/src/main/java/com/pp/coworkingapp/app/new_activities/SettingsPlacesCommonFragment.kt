package com.pp.coworkingapp.app.new_activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pp.coworkingapp.R
import com.pp.coworkingapp.databinding.FragmentSettingsPlacesCommonBinding

class SettingsPlacesCommonFragment : Fragment() {

    private lateinit var binding: FragmentSettingsPlacesCommonBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsPlacesCommonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btBackToMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_settingsPlacesCommonFrag_to_mainPageFragment)
        }
    }
}