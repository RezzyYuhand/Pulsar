package com.example.pulsar

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class RegisterViewModel: ViewModel() {
    var fireBaseAuth = FirebaseAuth.getInstance()
    fun register(email: String, pass: String, onSuccess:() -> Unit, onFailure:() -> Unit){
        fireBaseAuth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                onFailure()
            }
    }

}