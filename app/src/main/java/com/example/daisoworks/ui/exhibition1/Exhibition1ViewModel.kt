package com.example.daisoworks.ui.exhibition1
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel





class Exhibition1ViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This isExhibitionview Fragment"
    }
    val text: LiveData<String> = _text
}

