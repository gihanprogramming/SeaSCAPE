package com.example.seascape.sUser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.seascape.R

class UserHomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        startActivity(Intent(this@UserHomeActivity, HomeActivity::class.java))
        finish()
    }
}