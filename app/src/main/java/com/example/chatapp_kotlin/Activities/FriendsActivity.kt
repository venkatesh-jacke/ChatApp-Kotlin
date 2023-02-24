package com.example.chatapp_kotlin.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.chatapp_kotlin.Adapters.UserAdapter
import com.example.chatapp_kotlin.R
import com.example.chatapp_kotlin.DataClass.User
import com.example.chatapp_kotlin.databinding.ActivityFriendsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendsBinding
    lateinit var recyclerView: RecyclerView
    lateinit var users: ArrayList<User>
    lateinit var userAdapter: UserAdapter
    lateinit var progressBar: ProgressBar
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var onItemClickListener: UserAdapter.OnItemClickListener
    lateinit var myImageUri: String


    private lateinit var toolBar: Toolbar
    private var menuItem: Menu? = null

    lateinit var myPic: ImageView


    //Firebase
    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        toolBar = findViewById(R.id.friendBar)
        myPic = findViewById(R.id.myPic)
        toolBar.title = "Friends"

        setSupportActionBar(toolBar)

        //Firebase
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        progressBar = binding.progressBar
        recyclerView = binding.recyclerView
        swipeRefreshLayout = binding.swipeLayout


        users = ArrayList()
        swipeRefreshLayout.setOnRefreshListener {
            getUsers()
            swipeRefreshLayout.isRefreshing = false
        }
        myPic.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        onItemClickListener = object : UserAdapter.OnItemClickListener {

            override fun onItemClick(position: Int) {

                Intent(this@FriendsActivity, MessageActivity::class.java).apply {
                    putExtra("roommate_userName", users[position].userName)
                    putExtra("roommate_email", users[position].email)
                    putExtra("roommate_img", users[position].profile_image)
                    putExtra("my_img", myImageUri)

                    startActivity(this)
                }
                Toast.makeText(
                    this@FriendsActivity,
                    "tapped on ${users[position].userName}",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
        getUsers()

    }

    private fun getUsers() {
        users.clear()
        mDatabase.getReference("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    users += data.getValue(User::class.java)!!
                    userAdapter = UserAdapter(users, this@FriendsActivity, onItemClickListener)
                    recyclerView.layoutManager = LinearLayoutManager(this@FriendsActivity)
                    recyclerView.adapter = userAdapter
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
                for (user in users) {
                    if (user.email == mAuth.currentUser?.email) {
                        myImageUri = user.profile_image!!
                        return
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val uid = mAuth.uid
        val ref = mDatabase.getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    if (user.profile_image != "") {
                        Glide.with(this@FriendsActivity).load(user.profile_image).into(myPic)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FriendsActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }


}