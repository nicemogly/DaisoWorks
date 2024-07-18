package com.example.daisoworks.ui.approve

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ApproveViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is DMS Fragment"
    }
    val text: LiveData<String> = _text


}


