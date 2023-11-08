package com.pp.coworkingapp.app.retrofit.domain.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    val token = MutableLiveData<String>()
}