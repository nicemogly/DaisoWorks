package com.example.daisoworks.ui.exhibition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ExhibitionViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This isExhibition Fragment"
    }
    val text: LiveData<String> = _text
}

