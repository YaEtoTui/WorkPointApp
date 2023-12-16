package com.pp.coworkingapp.app.new_activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.retrofit.api.MainApi
import com.pp.coworkingapp.app.retrofit.domain.Common
import com.pp.coworkingapp.databinding.FragmentAddNewPlaceCommonBinding
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddNewPlaceCommonFragment : Fragment() {

    private lateinit var binding: FragmentAddNewPlaceCommonBinding
    private lateinit var mainApi: MainApi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNewPlaceCommonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainApi = Common.retrofitService

        binding.btBackToMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_addNewPlaceCommonFrag_to_settingsProfileCommonFrag)
        }

    }
}