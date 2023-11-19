package com.pp.coworkingapp.app.new_activities

import android.graphics.Color
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
import com.pp.coworkingapp.app.retrofit.adapter.ReviewAdapter
import com.pp.coworkingapp.app.retrofit.adapter.TagAdapter
import com.pp.coworkingapp.app.retrofit.api.MainApi
import com.pp.coworkingapp.app.retrofit.domain.request.CreateReviewRequest
import com.pp.coworkingapp.app.retrofit.domain.viewModel.AuthViewModel
import com.pp.coworkingapp.app.retrofit.domain.viewModel.PlaceIdViewModel
import com.pp.coworkingapp.app.retrofit.domain.viewModel.UserViewModel
import com.pp.coworkingapp.databinding.FragmentPlaceCardBinding
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates


class PlaceCardFragment : Fragment() {

    private lateinit var adapterTags: TagAdapter
    private lateinit var adapterReview: ReviewAdapter
    private lateinit var binding: FragmentPlaceCardBinding
    private lateinit var mainApi: MainApi
    private val placeIdViewModel: PlaceIdViewModel by activityViewModels()
    private val viewModel: AuthViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private lateinit var tokenUser: String
    private var idPlace by Delegates.notNull<Int>()

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
        initReview()

        binding.btBackToMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_placeCardFragment_to_mainPageFragment)
        }

        binding.btCreateReview.setOnClickListener {
            if (binding.idTextReview.text?.isNotEmpty() == true) {
                addReview()
                initPlaceCard()
                initReview()
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

    private fun initPlaceCard() {
        placeIdViewModel.placeId.observe(viewLifecycleOwner) { placeId ->
            CoroutineScope(Dispatchers.IO).launch {
                val currentPlace = mainApi.findPlaceCard(placeId)
                requireActivity().runOnUiThread {
                    idPlace = placeId
                    binding.apply {
                        tvNamePoint.text = currentPlace.name
                        tvRating.text = currentPlace.rating
                        if (currentPlace.photo.isNotEmpty() || currentPlace.photo.startsWith("http")) {

                            Picasso.get()
                                .load(currentPlace.photo)
                                .centerCrop().fit()
                                .error(R.drawable.ic_launcher_foreground)
                                .into(binding.imPhotoCorousel)

                        }
                        tvDescPoint.text = currentPlace.description
                        tvGeo.text = currentPlace.address
                        tvTime.text = currentPlace.openingHours
                        initTagsAdapter()
                        //тэги
                        adapterTags.submitList(currentPlace.tags)
                        tvPhone.text = currentPlace.companyPhone
                        if (currentPlace.rating.contains("0") || currentPlace.rating.contains("1") || currentPlace.rating.contains(
                                "2"
                            ) || currentPlace.rating.contains("3")
                            || currentPlace.rating.contains("4") || currentPlace.rating.contains("5")
                        ) {
                            btRatingBar.rating = currentPlace.rating.toFloat()
                        } else {
                            btRatingBar.rating = 0f
                        }
                    }
                }
            }
        }
    }

    private fun initReview() {
        placeIdViewModel.placeId.observe(viewLifecycleOwner) { placeId ->
            CoroutineScope(Dispatchers.IO).launch {
                val reviewsList = mainApi.findReviews(placeId)
                requireActivity().runOnUiThread {
                    initReviewAdapter()
                    binding.apply {
                        adapterReview.submitList(reviewsList)
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
                    tokenUser = token
                    binding.apply {
                        Picasso.get().load(currentUser.photoUser).into(binding.imAvatar)
                        binding.tvNameAccount.text =
                            String.format("%s %s", currentUser.name, currentUser.surname)
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

    private fun initReviewAdapter() {
        adapterReview = ReviewAdapter()
        binding.idRcViewReviews.layoutManager = LinearLayoutManager(context)
        binding.idRcViewReviews.adapter = adapterReview
    }

    private fun addReview() {
        userViewModel.user.observe(viewLifecycleOwner) {user ->
            CoroutineScope(Dispatchers.IO).launch {
                 val review = mainApi.addReview(
                     "Bearer $tokenUser",
                    CreateReviewRequest(
                        user.id,
                        idPlace,
                        binding.idTextReview.text.toString(),
                        binding.btRatingBar.rating.toInt()
                    )
                )
                requireActivity().runOnUiThread {
                    binding.apply {
                        idTextReview.text = null
                        btRatingBar.rating = review.rank.toFloat()
                    }
                }
            }
        }
    }
}