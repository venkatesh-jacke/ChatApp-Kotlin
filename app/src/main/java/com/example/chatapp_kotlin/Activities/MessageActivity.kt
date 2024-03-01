package com.example.chatapp_kotlin.Activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp_kotlin.Adapters.MessageAdapter
import com.example.chatapp_kotlin.Constants
import com.example.chatapp_kotlin.DataClass.Message
import com.example.chatapp_kotlin.R
import com.example.chatapp_kotlin.DataClass.User
import com.example.chatapp_kotlin.databinding.ActivityMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessageActivity : AppCompatActivity() {
    private var TAG =MessageActivity::class.java.simpleName
    lateinit var binding: ActivityMessageBinding
    private lateinit var toolBar: Toolbar
    private lateinit var chat_profile: ImageView
    private lateinit var chat_name:TextView
    lateinit var recyclerView: RecyclerView
    lateinit var etMessage: EditText
    lateinit var ivSend: ImageView
    lateinit var progressMessage: ProgressBar
    lateinit var messages: ArrayList<Message>
    lateinit var messageAdapter: MessageAdapter

    lateinit var roommate_userName: String
    lateinit var roommate_email: String
    lateinit var chatRoomId: String

    //Firebase
    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        roommate_userName = intent.getStringExtra(Constants.EXTRA_ROOMMATE_USERNAME).toString()
        roommate_email = intent.getStringExtra(Constants.EXTRA_ROOMMATE_EMAIL).toString()

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()


        toolBar = findViewById(R.id.toolbar)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        recyclerView = binding.recyclerViewMessage
        etMessage = binding.etMessage
        ivSend = binding.ivSend
        chat_profile=toolBar.findViewById(R.id.chat_profile)
        chat_name=toolBar.findViewById(R.id.chat_name)


        progressMessage = binding.progressMessage


        messages = ArrayList()

        messageAdapter = MessageAdapter(
            messages,
            intent.extras?.getString(Constants.EXTRA_ROOMMATE_IMG).toString(),
            intent.extras?.getString(Constants.EXTRA_MY_IMG).toString(),
            this@MessageActivity
        )


        ivSend.setOnClickListener {
            mDatabase.getReference("messages/" + chatRoomId).push().setValue(
                Message(
                    mAuth.currentUser?.email.toString(),
                    roommate_email,
                    etMessage.text.toString()
                )
            )
            etMessage.text.clear()
        }

        recyclerView.layoutManager = LinearLayoutManager(this@MessageActivity)
        recyclerView.adapter = messageAdapter


        Glide.with(this@MessageActivity)
            .load(intent.extras?.getString(Constants.EXTRA_ROOMMATE_IMG).toString())
            .error(R.drawable.account_person)
            .placeholder(R.drawable.account_person)
            .into(chat_profile)

        chat_name.text=intent.extras?.getString(Constants.EXTRA_ROOMMATE_USERNAME).toString()
        setupChatRoom()

    }

    fun setupChatRoom() {
        mDatabase.getReference("users/" + mAuth.uid)
            .addListenerForSingleValueEvent(object :
                ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    var myName = snapshot.getValue(User::class.java)?.userName
                    if (roommate_userName.compareTo(myName!!) > 0) {
                        chatRoomId = roommate_userName + myName
                    } else if (roommate_userName.compareTo(myName) == 0) {
                        chatRoomId = roommate_userName + myName
                    } else {
                        chatRoomId = myName + roommate_userName
                    }
                    attachMessageListener(chatRoomId)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG,error.message)
                }

            })
    }


    fun attachMessageListener(chatRoomId: String) {
        mDatabase.getReference("messages/" + this.chatRoomId)
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages.clear()
                    for (datasnapShot in snapshot.children) {
                        messages.add(datasnapShot.getValue(Message::class.java)!!)
                    }

                    messageAdapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(messages.size - 1)
                    progressMessage.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG,error.message)
                }
            })
    }
}