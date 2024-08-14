package com.example.daisoworks.ui.client

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HerpclientViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is HerpclientViewModel Fragment"
    }
    val text: LiveData<String> = _text
}