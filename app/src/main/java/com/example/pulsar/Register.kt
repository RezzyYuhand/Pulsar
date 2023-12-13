package com.example.pulsar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsar.databinding.ActivityRegisterBinding

import com.google.android.material.textfield.TextInputEditText

private lateinit var registerViewModel: RegisterViewModel
class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerViewModel = RegisterViewModel()

        val createAccountBtn: Button = findViewById(R.id.createaccbtn)
        val emailInputText: TextInputEditText = findViewById(R.id.emailInput)
        val passInputText: TextInputEditText = findViewById(R.id.passInput)

        createAccountBtn.setOnClickListener {
            val email = emailInputText.text.toString()
            val pass = passInputText.text.toString()
            registerViewModel.register(email, pass,
                onSuccess = {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                },
                onFailure = {
                    Toast.makeText(
                        this,
                        "Registration failed. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}