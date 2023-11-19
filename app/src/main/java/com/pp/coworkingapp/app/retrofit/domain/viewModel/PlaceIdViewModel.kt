package com.pp.coworkingapp.app.retrofit.domain.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaceIdViewModel: ViewModel() {
    val placeId = MutableLiveData<Int>()
}