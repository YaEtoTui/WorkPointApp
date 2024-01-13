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
import com.pp.coworkingapp.app.retrofit.adapter.AdvertisementAdapter
import com.pp.coworkingapp.app.retrofit.api.MainApi
import com.pp.coworkingapp.app.retrofit.domain.Common
import com.pp.coworkingapp.app.retrofit.domain.response.Advertisement
import com.pp.coworkingapp.app.retrofit.domain.response.Place
import com.pp.coworkingapp.app.retrofit.domain.viewModel.AuthViewModel
import com.pp.coworkingapp.databinding.FragmentPromotionBusiness3Binding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PromotionBusiness3Fragment : Fragment() {

    private lateinit var adapter : AdvertisementAdapter
    private lateinit var binding: FragmentPromotionBusiness3Binding
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var mainApi: MainApi
    private lateinit var tokenUser: String
//    private val placeIdViewModel: PlaceIdViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPromotionBusiness3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSettings()
        initMenu()
        mainApi = Common.retrofitService
        initAdapterList()
        initCurrentPerson()
        initListData()
        initListStatus()

        binding.btBackToMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_promotion3Frag_to_mainPageFragment)
        }

        binding.tvlogOutBusiness.setOnClickListener {
            findNavController().navigate(R.id.action_promotion3Frag_to_authFragment)
        }
    }

    private fun initListStatus() {
        binding.edTextStatus.setOnClickListener {
            if (binding.idListStatusConst.isVisible)
                binding.idListStatusConst.visibility = View.GONE
            else
                binding.idListStatusConst.visibility = View.VISIBLE
        }

        binding.btStatusAll.setOnClickListener {
            binding.edTextStatus.setText(binding.btStatusAll.text.toString())
            binding.idListStatusConst.visibility = View.GONE
        }
        binding.btStatusProcess.setOnClickListener {
            binding.edTextStatus.setText(binding.btStatusProcess.text.toString())
            binding.idListStatusConst.visibility = View.GONE
        }
        binding.btStatusNow.setOnClickListener {
            binding.edTextStatus.setText(binding.btStatusNow.text.toString())
            binding.idListStatusConst.visibility = View.GONE
        }
        binding.btStatusWill.setOnClickListener {
            binding.edTextStatus.setText(binding.btStatusWill.text.toString())
            binding.idListStatusConst.visibility = View.GONE
        }
        binding.btStatusEnd.setOnClickListener {
            binding.edTextStatus.setText(binding.btStatusEnd.text.toString())
            binding.idListStatusConst.visibility = View.GONE
        }
    }

    private fun initListData() {
        binding.edTextData.setOnClickListener {
            if (binding.idListDataConst.isVisible)
                binding.idListDataConst.visibility = View.GONE
            else
                binding.idListDataConst.visibility = View.VISIBLE
        }

        binding.btDateDefault.setOnClickListener {
            binding.edTextData.setText(binding.btDateDefault.text.toString())
            binding.idListDataConst.visibility = View.GONE
        }
        binding.btDateNew.setOnClickListener {
            binding.edTextData.setText(binding.btDateNew.text.toString())
            binding.idListDataConst.visibility = View.GONE
        }
        binding.btDateOld.setOnClickListener {
            binding.edTextData.setText(binding.btDateOld.text.toString())
            binding.idListDataConst.visibility = View.GONE
        }
    }

    private fun initAdapterList() {
        adapter = AdvertisementAdapter()
        binding.rcView.layoutManager = LinearLayoutManager(context)
        binding.rcView.adapter = adapter
    }

    private fun initCurrentPerson() {
        //создание текущего user
        viewModel.token.observe(viewLifecycleOwner) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                Log.i("Token", token.toString())
                val currentUser = mainApi.checkUser("Bearer $token")
                val listAds: List<Advertisement> = mainApi.getAdvertisementsByUserId("Bearer $token")
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

                    adapter.submitList(listAds)
                }
            }
        }
    }

    private fun initMenu() {
        binding.apply {
            idTvFavorites.setOnClickListener {
                findNavController().navigate(R.id.action_promotion3Frag_to_favouritesBusinessFrag)
            }
            idTvSettingsProfile.setOnClickListener {
                findNavController().navigate(R.id.action_promotion3Frag_to_settingsProfileBusinessFrag)
            }
            idSettingsPlaces.setOnClickListener {
                findNavController().navigate(R.id.action_promotion3Frag_to_settingsPlacesBusinessFrag)
            }
        }
    }

    private fun initSettings() {
        binding.apply {
            binding.tvAddPlaceBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_promotion3Frag_to_addNewPlaceBusinessFrag)
            }
            tvFavoritesBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_promotion3Frag_to_favouritesBusinessFrag)
            }
            tvSettingsProfileBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_promotion3Frag_to_settingsProfileBusinessFrag)
            }
            tvSettingsPlacesBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_promotion3Frag_to_settingsPlacesBusinessFrag)
            }
            tvPromotionBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_promotion3Frag_to_promotionFrag)
            }
        }
    }
}