package com.pp.coworkingapp.app.new_fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.retrofit.api.MainApi
import com.pp.coworkingapp.app.retrofit.domain.Common
import com.pp.coworkingapp.app.retrofit.domain.request.CreateSettingsUserRequest
import com.pp.coworkingapp.app.retrofit.domain.response.Place
import com.pp.coworkingapp.app.retrofit.domain.viewModel.AuthViewModel
import com.pp.coworkingapp.app.retrofit.domain.viewModel.UserViewModel
import com.pp.coworkingapp.databinding.FragmentSettingsProfileCommonBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File


class SettingsProfileCommonFragment : Fragment() {

    private lateinit var binding: FragmentSettingsProfileCommonBinding
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var mainApi: MainApi
    private lateinit var tokenUser: String
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsProfileCommonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainApi = Common.retrofitService
        initCurrentPerson()
        initSettings()
        initMenu()
        initButtonChangeRole()

        binding.btBackToMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_settingsProfileCommonFrag_to_mainPageFragment)
        }

        binding.idCircleChangeAvatar.setOnClickListener {
            getPhoto()
//            addPhotoFromIntent()
        }

        binding.btSearchCow.setOnClickListener {
            findNavController().navigate(R.id.action_settingsProfileCommonFrag_to_addNewPlaceCommonFrag)
        }

        binding.btSave.setOnClickListener {
            changeSettings()
        }

        binding.tvlogOut.setOnClickListener {
            findNavController().navigate(R.id.action_settingsProfileCommonFrag_to_authFragment)
        }
    }

    companion object {
        private const val CODE_IMG_GALLERY = 111
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
    }

    private fun addPhotoFromIntent() {
        val cameraIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE).takeIf { intent ->
            intent.resolveActivity(requireActivity().packageManager) != null
        }

        val galleryIntent = Intent(Intent.ACTION_PICK).apply { this.type = "image/*" }

        val intentChooser = Intent(Intent.ACTION_CHOOSER).apply {
            this.putExtra(Intent.EXTRA_INTENT, galleryIntent)
            cameraIntent?.let { intent ->
                this.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayListOf(intent).toTypedArray<Parcelable>())
            }
            this.putExtra(Intent.EXTRA_TITLE, resources.getString(R.string.gallery_title))
        }

        startActivityForResult(intentChooser, CODE_IMG_GALLERY)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == CODE_IMG_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
//            val imageBitmap = data.createBitmapFromResult(requireActivity())
//            sendFileRequest(image = imageBitmap!!)
//        }
//    }

    private fun sendFileRequest(image: Bitmap) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val body = MultipartBody.Part.createFormData(
            userViewModel.user.value!!.surname,
            userViewModel.user.value!!.surname,
            byteArray.toRequestBody("image/*".toMediaTypeOrNull(), 0, byteArray.size)
        )

        try {
            viewModel.token.observe(viewLifecycleOwner) { token ->
                CoroutineScope(Dispatchers.IO).launch {
                    val photoString = mainApi.loadNewPhotoUser("Bearer $token", body)
                    val message = photoString.errorBody()?.string()?.let { JSONObject(it).getString("detail")}
                }
            }
        } catch (exc: Exception) {
            exc.message
        }
    }

    private fun initButtonChangeRole() {
        binding.btChangeOnBusiness.setOnClickListener {
            viewModel.token.observe(viewLifecycleOwner) { token ->
                CoroutineScope(Dispatchers.IO).launch {
                    val response = mainApi.changeRole("Bearer $token", 2)
                    requireActivity().runOnUiThread {
                        findNavController().navigate(R.id.action_settingsProfileCommonFrag_to_settingsProfileBusinessFrag)
                    }
                }
            }
        }
    }

    private fun changeSettings() {
        viewModel.token.observe(viewLifecycleOwner) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                val responseServer = mainApi.changeSettingsUser("Bearer $token", CreateSettingsUserRequest(
                    binding.edTextPhone.text.toString(),
                    binding.edTextFirstName.text.toString(),
                    binding.edTextSurName.text.toString(),
                    binding.edTextCity.text.toString(),
                    userViewModel.user.value!!.id
                ))
                requireActivity().runOnUiThread {
                    Log.i("ResultHttpChangeSettingsUser", responseServer.toString())
                }
            }
        }
    }


    private val PICK_IMAGE = 1

    private fun getPhoto(){
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data

            loadPhotoInDB(imageUri)
        }
    }

    private fun loadPhotoInDB(imageUri: Uri?) {
        try {
            viewModel.token.observe(viewLifecycleOwner) { token ->
                CoroutineScope(Dispatchers.IO).launch {
                    val file: File = getRealPathFromUri(requireContext(), imageUri!!)
                    val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", userViewModel.user.value?.surname.toString(), requestFile)
                    val photo: Response<String> = mainApi.loadNewPhotoUser("Bearer $token", body)
                    val message: String? = photo.errorBody()?.string()?.let { JSONObject(it).getString("detail")}
                }
            }
        } catch (exc: Exception) {
            exc.message
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
                findNavController().navigate(R.id.action_settingsProfileCommonFrag_to_favouritesCommonFrag)
            }
            idSettingsPlaces.setOnClickListener {
                findNavController().navigate(R.id.action_settingsProfileCommonFrag_to_settingsPlacesCommonFrag)
            }
        }
    }

    private fun initSettings() {
        binding.apply {
            tvAddPlace.setOnClickListener {
                findNavController().navigate(R.id.action_settingsProfileCommonFrag_to_addNewPlaceCommonFrag)
            }
            tvFavorites.setOnClickListener {
                findNavController().navigate(R.id.action_settingsProfileCommonFrag_to_favouritesCommonFrag)
            }
            tvSettingsPlaces.setOnClickListener {
                findNavController().navigate(R.id.action_settingsProfileCommonFrag_to_settingsPlacesCommonFrag)
            }
        }
    }

    private fun initCurrentPerson() {
        //создание текущего user
        viewModel.token.observe(viewLifecycleOwner) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                Log.i("Token", token.toString())
                val currentUser = mainApi.checkUser("Bearer $token")
                var numberCoffee: List<Place> = mainApi.getPlaceCoffee("Bearer $token", currentUser.id)
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

                    if (numberCoffee.isEmpty()) {
                        binding.imProgressBar.setBackgroundResource(R.drawable.progress_0)
                    } else if (numberCoffee.size == 1) {
                        binding.imProgressBar.setBackgroundResource(R.drawable.progress_1)
                    } else if (numberCoffee.size == 2) {
                        binding.imProgressBar.setBackgroundResource(R.drawable.progress_2)
                    } else if (numberCoffee.size == 3) {
                        binding.imProgressBar.setBackgroundResource(R.drawable.progress_3)
                    } else if (numberCoffee.size == 4) {
                        binding.imProgressBar.setBackgroundResource(R.drawable.progress_4)
                    } else if (numberCoffee.size >= 5) {
                        binding.imProgressBar.setBackgroundResource(R.drawable.progress_5)
                    }

                    tokenUser = token
                    userViewModel.user.value = currentUser
                    binding.apply {
                        Picasso.get().load(currentUser.photoUser).into(binding.imAvatar)
                        binding.tvNameAccount.text =
                            String.format("%s %s", currentUser.name, currentUser.surname)
//                        binding.idTextCity.text = currentUser.city

                        Picasso
                            .get()
                            .load(currentUser.photoUser)
                            .into(imChangeAvatar)

                        tvFio.text = "${currentUser.name} ${currentUser.surname}"
                        tvPhone.text = currentUser.phone
                        edTextFirstName.setText(currentUser.name)
                        edTextSurName.setText(currentUser.surname)
                        edTextCity.setText(currentUser.city)
                        edTextPhone.setText(currentUser.phone)

//                        imProgressBar.isIndeterminate = false
//                        imProgressBar.setProgress(4)
                    }
                }
            }
        }
    }
}