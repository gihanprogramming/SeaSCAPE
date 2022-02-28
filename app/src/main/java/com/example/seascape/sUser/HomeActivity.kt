package com.example.seascape.sUser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import com.example.seascape.R

class HomeActivity : AppCompatActivity() {

    lateinit var rlMen : RelativeLayout
    lateinit var rlWomen : RelativeLayout
    lateinit var rlKids : RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        rlMen = findViewById(R.id.rlMen)
        rlWomen = findViewById(R.id.rlWomen)
        rlKids = findViewById(R.id.rlKids)

        rlMen.setOnClickListener {
            startActivity(Intent(this@HomeActivity, MenActivity::class.java))
            finish()
        }
    }
}