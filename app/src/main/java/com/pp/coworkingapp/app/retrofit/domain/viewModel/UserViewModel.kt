package com.pp.coworkingapp.app.retrofit.domain.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pp.coworkingapp.app.retrofit.domain.response.CurrentUser

class UserViewModel: ViewModel() {
    val user = MutableLiveData<CurrentUser>()
}