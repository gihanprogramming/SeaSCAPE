package com.example.seascape.sLoginRegister

import android.app.ActivityOptions
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.example.seascape.R
import com.example.seascape.sUser.UserHomeActivity
import com.google.android.material.textfield.TextInputEditText
import android.util.Pair as UtilPair

class LoginActivity : AppCompatActivity() {

    //pre initialize variables
    private lateinit var ivLogo : ImageView
    private lateinit var tvLogo_name : TextView
    private lateinit var tvLogo_intro : TextView
    private lateinit var etEmail : TextInputEditText
    private lateinit var etPassword : TextInputEditText
    private lateinit var btnForget_password : Button
    private lateinit var btnLogin : Button
    private lateinit var btnRegister : Button
    private lateinit var progressDialog : ProgressDialog

    //FirebaseAuth
    private lateinit var firebaseAuth : FirebaseAuth
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //assign Elements to variables
        ivLogo = findViewById(R.id.ivLogo)
        tvLogo_name = findViewById(R.id.tvLogo_name)
        tvLogo_intro = findViewById(R.id.tvLogo_intro)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnForget_password = findViewById(R.id.btnForget_password)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)


        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Logging In...")
        progressDialog.setCanceledOnTouchOutside(false)

        //initialize firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //Forget Password Button
        btnForget_password.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForegetPasswordActivity::class.java))
            finish()
        }

        //Login Button
        btnLogin.setOnClickListener {
            //before logging in, validate data
            validateData()
        }

        //Register page switch button
        btnRegister.setOnClickListener {
            //goto register page
            gotoRegisterPage()
        }
    }

    private fun checkUser()
    {
        //if user already logged in go to UserHomeActivity
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null)
        {
            //user is already logged in
            startActivity(Intent(this, UserHomeActivity::class.java))
            finish()
        }
    }

    private fun validateData(){
        //get data
        email = etEmail.text.toString().trim()
        password = etPassword.text.toString().trim()

        //validate data
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            //invalid email format
            etEmail.error = "Invalid email format"
        }else if (TextUtils.isEmpty(password)){
            //no password entered
            etPassword.error = "Please enter password"
        }
        else{
            //data is validated, begin login
            firebaseLogin()
        }
    }

    private fun firebaseLogin(){
        //show progress
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //login success
                progressDialog.dismiss()

                //get user info
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                var fireStore  = FireStore()
                fireStore.getUserDetails(this@LoginActivity)
                Toast.makeText(this, "LoggedIn as $email", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{ e->
                //login failed
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun gotoRegisterPage()
    {
        val gotoRegister = Intent(this@LoginActivity, RegisterActivity::class.java)
        val option = ActivityOptions.makeSceneTransitionAnimation(this,
            UtilPair.create(ivLogo, "logo_image"),
            UtilPair.create(tvLogo_name, "Logo_title"),
            UtilPair.create(tvLogo_intro, "Logo_desc"))
        startActivity(gotoRegister, option.toBundle())
        finish()
    }

    public fun userLoggedInSuccess(user: User){
        progressDialog.hide()
        Log.i("Full name: ", user.fullname)
        Log.i("Email: ", user.email)
        Log.i("Address: ", user.address)
        Log.i("Gender: ", user.gender)

        //goto user page
        startActivity(Intent(this, UserHomeActivity::class.java))
        finish()
    }
}