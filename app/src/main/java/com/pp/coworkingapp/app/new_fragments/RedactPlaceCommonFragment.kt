package com.pp.coworkingapp.app.new_fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.enum.Cafe
import com.pp.coworkingapp.app.enum.Cost
import com.pp.coworkingapp.app.enum.Hours
import com.pp.coworkingapp.app.enum.Status
import com.pp.coworkingapp.app.retrofit.adapter.FilterAdapter
import com.pp.coworkingapp.app.retrofit.adapter.TagsAddNewPlaceCardAdapter
import com.pp.coworkingapp.app.retrofit.adapter.TagsRedactPlaceCardAdapter
import com.pp.coworkingapp.app.retrofit.api.MainApi
import com.pp.coworkingapp.app.retrofit.domain.Common
import com.pp.coworkingapp.app.retrofit.domain.request.Payload
import com.pp.coworkingapp.app.retrofit.domain.request.PayloadSansTags
import com.pp.coworkingapp.app.retrofit.domain.response.PlaceWithTags
import com.pp.coworkingapp.app.retrofit.domain.response.Tag
import com.pp.coworkingapp.app.retrofit.domain.viewModel.AuthViewModel
import com.pp.coworkingapp.app.retrofit.domain.viewModel.PlaceIdViewModel
import com.pp.coworkingapp.app.retrofit.domain.viewModel.UserViewModel
import com.pp.coworkingapp.databinding.FragmentAddNewPlaceCommonBinding
import com.pp.coworkingapp.databinding.FragmentRedactPlaceCommonBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File


class RedactPlaceCommonFragment : Fragment() {

    private lateinit var binding: FragmentRedactPlaceCommonBinding
    private lateinit var mainApi: MainApi
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var tokenUser: String
    private val placeIdViewModel: PlaceIdViewModel by activityViewModels()
    private lateinit var adapter : FilterAdapter
    private lateinit var adapterTags : TagsAddNewPlaceCardAdapter
    private lateinit var adapterTagAdd : TagsRedactPlaceCardAdapter
    private lateinit var listTagsPlaceCard: ArrayList<Tag>
    private lateinit var listPhotoPlaceCard: Array<Uri?>
    private var photoUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRedactPlaceCommonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainApi = Common.retrofitService

        listTagsPlaceCard = ArrayList()
        listPhotoPlaceCard = arrayOfNulls<Uri?>(size = 3)

        initCurrentPerson()
        initSettings()

        initPlaceCard()
        initListCost()
        initListTypeCoffee()
        initListHours()
        initListTags()

        updatePlace()
        initListPhoto()

        initMenu()

        binding.btDeleteNewCard.setOnClickListener {
            findNavController().navigate(R.id.action_redactPlaceCommonFrag_to_settingsPlacesCommonFrag)
        }

        binding.btBackToMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_redactPlaceCommonFrag_to_mainPageFragment)
        }

        binding.tvlogOut.setOnClickListener {
            findNavController().navigate(R.id.action_redactPlaceCommonFrag_to_authFragment)
        }

        deletePlace()
    }

    private fun deletePlace() {
        binding.btDeleteNewCard.setOnClickListener {
            viewModel.token.observe(viewLifecycleOwner) { token ->
                placeIdViewModel.placeId.observe(viewLifecycleOwner) { placeId ->
                    CoroutineScope(Dispatchers.IO).launch {
                        mainApi.deletePlaces("Bearer $token", placeId)
                    }
                    requireActivity().runOnUiThread {
                        findNavController().navigate(R.id.action_redactPlaceCommonFrag_to_settingsPlacesCommonFrag)
                    }
                }
            }
        }
    }

    private fun getRealPathFromUri(context: Context, contentUri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(contentUri)
        val file = File(context.cacheDir, "temp_file")
        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        return file
    }

    private fun initMenu() {
        binding.apply {
            idTvFavorites.setOnClickListener {
                findNavController().navigate(R.id.action_redactPlaceCommonFrag_to_favouritesCommonFrag)
            }
            idTvSettingsProfile.setOnClickListener {
                findNavController().navigate(R.id.action_redactPlaceCommonFrag_to_settingsProfileCommonFrag)
            }
            idSettingsPlaces.setOnClickListener {
                findNavController().navigate(R.id.action_redactPlaceCommonFrag_to_settingsPlacesCommonFrag)
            }
        }
    }

    private fun initListPhoto() {
        binding.apply {
            idPhoto1.setOnClickListener {
                getPhoto(PICK_IMAGE1)
            }
            idPhoto2.setOnClickListener {
                getPhoto(PICK_IMAGE2)
            }
            idPhoto3.setOnClickListener {
                getPhoto(PICK_IMAGE3)
            }
        }
    }

    private val PICK_IMAGE1 = 1
    private val PICK_IMAGE2 = 2
    private val PICK_IMAGE3 = 3

    private fun getPhoto(pickImage: Int) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, pickImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE1 && resultCode == Activity.RESULT_OK) {
            Log.i("Photo", "Тут 1")
            val imageUri = data?.data
            photoUri = data?.data
            listPhotoPlaceCard[0] = imageUri
            loadPhoto(imageUri, binding.idPhoto1)
        } else if (requestCode == PICK_IMAGE2 && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            listPhotoPlaceCard[1] = imageUri
            loadPhoto(imageUri, binding.idPhoto2)
            Log.i("Photo", "Тут 2")
        } else if (requestCode == PICK_IMAGE3 && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            listPhotoPlaceCard[2] = imageUri
            loadPhoto(imageUri, binding.idPhoto3)
            Log.i("Photo", "Тут 3")
        }
    }

    private fun loadPhoto(imageUri: Uri?, view: ImageView) {
        Glide.with(this)
            .load(imageUri)
            .transform(RoundedCorners(20))
            .centerCrop()
            .error(R.drawable.ic_launcher_foreground)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(view)
    }

    private fun updatePlace() {

        val editTextNamePlace: String = binding.edTextNameInstitution.text.toString()
        val editTextCity: String = binding.edTextCity.text.toString()
        val editTextArea: String = binding.edTextArea.text.toString()
        val editTextAddress: String = binding.edTextAddress.text.toString()

        val editTextDesc: String = binding.edTextDesc.text.toString()

        val editTextFilterTime: String = binding.edTextFilterTime.text.toString()
        val editTextCoffeeType: String = binding.edTextFilterCoffeeType.text.toString()
        val editTextCost: String = binding.edTextFilterCost.text.toString()

        val countListTags: Int = binding.idListTagsPlaceCard.size

        val listTagsId: ArrayList<String> = ArrayList()
        for(i in 0..<listTagsPlaceCard.size) {
            when (listTagsPlaceCard[i].name) {
                "Wi-Fi" -> {
                    listTagsId.add("1")
                }
                "Розетки" -> {
                    listTagsId.add("2")
                }
                "Еда" -> {
                    listTagsId.add("3")
                }
                "Напитки" -> {
                    listTagsId.add("4")
                }
                "Канцелярия" -> {
                    listTagsId.add("5")
                }
            }
        }

        val editTextPhone: String = binding.edTextPhone.text.toString()
        val editTextMail: String = binding.edTextMail.text.toString()
        val editTextSite: String = binding.edTextSite.text.toString()

        Log.i("Text", editTextFilterTime)
        Log.i("Text", editTextCoffeeType)
        Log.i("Text", editTextCost)

        binding.btSaveNewCard.setOnClickListener {
            viewModel.token.observe(viewLifecycleOwner) { token ->
                placeIdViewModel.placeId.observe(viewLifecycleOwner) { placeId ->

                    val listFiles: ArrayList<File> = ArrayList()

                    for(i in 0..listPhotoPlaceCard.size - 1) {
                        if (listPhotoPlaceCard[i] != null) {
                            listFiles.add(getRealPathFromUri(requireContext(), listPhotoPlaceCard[i]!!))
                        }
                    }

                    val imageParts: List<MultipartBody.Part> = listFiles.toList().mapIndexed { index, file ->
                        MultipartBody.Part.createFormData(
                            "image_${index + 1}",
                            "image_${index + 1}",
                            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                        )
                    }

                    val payload: PayloadSansTags = PayloadSansTags(
                        0,
                        editTextNamePlace,
                        editTextCity,
                        editTextArea,
                        editTextAddress,
                        editTextDesc,
                        editTextFilterTime,
                        editTextCoffeeType,
                        editTextCost,
                        "0",
                        binding.btParking.isChecked,
                        binding.btRestZone.isChecked,
                        binding.btConferenceHall.isChecked,
                        editTextPhone,
                        editTextMail,
                        editTextSite,
                        "",
                        Status.UNDERREVIEW.status,
                        placeId
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        val responsePayload = mainApi.loadRedactPayload("Bearer $token", payload)
                        mainApi.loadRedactTags("Bearer $token", placeId, listTagsId.toList())
//                        val responsePhoto = mainApi.loadRedactPhoto("Bearer $token", placeId, imageParts)
                        requireActivity().runOnUiThread {
                            findNavController().navigate(R.id.action_redactPlaceCommonFrag_to_settingsPlacesCommonFrag)
                        }
                    }
                }
            }
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

    private fun initListTags() {
        adapterTags = TagsAddNewPlaceCardAdapter()

        adapterTagAdd = TagsRedactPlaceCardAdapter()
        adapterTagAdd.setOnButtonClickListener(object: TagsRedactPlaceCardAdapter.OnButtonClickListener {
            override fun onClick(tag: Tag) {
                listTagsPlaceCard.remove(tag)
                Log.i("CountTags", listTagsPlaceCard.size.toString())

                adapterTagAdd.submitList(listTagsPlaceCard.toList())
            }
        })
        binding.idListTagsPlaceCard.layoutManager = GridLayoutManager(context, 3)
        binding.idListTagsPlaceCard.adapter = adapterTagAdd

        adapterTags.setOnButtonClickListener(object: TagsAddNewPlaceCardAdapter.OnButtonClickListener {
            override fun onClick(tag: Tag) {
                if (!listTagsPlaceCard.contains(tag)) {
                    listTagsPlaceCard.add(tag)
                    Log.i("CountTags", listTagsPlaceCard.size.toString())

                    adapterTagAdd.submitList(listTagsPlaceCard.toList())
                }
            }
        })

        binding.idListTagsClick.layoutManager = LinearLayoutManager(context)
        binding.idListTagsClick.adapter = adapterTags

        CoroutineScope(Dispatchers.IO).launch {
            val listTags: List<Tag> = mainApi.getTagsAll()
            requireActivity().runOnUiThread {
                adapterTags.submitList(listTags)
                binding.imIconClick.setOnClickListener {
                    if (binding.idListTagsClick.isVisible)
                        binding.idListTagsClick.visibility = View.GONE
                    else
                        binding.idListTagsClick.visibility = View.VISIBLE
                }
            }
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
                        adapterTagAdd.submitList(currentPlace.tags)

                        btParking.isChecked = currentPlace.parking
                        btRestZone.isChecked = currentPlace.recreationArea
                        btConferenceHall.isChecked = currentPlace.conferenceHall

                        edTextPhone.setText(currentPlace.companyPhone)
                        edTextMail.setText(currentPlace.email)
                        edTextSite.setText(currentPlace.site)

                        //photo не сделано
                        val listPictures: List<String> = currentPlace.photo.split('#')
                        if (!listPictures[0].equals(null)) {
                            Glide.with(this@RedactPlaceCommonFragment)
                                .load(listPictures[0])
                                .transform(RoundedCorners(20))
                                .centerCrop()
                                .error(R.drawable.ic_launcher_foreground)
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .into(idPhoto1)
                        } else if (!listPictures[1].equals(null)) {
                            Glide.with(this@RedactPlaceCommonFragment)
                                .load(listPictures[1])
                                .transform(RoundedCorners(20))
                                .centerCrop()
                                .error(R.drawable.ic_launcher_foreground)
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .into(idPhoto2)
                        } else if (!listPictures[2].equals(null)) {
                            Glide.with(this@RedactPlaceCommonFragment)
                                .load(listPictures[2])
                                .transform(RoundedCorners(20))
                                .centerCrop()
                                .error(R.drawable.ic_launcher_foreground)
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .into(idPhoto3)
                        }
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
                findNavController().navigate(R.id.action_redactPlaceCommonFrag_to_addNewPlaceCommonFrag)
            }
            tvFavorites.setOnClickListener {
                findNavController().navigate(R.id.action_redactPlaceCommonFrag_to_favouritesCommonFrag)
            }
            tvSettingsProfile.setOnClickListener {
                findNavController().navigate(R.id.action_redactPlaceCommonFrag_to_settingsProfileCommonFrag)
            }
            tvSettingsPlaces.setOnClickListener {
                findNavController().navigate(R.id.action_redactPlaceCommonFrag_to_settingsPlacesCommonFrag)
            }
        }
    }
}
