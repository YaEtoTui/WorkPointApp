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
import androidx.recyclerview.widget.LinearLayoutManager
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.enum.Cafe
import com.pp.coworkingapp.app.enum.Cost
import com.pp.coworkingapp.app.enum.Hours
import com.pp.coworkingapp.app.retrofit.adapter.FilterAdapter
import com.pp.coworkingapp.app.retrofit.adapter.PlaceAdapter
import com.pp.coworkingapp.app.retrofit.api.MainApi
import com.pp.coworkingapp.app.retrofit.domain.Common
import com.pp.coworkingapp.app.retrofit.domain.viewModel.AuthViewModel
import com.pp.coworkingapp.databinding.FragmentAddNewPlaceCommonBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AddNewPlaceCommonFragment : Fragment() {

    private lateinit var binding: FragmentAddNewPlaceCommonBinding
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var mainApi: MainApi
    private lateinit var tokenUser: String
    private lateinit var adapter : FilterAdapter

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
        initCurrentPerson()

        initListHours()
        initListTypeCoffee()
        initListCost()

        binding.btBackToMainPage.setOnClickListener {
            findNavController().navigate(com.pp.coworkingapp.R.id.action_addNewPlaceCommonFrag_to_settingsProfileCommonFrag)
        }

    }

    private fun initListCost() {
        adapter = FilterAdapter()
        adapter.setOnButtonClickListener(object: FilterAdapter.OnButtonClickListener {
            override fun onClick(strChoice: String) {
                binding.edTextFilterCost.setText(strChoice)
                binding.idListCost.visibility = View.GONE
            }
        })
        binding.idListCost.layoutManager = LinearLayoutManager(context)
        binding.idListCost.adapter = adapter

        val listString: List<String> = listOf(Cost.FORFREE.cost, Cost.PAID.cost)

        adapter.submitList(listString)
        binding.edTextFilterCost.setOnClickListener {
            if (binding.idListCost.isVisible)
                binding.idListCost.visibility = View.GONE
            else
                binding.idListCost.visibility = View.VISIBLE
        }
    }

    private fun initListTypeCoffee() {
        adapter = FilterAdapter()
        adapter.setOnButtonClickListener(object: FilterAdapter.OnButtonClickListener {
            override fun onClick(strChoice: String) {
                binding.edTextFilterCoffeeType.setText(strChoice)
                binding.idListCoffeeType.visibility = View.GONE
            }
        })
        binding.idListCoffeeType.layoutManager = LinearLayoutManager(context)
        binding.idListCoffeeType.adapter = adapter

        val listString: List<String> = listOf(Cafe.CAFE.cafe, Cafe.ANTICAFE.cafe, Cafe.WORKROOM.cafe)

        adapter.submitList(listString)
        binding.edTextFilterCoffeeType.setOnClickListener {
            if (binding.idListCoffeeType.isVisible)
                binding.idListCoffeeType.visibility = View.GONE
            else
                binding.idListCoffeeType.visibility = View.VISIBLE
        }
    }

    private fun initListHours() {
        adapter = FilterAdapter()
        adapter.setOnButtonClickListener(object: FilterAdapter.OnButtonClickListener {
            override fun onClick(strChoice: String) {
                binding.edTextFilterTime.setText(strChoice)
                binding.idListHours.visibility = View.GONE
            }
        })
        binding.idListHours.layoutManager = LinearLayoutManager(context)
        binding.idListHours.adapter = adapter

        val listString: List<String> = listOf(Hours.AROUNDCLOCK.hours, Hours.ONWEEKDAYS.hours, Hours.EVERYDAY.hours)

        adapter.submitList(listString)
        binding.edTextFilterTime.setOnClickListener {
            if (binding.idListHours.isVisible)
                binding.idListHours.visibility = View.GONE
            else
                binding.idListHours.visibility = View.VISIBLE
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
}