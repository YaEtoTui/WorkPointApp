package com.pp.coworkingapp.app.new_activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.retrofit.adapter.PlaceAdapter
import com.pp.coworkingapp.app.retrofit.api.MainApi
import com.pp.coworkingapp.app.retrofit.domain.viewModel.AuthViewModel
import com.pp.coworkingapp.app.retrofit.domain.viewModel.PlaceIdViewModel
import com.pp.coworkingapp.databinding.FragmentMainPageBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainPageFragment : Fragment() {

    private lateinit var adapter : PlaceAdapter
    private lateinit var binding: FragmentMainPageBinding
    private lateinit var mainApi: MainApi
    private val viewModel: AuthViewModel by activityViewModels()
    private val placeIdViewModel: PlaceIdViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainPageBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRetrofit()
        initRcView()

        //Загрузка текущего списка
        CoroutineScope(Dispatchers.IO).launch {
            val placesList = mainApi.getListPlaces()
            requireActivity().runOnUiThread {
                binding.apply {
                    tvCount.text = String.format("Найдено %s", placesList.count())
                    adapter.submitList(placesList)
                }
            }
        }

        //создание текущего user
        viewModel.token.observe(viewLifecycleOwner) {token ->
            CoroutineScope(Dispatchers.IO).launch {
                Log.i("Token", token.toString())
                val currentUser = mainApi.checkUser("Bearer $token")
                requireActivity().runOnUiThread {
                    binding.apply {
                        btSignInMain.visibility = View.INVISIBLE
                        imClock.visibility = View.VISIBLE
                        imAvatar.visibility = View.VISIBLE
                        tvNameAccount.visibility = View.VISIBLE
                        imList.visibility = View.VISIBLE
                        Picasso.get().load(currentUser.photoUser).into(binding.imAvatar)
                        binding.tvNameAccount.text = String.format("%s %s", currentUser.name, currentUser.surname)
                        binding.textGeo.text = currentUser.city
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

    private fun initRcView() {
        adapter = PlaceAdapter()
        adapter.setOnButtonClickListener(object: PlaceAdapter.OnButtonClickListener {
            override fun onClick(placeId: Int) {
                placeIdViewModel.placeId.value = placeId
                findNavController().navigate(R.id.action_mainPageFragment_to_placeCardFragment)
            }
        })
        binding.rcView.layoutManager = LinearLayoutManager(context)
        binding.rcView.adapter = adapter
    }
}