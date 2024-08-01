package com.example.daisoworks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private var inputText: MutableLiveData<String> = MutableLiveData()

    fun getData(): MutableLiveData<String> = inputText

    fun updateText(input: String) {
        inputText.value = input
    }
}