package com.example.pulsar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsar.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputEditText


private lateinit var loginViewModel: LoginViewModel
class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = LoginViewModel()
        loginViewModel.checkLogin {
            startActivity(Intent(this, MainActivity::class.java))
        }
        Thread.sleep(2000)

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        val loginBtn: Button = findViewById(R.id.loginbtn)
        val registerBtn: Button = findViewById(R.id.registerbtn)
        val emailInput: TextInputEditText = findViewById(R.id.emailLogin)
        val passInput: TextInputEditText = findViewById(R.id.passLogin)

        loginBtn.setOnClickListener {
            val email = emailInput.text.toString()
            val pass = passInput.text.toString()
            loginViewModel.login(email, pass,
                onSuccess = {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                },
                onFailure = {
                    Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            )
        }

        registerBtn.setOnClickListener {
            Log.e("MASUK", "Udah masuk")
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

    }
}