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
import com.pp.coworkingapp.app.retrofit.domain.viewModel.UserViewModel
import com.pp.coworkingapp.databinding.FragmentMainPageBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.streams.toList

class MainPageFragment : Fragment() {

    private lateinit var adapter : PlaceAdapter
    private lateinit var binding: FragmentMainPageBinding
    private lateinit var mainApi: MainApi
    private val viewModel: AuthViewModel by activityViewModels()
    private val placeIdViewModel: PlaceIdViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var listPlaces: List<Place>
    private lateinit var listCurrent: ArrayList<Place>
    private lateinit var listInt: ArrayList<Int>
    private lateinit var listFavoriteUserPlaces: List<IdResponse>
    private lateinit var tokenUser: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainPageBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listFavoriteUserPlaces = emptyList()
        mainApi = Common.retrofitService
        listCurrent = ArrayList()
        initRcView()
        initFilters()
        onClickCheckBox()
        searchText()

        binding.btSignInMain.setOnClickListener {
            findNavController().navigate(R.id.action_mainPageFragment_to_authFragment)
        }

        onClickCommonText()
        onClickBusinessText()

        if (viewModel.token.value != null) {
            //создание текущего user
            createUser()
        } else {
            loadListPlaces()

        }

        logout()
    }

    private fun initFilters() {
        binding.btFilters.setOnClickListener {
            if (binding.idFilters.visibility == View.GONE) {
                binding.idFilters.visibility = View.VISIBLE
            } else if (binding.idFilters.visibility == View.VISIBLE) {
                binding.idFilters.visibility = View.GONE
            }
        }
    }

    private fun logout() {
        binding.tvlogOut.setOnClickListener {
            findNavController().navigate(R.id.action_mainPageFragment_to_authFragment)
        }
    }

    private fun createUser() {
        viewModel.token.observe(viewLifecycleOwner) {token ->
            CoroutineScope(Dispatchers.IO).launch {
                Log.i("Token", token.toString())
                val currentUser = mainApi.checkUser("Bearer $token")
                listFavoriteUserPlaces = mainApi.getFavoritePlaces("Bearer $token")
                listInt = listFavoriteUserPlaces.stream().map(this::createInt).toList() as ArrayList<Int>
                listPlaces = mainApi.getListPlaces()
                adapter.setList(listInt.toList())
                requireActivity().runOnUiThread {
                    tokenUser = token
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
                    binding.apply {
                        btSignInMain.visibility = View.GONE
                        idAccount.visibility = View.VISIBLE
                        imClock.visibility = View.VISIBLE
                        Picasso.get()
                            .load(currentUser.photoUser)
                            .into(binding.imAvatar)
                        if (currentUser.name.length + currentUser.surname.length >= 16) {
                            binding.tvNameAccount.text = String.format("%s %s.", currentUser.name, currentUser.surname.substring(0,1))
                        } else {
                            binding.tvNameAccount.text = String.format("%s %s", currentUser.name, currentUser.surname)
                        }
//                        binding.textGeo.text = currentUser.city

                        userViewModel.user.value = currentUser

                        tvCount.text = String.format("Найдено: %s", listPlaces.count())
                        adapter.submitList(listPlaces)

                        if (listPlaces.isNotEmpty()) {
                            idProgressBar.visibility = View.GONE
                            idMainPage.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun onClickCommonText() {
        binding.tvAddPlace.setOnClickListener {
            findNavController().navigate(R.id.action_mainPageFragment_to_addNewPlaceCommonFrag)
        }
        binding.tvFavorites.setOnClickListener {
            findNavController().navigate(R.id.action_mainPageFragment_to_favouritesCommonFrag)
        }

        binding.tvSettingsProfile.setOnClickListener {
            findNavController().navigate(R.id.action_mainPageFragment_to_settingsProfileCommonFrag)
        }

        binding.tvSettingsPlaces.setOnClickListener {
            findNavController().navigate(R.id.action_mainPageFragment_to_settingsPlacesCommonFrag)
        }
    }

    private fun onClickBusinessText() {
        binding.tvAddPlaceBusiness.setOnClickListener {
            findNavController().navigate(R.id.action_mainPageFragment_to_addNewPlaceBusinessFrag)
        }
        binding.tvFavoritesBusiness.setOnClickListener {
            findNavController().navigate(R.id.action_mainPageFragment_to_favouritesBusinessFrag)
        }

        binding.tvSettingsProfileBusiness.setOnClickListener {
            findNavController().navigate(R.id.action_mainPageFragment_to_settingsProfileBusinessFrag)
        }

        binding.tvSettingsPlacesBusiness.setOnClickListener {
            findNavController().navigate(R.id.action_mainPageFragment_to_settingsPlacesBusinessFrag)
        }
    }

    private fun loadListPlaces() {
        //Загрузка текущего списка
        CoroutineScope(Dispatchers.IO).launch {
            listPlaces = mainApi.getListPlaces()
            requireActivity().runOnUiThread {
                binding.apply {
                    tvCount.text = String.format("Найдено: %s", listPlaces.count())
                    adapter.submitList(listPlaces)

                    if (listPlaces.isNotEmpty()) {
                        idProgressBar.visibility = View.GONE
                        idMainPage.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun searchText() {
        binding.btSearchCow.setOnClickListener {
            val queryText: String = binding.edSearch.text.toString()
            listCurrent = listPlaces.filter { place -> place.name.lowercase().contains(queryText.lowercase())} as ArrayList<Place>
            binding.tvCount.text = String.format("Найдено: %s", listCurrent.count())
            adapter.submitList(listCurrent.toList())
            binding.edSearch.text = null
        }
    }

    private fun onClickCheckBox() {
        //вопрос не решен
        binding.apply {
            btArea1.setOnClickListener {
                if (btArea1.isChecked) {
                    listCurrent.addAll(listPlaces.filter { place ->
                        place.district.lowercase().contains(btArea1.text.toString().lowercase())
                    })
                } else {
                    listCurrent.addAll(listPlaces.filter { place ->
                        !place.district.lowercase().contains(btArea1.text.toString().lowercase())
                    })
                }

                binding.tvCount.text = String.format("Найдено: %s", listCurrent.count())
                adapter.submitList(listCurrent.toList())
            }
        }
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
                        }
                        listInt.add(placeId)
                        adapter.setList(listInt.toList())
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            val responseIds: IdResponse = mainApi.addFavoritePlace("Bearer $tokenUser", CreatePlaceAndUserRequest(
                                0,
                                placeId
                            ))
                        }
                        listInt.remove(placeId)
                        adapter.setList(listInt.toList())
                    }
                }
            }
        })
        binding.rcView.layoutManager = LinearLayoutManager(context)
        binding.rcView.adapter = adapter
    }

    private fun createInt(user: IdResponse): Int {
        return user.placeId
    }
}

private fun CoroutineScope.createInt(idResponse: IdResponse): Int {
    return idResponse.placeId
}
