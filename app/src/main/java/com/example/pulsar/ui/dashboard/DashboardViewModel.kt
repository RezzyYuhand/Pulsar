package com.example.pulsar.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class DashboardViewModel : ViewModel() {

    private var fireBaseAuth = FirebaseAuth.getInstance()

    private val _text = MutableLiveData<String>().apply {
        value = "Please Insert you device ID to continue"
    }
    val text: LiveData<String> = _text

    private val id = MutableLiveData<String>().apply {
        value = ""
    }
    val idDevice: LiveData<String> = id

    fun logout(){
        fireBaseAuth.signOut()
    }


}