package com.example.daisoworks.ui.itemmaster

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HerpitemViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is menu_itemmaster Fragment"
    }
    val text: LiveData<String> = _text
}