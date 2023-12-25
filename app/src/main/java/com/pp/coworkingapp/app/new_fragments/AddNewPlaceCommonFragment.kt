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
import com.pp.coworkingapp.app.retrofit.domain.response.Tag
import com.pp.coworkingapp.app.retrofit.domain.viewModel.AuthViewModel
import com.pp.coworkingapp.app.retrofit.domain.viewModel.UserViewModel
import com.pp.coworkingapp.databinding.FragmentAddNewPlaceCommonBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File


class AddNewPlaceCommonFragment : Fragment() {

    private lateinit var binding: FragmentAddNewPlaceCommonBinding
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var mainApi: MainApi
    private lateinit var tokenUser: String
    private lateinit var adapter : FilterAdapter
    private lateinit var adapterTags : TagsAddNewPlaceCardAdapter
    private lateinit var adapterTagAdd : TagsRedactPlaceCardAdapter
    private lateinit var listTagsPlaceCard: ArrayList<Tag>
    private lateinit var listPhotoPlaceCard: Array<Uri?>
    private var photoUri: Uri? = null

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNewPlaceCommonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listTagsPlaceCard = ArrayList()
        listPhotoPlaceCard = arrayOfNulls<Uri?>(size = 3)

        mainApi = Common.retrofitService
        initCurrentPerson()

        initMenu()
        initSettings()

        initListHours()
        initListTypeCoffee()
        initListCost()
        initListTags()
        initListPhoto()

        saveCurrentPlaceCard()

        binding.btDeleteNewCard.setOnClickListener {
            findNavController().navigate(R.id.action_addNewPlaceCommonFrag_to_settingsPlacesCommonFrag)
        }

//        binding.btParking.setOnClickListener {
//            if (!binding.btParking.isChecked) {
//                binding.btParking.isChecked = false
//            } else {
//                binding.btParking.isChecked = true
//            }
//        }

        binding.btBackToMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_addNewPlaceCommonFrag_to_mainPageFragment)
        }
    }

    private fun saveCurrentPlaceCard() {
        binding.btSaveNewCard.setOnClickListener {
            binding.apply {
                val editTextNamePlace: String = edTextNameInstitution.text.toString()
                val editTextCity: String = edTextCity.text.toString()
                val editTextArea: String = edTextArea.text.toString()
                val editTextAddress: String = edTextAddress.text.toString()

                val editTextDesc: String = edTextDesc.text.toString()

                val editTextFilterTime: String = edTextFilterTime.text.toString()
                val editTextCoffeeType: String = edTextFilterCoffeeType.text.toString()
                val editTextCost: String = edTextFilterCost.text.toString()

                val countListTags: Int = idListTagsPlaceCard.size

                val editTextPhone: String = edTextPhone.text.toString()
                val editTextMail: String = edTextMail.text.toString()
                val editTextSite: String = edTextSite.text.toString()

                if (editTextNamePlace.isEmpty() || editTextCity.isEmpty() || editTextArea.isEmpty() || editTextAddress.isEmpty()
                    || editTextDesc.isEmpty()
                    || editTextFilterTime.isEmpty() || editTextCoffeeType.isEmpty() || editTextCost.isEmpty()
                    || countListTags == 0
                    || editTextPhone.isEmpty() || editTextMail.isEmpty() || editTextSite.isEmpty()) {

                    if (editTextPhone.isEmpty()) {
                        idLayoutEdPhone.setBackgroundResource(R.drawable.error_red_add_new_place_card)
                        tvErrorPhone.visibility = View.VISIBLE
                    }
                    if (editTextMail.isEmpty()) {
                        idLayoutEdMail.setBackgroundResource(R.drawable.error_red_add_new_place_card)
                        tvErrorMail.visibility = View.VISIBLE
                    }
                    if (editTextSite.isEmpty()) {
                        idLayoutEdSite.setBackgroundResource(R.drawable.error_red_add_new_place_card)
                        tvErrorSite.visibility = View.VISIBLE
                    }

                    if (editTextNamePlace.isEmpty()) {
                        idLayoutEdNameInstitution.setBackgroundResource(R.drawable.error_red_add_new_place_card)
                        tvErrorNameInstitution.visibility = View.VISIBLE
                    }
                    if (editTextCity.isEmpty()) {
                        idLayoutEdCity.setBackgroundResource(R.drawable.error_red_add_new_place_card)
                        tvErrorCity.visibility = View.VISIBLE
                    }
                    if (editTextArea.isEmpty()) {
                        idLayoutEdArea.setBackgroundResource(R.drawable.error_red_add_new_place_card)
                        tvErrorArea.visibility = View.VISIBLE
                    }
                    if (editTextAddress.isEmpty()) {
                        idLayoutEdAddress.setBackgroundResource(R.drawable.error_red_add_new_place_card)
                        tvErrorAddress.visibility = View.VISIBLE
                    }

                    if (editTextDesc.isEmpty()) {
                        idLayoutEdDesc.setBackgroundResource(R.drawable.error_red_add_new_place_card)
                        tvErrorDesc.visibility = View.VISIBLE
                    }

                    if (editTextFilterTime.isEmpty()) {
                        idLayoutEdFilterTime.setBackgroundResource(R.drawable.error_red_add_new_place_card)
                        tvErrorFilterTime.visibility = View.VISIBLE
                    }
                    if (editTextCoffeeType.isEmpty()) {
                        idLayoutEdFilterCoffeeType.setBackgroundResource(R.drawable.error_red_add_new_place_card)
                        tvErrorFilterCoffeeType.visibility = View.VISIBLE
                    }
                    if (editTextCost.isEmpty()) {
                        idLayoutEdFilterCost.setBackgroundResource(R.drawable.error_red_add_new_place_card)
                        tvErrorFilterCost.visibility = View.VISIBLE
                    }

                    if (countListTags == 0) {
                        idRectangle.setBackgroundResource(R.drawable.error_red_add_new_place_card)
                        tvErrorTags.visibility = View.VISIBLE
                    }
                } else {
                    idLayoutEdNameInstitution.setBackgroundResource(R.drawable.rectangle_edit_settings)
                    tvErrorNameInstitution.visibility = View.INVISIBLE

                    idLayoutEdCity.setBackgroundResource(R.drawable.rectangle_edit_settings)
                    tvErrorCity.visibility = View.INVISIBLE

                    idLayoutEdArea.setBackgroundResource(R.drawable.rectangle_edit_settings)
                    tvErrorArea.visibility = View.INVISIBLE

                    idLayoutEdAddress.setBackgroundResource(R.drawable.rectangle_edit_settings)
                    tvErrorAddress.visibility = View.INVISIBLE


                    idLayoutEdDesc.setBackgroundResource(R.drawable.rectangle_edit_settings)
                    tvErrorDesc.visibility = View.INVISIBLE


                    idLayoutEdFilterTime.setBackgroundResource(R.drawable.rectangle_edit_settings)
                    tvErrorFilterTime.visibility = View.INVISIBLE

                    idLayoutEdFilterCoffeeType.setBackgroundResource(R.drawable.rectangle_edit_settings)
                    tvErrorFilterCoffeeType.visibility = View.INVISIBLE

                    idLayoutEdFilterCost.setBackgroundResource(R.drawable.rectangle_edit_settings)
                    tvErrorFilterCost.visibility = View.INVISIBLE

                    idRectangle.setBackgroundResource(R.drawable.rectangle_present)
                    tvErrorTags.visibility = View.INVISIBLE


                    idLayoutEdPhone.setBackgroundResource(R.drawable.rectangle_edit_settings)
                    tvErrorPhone.visibility = View.INVISIBLE

                    idLayoutEdMail.setBackgroundResource(R.drawable.rectangle_edit_settings)
                    tvErrorMail.visibility = View.INVISIBLE

                    idLayoutEdSite.setBackgroundResource(R.drawable.rectangle_edit_settings)
                    tvErrorSite.visibility = View.INVISIBLE

                    val listTagsId: ArrayList<String> = ArrayList()
                    for(i in 0..<listTagsPlaceCard.size) {
                        if (listTagsPlaceCard[i].name == "Wi-Fi") {
                            listTagsId.add("1")
                        } else if (listTagsPlaceCard[i].name == "Розетки") {
                            listTagsId.add("2")
                        } else if (listTagsPlaceCard[i].name == "Еда") {
                            listTagsId.add("3")
                        } else if (listTagsPlaceCard[i].name == "Напитки") {
                            listTagsId.add("4")
                        } else if (listTagsPlaceCard[i].name == "Канцелярия") {
                            listTagsId.add("5")
                        }
                    }

                    var parking: String = ""
                    if (btParking.isChecked) {
                        parking = "Парковка"
                    } else {
                        parking = ""
                    }

                    var restZone: String = ""
                    if (btParking.isChecked) {
                        restZone = "Зона отдыха"
                    } else {
                        restZone = ""
                    }

                    var conferenceHall: String = ""
                    if (btConferenceHall.isChecked) {
                        conferenceHall = "Конференц-зал"
                    } else {
                        conferenceHall = ""
                    }

                    val files: ArrayList<MultipartBody.Part> = ArrayList()

//                    for(i in 0..<listPhotoPlaceCard.size) {
//                        if (listPhotoPlaceCard[i] != null) {
//                            val newFile: File = getRealPathFromUri(requireContext(), listPhotoPlaceCard[i]!!)
//                            val requestFile = newFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//                            val fileBody: MultipartBody.Part = MultipartBody.Part.createFormData("file", userViewModel.user.value?.surname.toString(), requestFile)
//                            files.add(fileBody)
//                        }
//                    }

                    val newFile: File = getRealPathFromUri(requireContext(), photoUri!!)
                    val requestFile = newFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val fileBody: MultipartBody.Part = MultipartBody.Part.createFormData("file", userViewModel.user.value?.surname.toString(), requestFile)

                    userViewModel.user.observe(viewLifecycleOwner) {user ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val response = mainApi.loadNewPlaceInDB(
                                "Bearer $tokenUser",
                                Payload(
                                    user.id,
                                    editTextNamePlace,
                                    editTextCity,
                                    editTextArea,
                                    editTextAddress,
                                    editTextDesc,
                                    editTextFilterTime,
                                    editTextCoffeeType,
                                    editTextCost,
                                    listTagsId.toList(),
                                    "0",
                                    parking,
                                    restZone,
                                    conferenceHall,
                                    editTextPhone,
                                    editTextMail,
                                    editTextSite,
                                    "",
                                    Status.UNDERREVIEW.status
                                ),
                                fileBody
                            )
                            val message = response.errorBody()?.string()?.let { JSONObject(it).getString("detail")}
                            requireActivity().runOnUiThread {
                                Log.i("Response", message!!)
                            }
                        }
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

    private fun initSettings() {
        binding.apply {
            tvFavorites.setOnClickListener {
                findNavController().navigate(R.id.action_addNewPlaceCommonFrag_to_favouritesCommonFrag)
            }
            tvSettingsProfile.setOnClickListener {
                findNavController().navigate(R.id.action_addNewPlaceCommonFrag_to_settingsProfileCommonFrag)
            }
            tvSettingsPlaces.setOnClickListener {
                findNavController().navigate(R.id.action_addNewPlaceCommonFrag_to_settingsPlacesCommonFrag)
            }
        }
    }

    private fun initMenu() {
        binding.apply {
            idTvFavorites.setOnClickListener {
                findNavController().navigate(R.id.action_addNewPlaceCommonFrag_to_favouritesCommonFrag)
            }
            idTvSettingsProfile.setOnClickListener {
                findNavController().navigate(R.id.action_addNewPlaceCommonFrag_to_settingsProfileCommonFrag)
            }
            idSettingsPlaces.setOnClickListener {
                findNavController().navigate(R.id.action_addNewPlaceCommonFrag_to_settingsPlacesCommonFrag)
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
            Log.i("Photo1", "Тут")
            val imageUri = data?.data
            photoUri = data?.data
            listPhotoPlaceCard[0] = imageUri
            loadPhoto(imageUri, binding.idPhoto1)
        } else if (requestCode == PICK_IMAGE2 && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            listPhotoPlaceCard[1] = imageUri
            loadPhoto(imageUri, binding.idPhoto2)
        } else if (requestCode == PICK_IMAGE3 && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            listPhotoPlaceCard[2] = imageUri
            loadPhoto(imageUri, binding.idPhoto3)
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

                        userViewModel.user.value = currentUser
                    }
                }
            }
        }
    }
}