package com.pp.coworkingapp.app.new_activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.retrofit.adapter.TagAdapter
import com.pp.coworkingapp.app.retrofit.api.MainApi
import com.pp.coworkingapp.app.retrofit.domain.viewModel.AuthViewModel
import com.pp.coworkingapp.app.retrofit.domain.viewModel.PlaceIdViewModel
import com.pp.coworkingapp.databinding.FragmentPlaceCardBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlaceCardFragment : Fragment() {

    private lateinit var adapterTags: TagAdapter
    private lateinit var binding: FragmentPlaceCardBinding
    private lateinit var mainApi: MainApi
    private val placeIdViewModel: PlaceIdViewModel by activityViewModels()
    private val viewModel: AuthViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaceCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRetrofit()
        initCurrentPerson()
        initPlaceCard()

        binding.btBackToMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_placeCardFragment_to_mainPageFragment)
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

    private fun initPlaceCard() {
        placeIdViewModel.placeId.observe(viewLifecycleOwner) {placeId ->
            CoroutineScope(Dispatchers.IO).launch {
                val currentPlace = mainApi.findPlaceCard(placeId)
                requireActivity().runOnUiThread {
                    binding.apply {
                        tvNamePoint.text = currentPlace.name
                        tvRating.text = currentPlace.rating
                        if (!currentPlace.photo.isEmpty()) {
                            Picasso.get()
                                .load(currentPlace.photo)
                                .into(binding.imPhotoCorousel);
                        }
                        tvDescPoint.text = currentPlace.description
                        tvGeo.text = currentPlace.address
                        tvTime.text = currentPlace.openingHours
                        if (!currentPlace.tags.isEmpty()) {
                            initTagsAdapter()
                            //тэги
                            adapterTags.submitList(currentPlace.tags)
                        }

                        tvPhone.text = currentPlace.companyPhone
                        val number: String = currentPlace.rating
                        btRatingBar.numStars
                        btRatingBar.rating = currentPlace.rating.toFloat()
                    }
                }
            }
        }
    }

    private fun initCurrentPerson() {
        //создание текущего user
        viewModel.token.observe(viewLifecycleOwner) {token ->
            CoroutineScope(Dispatchers.IO).launch {
                Log.i("Token", token.toString())
                val currentUser = mainApi.checkUser("Bearer $token")
                requireActivity().runOnUiThread {
                    binding.apply {
                        Picasso.get().load(currentUser.photoUser).into(binding.imAvatar)
                        binding.tvNameAccount.text = String.format("%s %s", currentUser.name, currentUser.surname)
                        binding.idTextCity.text = currentUser.city
                    }
                }
            }
        }
    }

    private fun initTagsAdapter() {
        adapterTags = TagAdapter()
        binding.idRcTags.adapter = adapterTags
    }
}