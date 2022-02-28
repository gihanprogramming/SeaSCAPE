package com.example.seascape.sLoginRegister

import android.app.ActivityOptions
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.util.Patterns
import android.widget.*
import com.example.seascape.R
import com.example.seascape.sUser.UserHomeActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    //initialize variables
    private lateinit var ivLogo : ImageView
    private lateinit var tvLogo_name : TextView
    private lateinit var tvLogo_intro : TextView

    private lateinit var etFull_name : TextInputEditText
    private lateinit var etEmail : TextInputEditText
    private lateinit var etPassword : TextInputEditText
    private lateinit var etConfirm_password : TextInputEditText
    private lateinit var etAddress : TextInputEditText

    private lateinit var cbMale : CheckBox
    private lateinit var cbFemale : CheckBox
    private lateinit var cbAgree : CheckBox

    private lateinit var btnRegister : Button
    private lateinit var btnLogin : Button

    private lateinit var progressDialog : ProgressDialog

    private lateinit var firebaseAuth : FirebaseAuth
    private var fullname = ""
    private var email = ""
    private var password = ""
    private var confirmPassword = ""
    private var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //assign Elements to variables
        ivLogo = findViewById(R.id.ivLogo)
        tvLogo_name = findViewById(R.id.tvLogo_name)
        tvLogo_intro = findViewById(R.id.tvLogo_intro)

        etFull_name = findViewById(R.id.etFull_name)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirm_password = findViewById(R.id.etConfirm_password)
        etAddress = findViewById(R.id.etAddress)

        cbMale = findViewById(R.id.cbMale)
        cbFemale = findViewById(R.id.cbFemale)
        cbAgree = findViewById(R.id.cbAgree)

        btnRegister = findViewById(R.id.btnRegister)
        btnLogin = findViewById(R.id.btnLogin)

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Logging In...")
        progressDialog.setCanceledOnTouchOutside(false)

        //initialize firebase authentication
        firebaseAuth = FirebaseAuth.getInstance()

        //switching group of check box (Gender)
        cbMale.setOnClickListener {
            if (cbMale.isChecked){
                cbFemale.isChecked = false
            }else{
                cbMale.isChecked = true
            }
        }
        cbFemale.setOnClickListener {
            if (cbFemale.isChecked){
                cbMale.isChecked = false
            }else{
                cbFemale.isChecked = true
            }
        }

        //Register button
        btnRegister.setOnClickListener {
            validateDate()
        }

        //Login Page switch button
        btnLogin.setOnClickListener {
            //goto login page
            gotoLogin()
        }
    }

    private fun validateDate()
    {
        //get data
        fullname = etFull_name.text.toString()
        email = etEmail.text.toString().trim()
        password = etPassword.text.toString().trim()
        confirmPassword = etConfirm_password.text.toString().trim()
        address = etAddress.text.toString()

        var isValidate : Boolean = true

        if (TextUtils.isEmpty(fullname)){
            //Full name isn't enter
            etFull_name.error = "Please enter your full name"
            isValidate = false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            //email address does not meet requirements
            etEmail.error = "Invalid email format"
            isValidate = false
        }
        if (TextUtils.isEmpty(password)){
            //password isn't entered
            etPassword.error = "Please enter password"
            isValidate = false
        }
        if (!password.equals(confirmPassword)){
            //password doesn't match
            etConfirm_password.error = "Passwords doesn't match"
            isValidate = false
        }
        if (TextUtils.isEmpty(address))
        {
            //address isn't enter
            etAddress.error = "Please enter your address"
        }
        if (!(cbMale.isChecked || cbFemale.isChecked)){
            //not tick any of gender checkboxes
            isValidate = false
            cbMale.error = "Please select your gender"
        }
        if (!cbAgree.isChecked)
        {
            //not tick agree checkbox
            cbAgree.error = "You need to agree our terms"
            isValidate = false
        }
        if (isValidate)
        {
            firebaseSignUp()
        }
    }

    private fun firebaseSignUp(){
        //show progress
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //signup success
                progressDialog.dismiss()

                //get current user
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                var gender = ""
                if (cbMale.isChecked){
                    gender = "Male"
                }else if (cbFemale.isChecked){
                    gender = "Female"
                }
                val user = User(
                    firebaseUser.uid,
                    etFull_name.text.toString().trim(),
                    etEmail.text.toString().trim(),
                    etAddress.text.toString().trim(),
                )

                var firestore = FireStore()
                firestore.registerUser(this@RegisterActivity, user)

                userRegisterSuccess()
                //goto user page
                startActivity(Intent(this, UserHomeActivity::class.java))
                finish()
            }
            .addOnFailureListener{ e->
                //signup failed
                progressDialog.dismiss()
                Toast.makeText(this, "SignUp Failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    public fun userRegisterSuccess(){
        Toast.makeText(this, "Account create with email $email", Toast.LENGTH_SHORT).show()
    }
    private fun gotoLogin()
    {
        val gotoRegister = Intent(this@RegisterActivity, LoginActivity::class.java)
        val option = ActivityOptions.makeSceneTransitionAnimation(this,
            Pair.create(ivLogo, "logo_image"),
            Pair.create(tvLogo_name, "Logo_title"),
            Pair.create(tvLogo_intro, "Logo_desc"))
        startActivity(gotoRegister, option.toBundle())
        finish()
    }
}