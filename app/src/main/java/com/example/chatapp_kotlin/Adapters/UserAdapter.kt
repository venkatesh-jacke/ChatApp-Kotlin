package com.example.chatapp_kotlin.Adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp_kotlin.R
import com.example.chatapp_kotlin.DataClass.User
import com.example.chatapp_kotlin.Utils.FirebaseUtils

class UserAdapter(var users:List<User>, var context:Context, var onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = View.inflate(context, R.layout.user_item, null)
        return UserViewHolder(view,onItemClickListener)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.userName.text = users[position].userName
        FirebaseUtils.loadChatImage(context,users[position].profile_image!!,holder.profile_image)
    }


    class UserViewHolder(itemView: View,onItemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        var userName = itemView.findViewById<TextView>(R.id.tvUserName)
        var profile_image = itemView.findViewById<ImageView>(R.id.profile_image)


        init {
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(adapterPosition)
            }


        }

    }
}