package com.example.daisoworks.ui.test1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Test1ViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is test1 Fragment"
    }
    val text: LiveData<String> = _text
}