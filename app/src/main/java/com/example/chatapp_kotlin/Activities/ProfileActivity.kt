package com.example.chatapp_kotlin.Activities



import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.chatapp_kotlin.DataClass.User
import com.example.chatapp_kotlin.Utils.FirebaseUtils
import com.example.chatapp_kotlin.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class ProfileActivity : AppCompatActivity() {
    private var TAG = FriendsActivity::class.java.simpleName
    private lateinit var binding: ActivityProfileBinding
    lateinit var btnLogOut: Button
    lateinit var btnUpload: Button
    lateinit var profileImage: ImageView
    private lateinit var selectedImageUri: Uri
    private lateinit var etName:EditText
    private lateinit var btnSaveName:Button

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
        etName=binding.etName
        btnSaveName=binding.btnSaveName
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
            if(!::selectedImageUri.isInitialized)
            {
                return@setOnClickListener
            }

            upLoadPhoto()
        }

        profileImage.setOnClickListener {
            Intent(Intent.ACTION_PICK).also {
                it.type = "image/*"
                startActivityForResult(it, 1)
            }
        }
        etName.setText(FirebaseUtils().getUserName())

        btnSaveName.setOnClickListener {
            FirebaseUtils().updateProfileName(etName.text.toString())
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
                           // updateProfilePhoto(it.result.toString())
                            FirebaseUtils().updateProfilePhoto(it.result.toString())
                        }
                    }
                    Toast.makeText(this, "Profile Uploaded", Toast.LENGTH_SHORT).show()
                    Log.d(
                        TAG,
                        "Profile Uploaded" + it.result?.metadata?.reference?.downloadUrl
                    )
                } else {
                    Toast.makeText(this, "Profile Upload Failed", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Profile Upload Failed" + it.exception?.message)
                }
                progressDialog.dismiss()
            }
            .addOnProgressListener {
                var progress = 100.0*it.bytesTransferred/it.totalByteCount
                progressDialog.setMessage("Uploaded ${progress.toInt()}%")
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data!!
            FirebaseUtils().updateProfilePhoto(selectedImageUri.toString())
            profileImage.setImageURI(selectedImageUri)
        }
    }

    override fun onResume() {
        super.onResume()
        FirebaseUtils().loadUserData(this,profileImage)
    }
}