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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.retrofit.api.MainApi
import com.pp.coworkingapp.app.retrofit.domain.Common
import com.pp.coworkingapp.app.retrofit.domain.request.CreateSettingsUserRequest
import com.pp.coworkingapp.app.retrofit.domain.viewModel.AuthViewModel
import com.pp.coworkingapp.app.retrofit.domain.viewModel.UserViewModel
import com.pp.coworkingapp.databinding.FragmentSettingsProfileCommonBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
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
        }

        binding.btSearchCow.setOnClickListener {
            findNavController().navigate(R.id.action_settingsProfileCommonFrag_to_addNewPlaceCommonFrag)
        }

        binding.btSave.setOnClickListener {
            changeSettings()
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
                    val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("file", userViewModel.user.value?.surname.toString(), requestFile)
                    val photoString = mainApi.loadNewPhotoUser("Bearer $token", body)
                    requireActivity().runOnUiThread {
//                        Picasso
//                            .get()
//                            .load(photoString)
//                            .into(binding.imChangeAvatar)
//                        Picasso
//                            .get()
//                            .load(photoString)
//                            .into(binding.imAvatar)
                    }
                }
            }
        } catch (exc: Exception) {
            exc.message
        }

    }

    fun getRealPathFromUri(context: Context, contentUri: Uri): File {
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
                val numberCoffee = mainApi.getPlaceCoffee("Bearer $token", currentUser.id)
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
                    if (numberCoffee.isNotEmpty())
                        binding.tvCoffeeNumber.text = "$numberCoffee/5"
                    else
                        binding.tvCoffeeNumber.text = "0/5"

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