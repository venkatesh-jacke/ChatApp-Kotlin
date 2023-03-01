package com.example.chatapp_kotlin.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import com.example.chatapp_kotlin.R
import com.example.chatapp_kotlin.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val SPLASH_TIMER = 3000L
    private val mAuth = FirebaseAuth.getInstance()
    private val mDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()


     Handler().postDelayed({
         if(mAuth.currentUser!=null){
             startActivity(Intent(this@SplashActivity,FriendsActivity::class.java))
             finish()
         }else{
             startActivity(Intent(this@SplashActivity,SignUpActivity::class.java))
             finish()
         }
     },SPLASH_TIMER)

    }
}