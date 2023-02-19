package com.example.chatapp_kotlin.Activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.MultiAutoCompleteTextView
import android.widget.Toast
import com.example.chatapp_kotlin.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    lateinit var btnLogOut: Button
    lateinit var btnUpload: Button
    lateinit var profileImage: ImageView
    private lateinit var selectedImageUri: Uri

    //Firebase
    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        btnLogOut = binding.btnLogOut
        profileImage = binding.profileImage
        btnUpload = binding.btnUpload
        mAuth=FirebaseAuth.getInstance()
        mDatabase= FirebaseDatabase.getInstance()
        btnLogOut.setOnClickListener {
            mAuth.signOut()
            Intent(this, SignUpActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                finish()
            }

        }

        btnUpload.setOnClickListener {
            upLoadPhoto()
        }

        profileImage.setOnClickListener {
            Intent(Intent.ACTION_PICK).also {
                it.type = "image/*"
                startActivityForResult(it, 1)
            }
        }

    }

    private fun upLoadPhoto() {
        val progressDialog = android.app.ProgressDialog(this)
        progressDialog.setTitle("Uploading...")
        progressDialog.show()
        FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString())
            .putFile(selectedImageUri).addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.storage.downloadUrl.addOnCompleteListener{
                        if(it.isSuccessful){
                            updateProfilePhoto(it.result.toString())
                        }
                    }
                    Toast.makeText(this, "Profile Uploaded", Toast.LENGTH_SHORT).show()
                    Log.d(
                        "ProfileActivity",
                        "Profile Uploaded" + it.result?.metadata?.reference?.downloadUrl
                    )
                } else {
                    Toast.makeText(this, "Profile Upload Failed", Toast.LENGTH_SHORT).show()
                    Log.d("ProfileActivity", "Profile Upload Failed" + it.exception?.message)
                }
                progressDialog.dismiss()
            }
            .addOnProgressListener {
                var progress = 100.0*it.bytesTransferred/it.totalByteCount
                progressDialog.setMessage("Uploaded ${progress.toInt()}%")
            }
    }

    private fun updateProfilePhoto(url: String) {
        val uid= mAuth.uid
        val ref= mDatabase.getReference("/users/$uid")
        ref.child("profile_image").setValue(url)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data!!
            profileImage.setImageURI(selectedImageUri)
        }
    }
}