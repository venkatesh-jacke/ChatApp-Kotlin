package com.example.chatapp_kotlin.Utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.chatapp_kotlin.DataClass.User
import com.example.chatapp_kotlin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FirebaseUtils {

    private val mAuth = FirebaseAuth.getInstance()
    private val ref = FirebaseDatabase.getInstance().getReference("/users/${mAuth.uid}")


    fun loadUserData(context: Context, profileImage: ImageView) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    if (user.profile_image != "") {
                        Glide.with(context)
                            .load(user.profile_image)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(profileImage)

                        Log.d("TAG", "onDataChange: ${user.profile_image}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                Log.e("TAG", "onCancelled: ", error.toException())
            }
        })

    }

    infix fun updateProfilePhoto(uri: Uri) {
        ref.child("profile_image").setValue(uri)

    }

    infix fun updateProfilePhoto(uri: String) {
        ref.child("profile_image").setValue(uri)

    }

    fun getUserName(): String {

      var name=""
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    name=user.userName!!
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Log.e("TAG", "onCancelled: ", error.toException())
            }
        })
        return name

    }

    fun updateProfileName(name: String) {
        ref.child("userName").setValue(name)
    }

    companion object {
        fun loadChatImage(context: Context, uri: String, chatProfile: ImageView?) {
            Glide.with(context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.account_person)
                .placeholder(R.drawable.account_person)
                .into(chatProfile!!)

        }

    }


}