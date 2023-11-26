package com.example.pulsar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    val id = MutableLiveData<String>().apply {
        value = "Please Insert you device ID to continue"
    }
    val text: LiveData<String> = id
}