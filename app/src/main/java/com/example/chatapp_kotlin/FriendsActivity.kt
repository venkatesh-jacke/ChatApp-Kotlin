package com.example.chatapp_kotlin

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp_kotlin.databinding.ActivityFriendsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendsBinding
    lateinit var recyclerView: RecyclerView
    lateinit var users: List<User>
    lateinit var userAdapter: UserAdapter
    lateinit var progressBar: ProgressBar
    lateinit var onItemClickListener: UserAdapter.OnItemClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        progressBar = binding.progressBar
        recyclerView = binding.recyclerView
        users = mutableListOf()

        onItemClickListener = object : UserAdapter.OnItemClickListener {

            override fun onItemClick(position: Int) {
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
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    users += data.getValue(User::class.java)!!
                    userAdapter = UserAdapter(users, this@FriendsActivity, onItemClickListener)
                    recyclerView.layoutManager = LinearLayoutManager(this@FriendsActivity)
                    recyclerView.adapter = userAdapter
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.profile) {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }


}