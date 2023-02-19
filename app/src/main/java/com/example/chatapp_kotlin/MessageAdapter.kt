package com.example.chatapp_kotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(
    var messages: ArrayList<Message>,
    var senderImg: String,
    var recieverImg: String,
    var context: Context
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        var view= LayoutInflater.from(context).inflate(R.layout.message_holder,parent,false)

        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
       holder.tvMessage.text=messages[position].context
        var cl=holder.cl
        if(messages[position].sender== FirebaseAuth.getInstance().currentUser?.email){

            Glide.with(context)
                .load(senderImg)
                .error(R.drawable.account_person)
                .placeholder(R.drawable.account_person)
                .into(holder.ivProfile)
            var constraintSet=ConstraintSet()
            constraintSet.clone(cl)
            constraintSet.clear(R.id.cvProfile,ConstraintSet.LEFT)
            constraintSet.clear(R.id.tvMessage,ConstraintSet.LEFT)
            constraintSet.connect(R.id.cvProfile,ConstraintSet.RIGHT,R.id.ccLayout,ConstraintSet.RIGHT,0)
            constraintSet.connect(R.id.tvMessage,ConstraintSet.RIGHT,R.id.cvProfile,ConstraintSet.LEFT,0)
            constraintSet.applyTo(cl)

        }
        else{

            Glide.with(context)
                .load(recieverImg)
                .error(R.drawable.account_person)
                .placeholder(R.drawable.account_person)
                .into(holder.ivProfile)
            var constraintSet=ConstraintSet()
            constraintSet.clone(cl)
            constraintSet.clear(R.id.cvProfile,ConstraintSet.RIGHT)
            constraintSet.clear(R.id.tvMessage,ConstraintSet.RIGHT)
            constraintSet.connect(R.id.cvProfile,ConstraintSet.LEFT,R.id.ccLayout,ConstraintSet.LEFT,0)
            constraintSet.connect(R.id.tvMessage,ConstraintSet.LEFT,R.id.cvProfile,ConstraintSet.RIGHT,0)
            constraintSet.applyTo(cl)
        }
    }


    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cl: ConstraintLayout = itemView.findViewById(R.id.ccLayout)
        var tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        var ivProfile: ImageView = itemView.findViewById(R.id.ivSmallProfile)

    }
}