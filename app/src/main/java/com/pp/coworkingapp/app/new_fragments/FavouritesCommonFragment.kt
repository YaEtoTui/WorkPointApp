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
import androidx.recyclerview.widget.LinearLayoutManager
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.retrofit.adapter.PlaceAdapter
import com.pp.coworkingapp.app.retrofit.api.MainApi
import com.pp.coworkingapp.app.retrofit.domain.Common
import com.pp.coworkingapp.app.retrofit.domain.request.CreatePlaceAndUserRequest
import com.pp.coworkingapp.app.retrofit.domain.response.IdResponse
import com.pp.coworkingapp.app.retrofit.domain.response.Place
import com.pp.coworkingapp.app.retrofit.domain.viewModel.AuthViewModel
import com.pp.coworkingapp.app.retrofit.domain.viewModel.PlaceIdViewModel
import com.pp.coworkingapp.databinding.FragmentFavouritesCommonBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.streams.toList

class FavouritesCommonFragment : Fragment() {

    private lateinit var binding: FragmentFavouritesCommonBinding
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var mainApi: MainApi
    private lateinit var tokenUser: String
    private lateinit var adapter : PlaceAdapter
    private lateinit var listFavoriteUserPlaces: List<IdResponse>
    private lateinit var listPlaces: List<Place>
    private lateinit var listInt: ArrayList<Int>
    private val placeIdViewModel: PlaceIdViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritesCommonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listFavoriteUserPlaces = emptyList()
        initRcView()
        initMenu()
        initSettings()
        mainApi = Common.retrofitService
        initCurrentPerson()

        binding.btBackToMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_favouritesCommonFrag_to_mainPageFragment)
        }

        binding.tvlogOut.setOnClickListener {
            findNavController().navigate(R.id.action_favouritesCommonFrag_to_authFragment)
        }
    }

    private fun createInt(user: IdResponse): Int {
        return user.placeId
    }

    private fun initRcView() {
        adapter = PlaceAdapter()
        adapter.setList(listFavoriteUserPlaces.stream().map(this::createInt).toList())
        adapter.setOnButtonClickListener(object: PlaceAdapter.OnButtonClickListener {
            override fun onClick(placeId: Int) {
                if (viewModel.token.value != null) {
                    placeIdViewModel.placeId.value = placeId
                    findNavController().navigate(R.id.action_mainPageFragment_to_placeCardFragment)
                } else {
                    findNavController().navigate(R.id.action_mainPageFragment_to_authFragment)
                }
            }
        })
        adapter.setOnButtonHeartClickListener(object: PlaceAdapter.OnButtonClickListener {
            override fun onClick(placeId: Int) {
                if (viewModel.token.value != null) {
                    if (!listInt.contains(placeId)) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val responseIds: IdResponse = mainApi.addFavoritePlace("Bearer $tokenUser", CreatePlaceAndUserRequest(
                                0,
                                placeId
                            ))
                            requireActivity().runOnUiThread {
                                Log.i("Http", "OK 200")
                                listInt.add(placeId)
                                adapter.setList(listInt.toList())
                            }
                        }
                    }
                }
            }
        })
        binding.rcView.layoutManager = LinearLayoutManager(context)
        binding.rcView.adapter = adapter
    }

    private fun initCurrentPerson() {
        //создание текущего user
        viewModel.token.observe(viewLifecycleOwner) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                Log.i("Token", token.toString())
                val currentUser = mainApi.checkUser("Bearer $token")
                listFavoriteUserPlaces = mainApi.getFavoritePlaces("Bearer $token")
                listInt = listFavoriteUserPlaces.stream().map(this::createInt).toList() as ArrayList<Int>
                listPlaces = mainApi.getListPlaces()
                adapter.setList(listInt.toList())
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

                        val list: List<Place> = listPlaces.filter { place -> listInt.contains(place.id) }
                        if (listFavoriteUserPlaces.isNotEmpty()) {
                            idEmpty.visibility = View.GONE
                            tvDesc.visibility = View.VISIBLE
                            adapter.submitList(list)

                        } else {
                            idEmpty.visibility = View.VISIBLE
                            tvDesc.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun initMenu() {
        binding.apply {
            idTvSettingsProfile.setOnClickListener {
                findNavController().navigate(R.id.action_favouritesCommonFrag_to_settingsProfileCommonFrag)
            }
            idSettingsPlaces.setOnClickListener {
                findNavController().navigate(R.id.action_favouritesCommonFrag_to_settingsPlacesCommonFrag)
            }
        }
    }

    private fun initSettings() {
        binding.apply {
            tvAddPlace.setOnClickListener {
                findNavController().navigate(R.id.action_favouritesCommonFrag_to_addNewPlaceCommonFrag)
            }
            tvSettingsProfile.setOnClickListener {
                findNavController().navigate(R.id.action_favouritesCommonFrag_to_settingsProfileCommonFrag)
            }
            tvSettingsPlaces.setOnClickListener {
                findNavController().navigate(R.id.action_favouritesCommonFrag_to_settingsPlacesCommonFrag)
            }
        }
    }
}

private fun CoroutineScope.createInt(idResponse: IdResponse): Int {
    return idResponse.placeId
}