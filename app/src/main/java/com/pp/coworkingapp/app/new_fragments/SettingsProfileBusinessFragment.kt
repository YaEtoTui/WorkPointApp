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
import com.pp.coworkingapp.databinding.FragmentSettingsProfileBusinessBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class SettingsProfileBusinessFragment : Fragment() {

    private lateinit var binding: FragmentSettingsProfileBusinessBinding
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var mainApi: MainApi
    private lateinit var tokenUser: String
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsProfileBusinessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainApi = Common.retrofitService
        initCurrentPerson()

        initSettings()
        initMenu()

        binding.btBackToMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_settingsProfileBusinessFrag_to_mainPageFragment)
        }

        binding.idCircleChangeAvatar.setOnClickListener {
            getPhoto()
        }

        binding.btSave.setOnClickListener {
            changeSettings()
        }

        binding.tvlogOutBusiness.setOnClickListener {
            findNavController().navigate(R.id.action_settingsProfileBusinessFrag_to_authFragment)
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

    private fun initCurrentPerson() {
        //создание текущего user
        viewModel.token.observe(viewLifecycleOwner) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                Log.i("Token", token.toString())
                val currentUser = mainApi.checkUser("Bearer $token")
                requireActivity().runOnUiThread {
                    //Настраиваем кнопку настройки пользователя
                    binding.idAccount.setOnClickListener {
                        if (binding.idListAccountBusiness.isVisible)
                            binding.idListAccountBusiness.visibility = View.GONE
                        else
                            binding.idListAccountBusiness.visibility = View.VISIBLE
                    }

                    tokenUser = token
                    userViewModel.user.value = currentUser
                    binding.apply {
                        Picasso.get().load(currentUser.photoUser).into(binding.imAvatar)
//                        binding.idTextCity.text = currentUser.city
                        tvFio.text = String.format("%s %s", currentUser.name, currentUser.surname)

                        Picasso
                            .get()
                            .load(currentUser.photoUser)
                            .into(imChangeAvatar)

                        if (currentUser.name.length + currentUser.surname.length >= 16) {
                            tvNameAccount.text = String.format("%s %s.", currentUser.name, currentUser.surname.substring(0,1))
                        } else {
                            tvNameAccount.text = String.format("%s %s", currentUser.name, currentUser.surname)
                        }

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

    private fun initMenu() {
        binding.apply {
            idTvFavorites.setOnClickListener {
                findNavController().navigate(R.id.action_settingsProfileBusinessFrag_to_favouritesBusinessFrag)
            }
            idSettingsPlaces.setOnClickListener {
                findNavController().navigate(R.id.action_settingsProfileBusinessFrag_to_settingsPlacesBusinessFrag)
            }
            idPromotion.setOnClickListener {
                findNavController().navigate(R.id.action_settingsProfileBusinessFrag_to_promotionFrag)
            }
        }
    }

    private fun initSettings() {
        binding.apply {
            binding.tvAddPlaceBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_settingsProfileBusinessFrag_to_addNewPlaceBusinessFrag)
            }
            tvFavoritesBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_settingsProfileBusinessFrag_to_favouritesBusinessFrag)
            }
            tvSettingsPlacesBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_settingsProfileBusinessFrag_to_settingsPlacesBusinessFrag)
            }
            tvPromotionBusiness.setOnClickListener {
                findNavController().navigate(R.id.action_settingsProfileBusinessFrag_to_promotionFrag)
            }
        }
    }
}