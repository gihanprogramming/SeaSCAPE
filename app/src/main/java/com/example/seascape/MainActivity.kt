package com.example.seascape

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Pair as UtilPair
import android.widget.ImageView
import android.widget.TextView
import com.example.seascape.sLoginRegister.LoginActivity
import com.example.seascape.sUser.UserHomeActivity

class MainActivity : AppCompatActivity() {

    //pre initialize variables
    private lateinit var imageView : ImageView
    private lateinit var textViewLogo : TextView
    private lateinit var textView1 : TextView
    private lateinit var textView2 : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        textViewLogo = findViewById(R.id.textViewLogo)
        textView1 = findViewById(R.id.textView1)
        textView2 = findViewById(R.id.textView2)

        Handler().postDelayed({
            val gotoLogin = Intent(this@MainActivity, LoginActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this,
            UtilPair.create(imageView, "logo_image"),
            UtilPair.create(textViewLogo, "logo_title"),
            UtilPair.create(textView1, "logo_desc"),
            UtilPair.create(textView2, "logo_desc"))
            startActivity(gotoLogin, options.toBundle())
            finish()
        },3000)
    }
}