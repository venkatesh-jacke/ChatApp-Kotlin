package com.example.chatapp_kotlin.Activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp_kotlin.Adapters.MessageAdapter
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
    lateinit var binding: ActivityMessageBinding
    lateinit var recyclerView: RecyclerView
    lateinit var etMessage: EditText
    lateinit var ivSend: ImageView
    lateinit var progressMessage: ProgressBar
    lateinit var tvChatName: TextView
    lateinit var imgChat: ImageView
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

        roommate_userName = intent.getStringExtra("roommate_userName").toString()
        roommate_email = intent.getStringExtra("roommate_email").toString()

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        recyclerView = binding.recyclerViewMessage
        etMessage = binding.etMessage
        ivSend = binding.ivSend
        imgChat = binding.imageToolBar
        progressMessage = binding.progressMessage
        tvChatName = binding.tvChatWith

        tvChatName.text = roommate_userName
        messages = ArrayList()

        messageAdapter = MessageAdapter(
            messages,
            intent.extras?.getString("roommate_img").toString(),
            intent.extras?.getString("my_img").toString(),
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
            .load(intent.extras?.getString("roommate_img").toString())
            .error(R.drawable.account_person)
            .placeholder(R.drawable.account_person)
            .into(imgChat)
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
                    } else if (roommate_userName.compareTo(myName!!) == 0) {
                        chatRoomId = roommate_userName + myName
                    } else {
                        chatRoomId = myName + roommate_userName
                    }
                    attachMessageListener(chatRoomId)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
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
                    TODO("Not yet implemented")
                }
            })
    }
}