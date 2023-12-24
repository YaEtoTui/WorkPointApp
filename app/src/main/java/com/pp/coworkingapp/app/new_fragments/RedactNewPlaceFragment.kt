package com.pp.coworkingapp.app.new_fragments

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
import com.pp.coworkingapp.app.retrofit.domain.Common
import com.pp.coworkingapp.app.retrofit.domain.response.Place
import com.pp.coworkingapp.app.retrofit.domain.response.PlaceWithTags
import com.pp.coworkingapp.app.retrofit.domain.viewModel.AuthViewModel
import com.pp.coworkingapp.app.retrofit.domain.viewModel.PlaceIdViewModel
import com.pp.coworkingapp.app.retrofit.domain.viewModel.UserViewModel
import com.pp.coworkingapp.databinding.FragmentRedactNewPlaceBinding
import com.squareup.picasso.Picasso
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RedactNewPlaceFragment : Fragment() {

    private lateinit var binding: FragmentRedactNewPlaceBinding
    private lateinit var mainApi: MainApi
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var tokenUser: String
    private val placeIdViewModel: PlaceIdViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRedactNewPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainApi = Common.retrofitService

        initCurrentPerson()
        initSettings()

        initPlaceCard()

        binding.btBackToMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_redactNewPlaceFrag_to_mainPageFragment)
        }
    }

    private fun initPlaceCard() {
        placeIdViewModel.placeId.observe(viewLifecycleOwner) { placeId ->
            CoroutineScope(Dispatchers.IO).launch {
                val currentPlace: PlaceWithTags = mainApi.findPlaceCard(placeId)
                requireActivity().runOnUiThread {
                    binding.apply {
                        edTextNameInstitution.setText(currentPlace.name)
                        edTextCity.setText(currentPlace.city)
                        edTextArea.setText(currentPlace.district)
                        edTextAddress.setText(currentPlace.address)

                        edTextDesc.setText(currentPlace.description)

                        edTextFilterTime.setText(currentPlace.openingHours)
                        edTextFilterCoffeeType.setText(currentPlace.typeCafe)
                        edTextFilterCost.setText(currentPlace.cost)

                        //tags не сделано

                        btParking.isChecked = currentPlace.parking
                        btRestZone.isChecked = currentPlace.recreationArea
                        btConferenceHall.isChecked = currentPlace.conferenceHall

                        edTextPhone.setText(currentPlace.companyPhone)
                        edTextMail.setText(currentPlace.email)
                        edTextSite.setText(currentPlace.site)

                        //photo не сделано
                    }
                }
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
                        }
                    }
                    tokenUser = token
                    binding.apply {
                        Picasso.get().load(currentUser.photoUser).into(binding.imAvatar)
                        binding.tvNameAccount.text =
                            String.format("%s %s", currentUser.name, currentUser.surname)
//                        binding.idTextCity.text = currentUser.city

                    }
                }
            }
        }
    }

    private fun initSettings() {
        binding.apply {
            tvAddPlace.setOnClickListener {
                findNavController().navigate(R.id.action_redactNewPlaceFrag_to_addNewPlaceCommonFrag)
            }
            tvFavorites.setOnClickListener {
                findNavController().navigate(R.id.action_redactNewPlaceFrag_to_favouritesCommonFrag)
            }
            tvSettingsProfile.setOnClickListener {
                findNavController().navigate(R.id.action_redactNewPlaceFrag_to_settingsProfileCommonFrag)
            }
            tvSettingsPlaces.setOnClickListener {
                findNavController().navigate(R.id.action_redactNewPlaceFrag_to_settingsPlacesCommonFrag)
            }
        }
    }
}