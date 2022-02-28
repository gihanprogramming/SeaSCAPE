package com.example.seascape.sLoginRegister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import com.example.seascape.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ForegetPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail : TextInputEditText
    private lateinit var btnSubmit : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foreget_password)

        etEmail = findViewById(R.id.etEmail)
        btnSubmit = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword()
    {
        val email : String = etEmail.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.error = "Please enter a valid email"
        }else{
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            Toast.makeText(this, "Email sent successfully to reset your password!", Toast.LENGTH_LONG).show()
        }
    }
}