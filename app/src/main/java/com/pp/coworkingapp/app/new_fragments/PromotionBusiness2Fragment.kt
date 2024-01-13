package com.pp.coworkingapp.app.new_fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.enum.Status
import com.pp.coworkingapp.app.enum.StatusAdd
import com.pp.coworkingapp.app.retrofit.api.MainApi
import com.pp.coworkingapp.app.retrofit.domain.Common
import com.pp.coworkingapp.app.retrofit.domain.request.Payload
import com.pp.coworkingapp.app.retrofit.domain.request.PayloadAdvertisement
import com.pp.coworkingapp.app.retrofit.domain.response.CurrentUser
import com.pp.coworkingapp.app.retrofit.domain.viewModel.AuthViewModel
import com.pp.coworkingapp.app.retrofit.domain.viewModel.PlaceIdViewModel
import com.pp.coworkingapp.databinding.FragmentPromotionBusiness2Binding
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
import java.text.SimpleDateFormat
import java.util.Calendar

class PromotionBusiness2Fragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentPromotionBusiness2Binding
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var mainApi: MainApi
    private lateinit var tokenUser: String
    private val placeIdViewModel: PlaceIdViewModel by activityViewModels()

    private lateinit var listPhotoPlaceCard: Array<Uri?>
    private var photoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPromotionBusiness2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listPhotoPlaceCard = arrayOfNulls<Uri?>(size = 1)

        initSettings()
        initMenu()
        mainApi = Common.retrofitService
        initCurrentPerson()

        initPlaceCard()

        binding.btLoadFile.setOnClickListener {
            getPhoto(PICK_IMAGE1)
            binding.btLoadFile.visibility = View.VISIBLE
        }

        binding.edTextStartDate.setOnClickListener {
            // Получаем текущую дату
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Создаем диалог выбора даты
            val datePickerDialog = DatePickerDialog(requireContext(), this, year, month, day)
            datePickerDialog.show()
        }

        binding.btBackToMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_promotion2Frag_to_mainPageFragment)
        }

        binding.tvlogOutBusiness.setOnClickListener {
            findNavController().navigate(R.id.action_promotion2Frag_to_authFragment)
        }
    }

    private val PICK_IMAGE1 = 1

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
            loadPhoto(imageUri, binding.imFile)
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

    private fun initUploadAdvertisement(currentUser: CurrentUser) {

        binding.btSendApplication.setOnClickListener {
            val tariff: String = if (binding.btOneDay.isChecked)
                binding.btOneDay.text.toString()
            else
                binding.btSevenDay.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                val payload: PayloadAdvertisement = PayloadAdvertisement(
                    currentUser.id,
                    binding.edTextNameInstitution.text.toString(),
                    binding.edTextCity.text.toString(),
                    binding.edTextArea.text.toString(),
                    tariff,
                    binding.edTextEmail.text.toString(),
                    StatusAdd.UNDERREVIEW.statusAdd,
                    0,
                    binding.edTextStartDate.text.toString(),
                    binding.edTextEndDate.text.toString(),
                    ""
                )
                val payloadJson = Gson().toJson(payload)
                val payloadRequestBody =
                    payloadJson.toRequestBody("application/json".toMediaTypeOrNull())

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

                try {
                    val currentResponse = mainApi.uploadAd(
                        "Bearer $tokenUser",
                        payloadRequestBody,
                        imageParts[0]
                    )
                } catch (exc: Exception) {
                    exc.message
                }

                requireActivity().runOnUiThread {
                    findNavController().navigate(R.id.action_promotion2Frag_to_promotion3Frag)
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

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = Calendar.getInstance()
        date.set(year, month, dayOfMonth)
        val dateStart: String = dateFormat.format(date.time)
        val dateEnd: String = if (binding.btOneDay.isChecked) {
            date.add(Calendar.DAY_OF_MONTH, 1)
            dateFormat.format(date.time)
        } else {
            date.add(Calendar.DAY_OF_MONTH, 7)
            dateFormat.format(date.time)
        }

        binding.edTextStartDate.setText(dateStart)
        binding.edTextEndDate.setText(dateEnd)
        Log.i("ResponseTime", dateStart)
        CoroutineScope(Dispatchers.IO).launch {
            val response: Boolean = mainApi.getCountAd(dateEnd, dateEnd)
        }
    }

    private fun initPlaceCard() {
        placeIdViewModel.placeId.observe(viewLifecycleOwner) { placeId ->
            CoroutineScope(Dispatchers.IO).launch {
                val currentPlace = mainApi.findPlaceCard(placeId)
                requireActivity().runOnUiThread {
                    binding.apply {
                        edTextNameInstitution.setText(currentPlace.name)
                        edTextCity.setText(currentPlace.city)
                        edTextArea.setText(currentPlace.district)
                        edTextEmail.setText(currentPlace.email)
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
                val currentUser: CurrentUser = mainApi.checkUser("Bearer $token")
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

                    initUploadAdvertisement(currentUser)

                }
            }
        }
    }

    private fun initMenu() {
        binding.apply {
            idTvFavorites.setOnClickListener {
                findNavController().navigate(R.id.action_promotion2Frag_to_favouritesBusinessFrag)
            }
            idTvSettingsProfile.setOnClickListener {
                findNavController().navigate(R.id.action_promotion2Frag_to_settingsProfileBusinessFrag)
            }
            idSettingsPlaces.setOnClickListener {
                findNavController().navigate(R.id.action_promotion2Frag_to_settingsPlacesBusinessFrag)
            }
        }
    }

    private fun initSettings() {
        binding.apply {
            binding.tvAddPlaceBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_promotion2Frag_to_addNewPlaceBusinessFrag)
            }
            tvFavoritesBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_promotion2Frag_to_favouritesBusinessFrag)
            }
            tvSettingsProfileBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_promotion2Frag_to_settingsProfileBusinessFrag)
            }
            tvSettingsPlacesBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_promotion2Frag_to_settingsPlacesBusinessFrag)
            }
            tvPromotionBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_promotion2Frag_to_promotionFrag)
            }
        }
    }
}