package com.example.seascape.sLoginRegister

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.seascape.sUser.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStore
{
    private val sFireStore = FirebaseFirestore.getInstance()

    public fun registerUser(activity: RegisterActivity, userInfo : User){
        sFireStore.collection("users")
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener { activity.userRegisterSuccess() }
            .addOnFailureListener { e-> Log.e(activity.javaClass.simpleName, "Error while registering the user.", e) }
    }

    public fun getUserDetails(activity: Activity){
        sFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
                Log.i(activity.javaClass.simpleName, document.toString())
                val user = document.toObject(User::class.java)!!
                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.SEASCAPE_PREFERENCES,
                        Context.MODE_PRIVATE
                    )
                val editor : SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.fullname}"
                )
                editor.apply()
                when(activity){
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                }
            }
    }

    public fun getCurrentUserID() : String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
}