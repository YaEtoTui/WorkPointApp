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
import com.pp.coworkingapp.app.retrofit.adapter.AdapterPlacePromotion
import com.pp.coworkingapp.app.retrofit.api.MainApi
import com.pp.coworkingapp.app.retrofit.domain.Common
import com.pp.coworkingapp.app.retrofit.domain.response.Place
import com.pp.coworkingapp.app.retrofit.domain.viewModel.AuthViewModel
import com.pp.coworkingapp.app.retrofit.domain.viewModel.PlaceIdViewModel
import com.pp.coworkingapp.databinding.FragmentPromotionBusinessBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PromotionBusinessFragment : Fragment() {

    private lateinit var adapter : AdapterPlacePromotion
    private lateinit var binding: FragmentPromotionBusinessBinding
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var mainApi: MainApi
    private lateinit var tokenUser: String
    private val placeIdViewModel: PlaceIdViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPromotionBusinessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSettings()
        initMenu()
        mainApi = Common.retrofitService
        initAdapterList()
        initCurrentPerson()

        binding.btBackToMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_promotionFrag_to_mainPageFragment)
        }

        binding.tvlogOutBusiness.setOnClickListener {
            findNavController().navigate(R.id.action_promotionFrag_to_authFragment)
        }
    }

    private fun initAdapterList() {
        adapter = AdapterPlacePromotion()
        adapter.setOnButtonClickListener(object: AdapterPlacePromotion.OnButtonClickListener {
            override fun onClick(placeId: Int) {
                placeIdViewModel.placeId.value = placeId
//                findNavController().navigate(R.id.action_promotionFrag_to_promotion2Frag)
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
                val listPlaces: List<Place> = mainApi.getPlaceCoffee("Bearer $token", currentUser.id)
                requireActivity().runOnUiThread {
                    //Настраиваем кнопку настройки пользователя
                    binding.idAccount.setOnClickListener {
                        if (binding.idListAccountBusiness.isVisible)
                            binding.idListAccountBusiness.visibility = View.GONE
                        else
                            binding.idListAccountBusiness.visibility = View.VISIBLE
                    }
                    tokenUser = token
                    binding.apply {
                        Picasso.get().load(currentUser.photoUser).into(binding.imAvatar)
                        binding.tvNameAccount.text =
                            String.format("%s %s", currentUser.name, currentUser.surname)
//                        binding.idTextCity.text = currentUser.city
                    }

//                    adapter.submitList(listPlaces)
                }
            }
        }
    }

    private fun initMenu() {
        binding.apply {
            idTvFavorites.setOnClickListener {
                findNavController().navigate(R.id.action_promotionFrag_to_favouritesBusinessFrag)
            }
            idTvSettingsProfile.setOnClickListener {
                findNavController().navigate(R.id.action_promotionFrag_to_settingsProfileBusinessFrag)
            }
            idSettingsPlaces.setOnClickListener {
                findNavController().navigate(R.id.action_promotionFrag_to_settingsPlacesBusinessFrag)
            }
        }
    }

    private fun initSettings() {
        binding.apply {
            binding.tvAddPlaceBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_promotionFrag_to_addNewPlaceBusinessFrag)
            }
            tvFavoritesBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_promotionFrag_to_favouritesBusinessFrag)
            }
            tvSettingsProfileBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_promotionFrag_to_settingsProfileBusinessFrag)
            }
            tvSettingsPlacesBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_promotionFrag_to_settingsPlacesBusinessFrag)
            }
        }
    }
}