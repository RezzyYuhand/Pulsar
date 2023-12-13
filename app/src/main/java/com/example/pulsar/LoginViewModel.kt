package com.example.pulsar

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel: ViewModel() {
    var fireBaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, pass: String, onSuccess:() -> Unit, onFailure:() -> Unit){
        fireBaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun checkLogin(userLogin:() -> Unit){
        if (fireBaseAuth.currentUser != null) userLogin()
    }
}