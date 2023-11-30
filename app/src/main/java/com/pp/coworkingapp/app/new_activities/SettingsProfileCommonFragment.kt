package com.pp.coworkingapp.app.new_activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.retrofit.api.MainApi
import com.pp.coworkingapp.app.retrofit.domain.viewModel.AuthViewModel
import com.pp.coworkingapp.app.retrofit.domain.viewModel.PlaceIdViewModel
import com.pp.coworkingapp.app.retrofit.domain.viewModel.UserViewModel
import com.pp.coworkingapp.databinding.FragmentSettingsProfileCommonBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SettingsProfileCommonFragment : Fragment() {

    private lateinit var binding: FragmentSettingsProfileCommonBinding
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var mainApi: MainApi
    private lateinit var tokenUser: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsProfileCommonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRetrofit()
        initCurrentPerson()
        initSettings()
        initMenu()

        binding.btBackToMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_settingsProfileCommonFrag_to_mainPageFragment)
        }
    }

    private fun initMenu() {
        binding.apply {
            idTvFavorites.setOnClickListener {
                findNavController().navigate(R.id.action_settingsProfileCommonFrag_to_favouritesCommonFrag)
            }
            idSettingsPlaces.setOnClickListener {
                findNavController().navigate(R.id.action_settingsProfileCommonFrag_to_settingsPlacesCommonFrag)
            }
        }
    }

    private fun initSettings() {
        binding.apply {
            tvFavorites.setOnClickListener {
                findNavController().navigate(R.id.action_settingsProfileCommonFrag_to_favouritesCommonFrag)
            }
            tvSettingsPlaces.setOnClickListener {
                findNavController().navigate(R.id.action_settingsProfileCommonFrag_to_settingsPlacesCommonFrag)
            }
        }
    }

    private fun initCurrentPerson() {
        //создание текущего user
        viewModel.token.observe(viewLifecycleOwner) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                Log.i("Token", token.toString())
                val currentUser = mainApi.checkUser("Bearer $token")
                requireActivity().runOnUiThread {
                    //Настраиваем кнопку настройки пользователя
                    binding.idAccount.setOnClickListener {
                        if (currentUser.roleId == 1) {
                            if (binding.idListAccountCommon.isVisible)
                                binding.idListAccountCommon.visibility = View.GONE
                            else
                                binding.idListAccountCommon.visibility = View.VISIBLE
                        } else {
                            if (binding.idListAccountBusiness.isVisible)
                                binding.idListAccountBusiness.visibility = View.GONE
                            else
                                binding.idListAccountBusiness.visibility = View.VISIBLE
                        }
                    }
                    tokenUser = token
                    binding.apply {
                        Picasso.get().load(currentUser.photoUser).into(binding.imAvatar)
                        binding.tvNameAccount.text =
                            String.format("%s %s", currentUser.name, currentUser.surname)
//                        binding.idTextCity.text = currentUser.city

                        Picasso
                            .get().
                            load(currentUser.photoUser).
                            into(imChangeAvatar)
                        tvFio.text = "${currentUser.name} ${currentUser.surname}"
                        tvPhone.text = currentUser.phone
                        edTextFirstName.setText(currentUser.name)
                        edTextSurName.setText(currentUser.surname)
                        edTextCity.setText(currentUser.city)
                        edTextPhone.setText(currentUser.phone)
                    }
                }
            }
        }
    }

    private fun initRetrofit() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.1506815-cq40245.tw1.ru").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
        mainApi = retrofit.create(MainApi::class.java)
    }
}