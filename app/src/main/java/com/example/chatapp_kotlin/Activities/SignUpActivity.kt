package com.example.chatapp_kotlin.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatapp_kotlin.DataClass.User
import com.example.chatapp_kotlin.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySignUpBinding
    lateinit var etUserName: EditText
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var btnSubmit: Button
    lateinit var tvLogin: TextView
    private var isSigningUp = true

    //Firebase
    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabase: FirebaseDatabase

    companion object {
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE =
            123  // Request code for READ_EXTERNAL_STORAGE. It can be any number > 0.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        etUserName = binding.etUserName
        etEmail = binding.etEmail
        etPassword = binding.etPassword
        btnSubmit = binding.btnSubmit
        tvLogin = binding.tvLogin
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        val currentUser = mAuth.currentUser
        // If user is already logged in, redirect to FriendsActivity
        if (currentUser != null) {
            startActivity(Intent(this, FriendsActivity::class.java))
            finish()
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )  // Check if the READ_EXTERNAL_STORAGE permission is already available.
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        }


        tvLogin.setOnClickListener {
            if (isSigningUp) {
                isSigningUp = false
                btnSubmit.text = "Log In"
                tvLogin.text = "Dont have an account? Sign Up"
                etUserName.visibility = View.GONE
            } else {
                isSigningUp = true
                etUserName.visibility = View.VISIBLE
                btnSubmit.text = "Sign Up"
                tvLogin.text = "Already have an account? Login"
            }
        }
        btnSubmit.setOnClickListener {
            if (isSigningUp) {
                if (isValid(
                        etUserName.text.toString(),
                        etEmail.text.toString(),
                        etPassword.text.toString()
                    )
                ) {
                    signUp()
                }
            } else {
                if (isValid(null, etEmail.text.toString(), etPassword.text.toString())) {
                    logIn()
                }
            }
        }

    }


    // Function to check if the entered details are valid
    fun isValid(username: String?, email: String, password: String): Boolean {
        var isValid = true

//        if (username != null) {
//            if (!username.matches(Regex("^[a-zA-Z0-9]+$"))) {
//                etUserName.error = "Username can only contain letters and numbers"
//                isValid = false
//            }
//        }
        if (username == null) {
            etUserName.error = "UserName cannot be empty"
            isValid = false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Invalid email address"
            isValid = false
        }

//        if (!password.matches(Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"))) {
//            etPassword.error = "Password must contain at least 8 characters including 1 uppercase letter, 1 lowercase letter, 1 number and 1 special character"
//            isValid = false
//        }
        if (password.length < 5) {
            etPassword.error = "Password must be at least 6 characters"
            isValid = false
        }

        return isValid
    }

    // Function to create a new user
    private fun signUp() {

        mAuth
            .createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    // Add user to database
                    mDatabase.getReference("users/" + mAuth.uid).setValue(
                        User(etUserName.text.toString(), etEmail.text.toString(), "")
                    )

                    // Redirect to FriendsActivity
                    Intent(this, FriendsActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                    Toast.makeText(this, "User Created", Toast.LENGTH_SHORT).show()
                    Log.d("SignUpActivity", "User Created")

                } else {
                    Toast.makeText(this, "User Creation Failed", Toast.LENGTH_SHORT).show()
                    Log.d(
                        "SignUpActivity",
                        "Creation Failed due to: " + it.exception?.message.toString()
                    )
                }
            }
    }

    // Function to log in an existing user
    private fun logIn() {

        mAuth.signInWithEmailAndPassword(
            binding.etEmail.text.toString(),
            binding.etPassword.text.toString()
        )
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    // Redirect to FriendsActivity
                    Intent(this, FriendsActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                    Toast.makeText(this, "SignIn Successful", Toast.LENGTH_SHORT).show()
                    Log.d("SignUpActivity", "SignIn Successful")

                } else {
                    Toast.makeText(this, "SignIn Failed", Toast.LENGTH_SHORT).show()
                    Log.d(
                        "SignUpActivity",
                        "SignIn Failed due to: " + it.exception?.message.toString()
                    )
                }
            }
    }

}