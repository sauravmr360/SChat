package com.example.schat.fragments

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.schat.R
import com.example.schat.modules.User
import com.example.schat.utils.ChatActivity
import com.example.schat.utils.IMAGE
import com.example.schat.utils.NAME
import com.example.schat.utils.UID
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*

class UsersViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){


    fun bind(user:User,context: Context) = with(itemView){

        timeTv.visibility = View.GONE
        countTv.visibility = View.GONE




        titleTv.text = user.name
        subTitleTv.text = user.status

        Picasso.get().load(user.thumbimage).placeholder(R.drawable.defaultavatar).
        error(R.drawable.defaultavatar).into(userImgView)


       setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(UID,user.uid)
            intent.putExtra(NAME,user.name)
            intent.putExtra(IMAGE,user.thumbimage)

            context.startActivity(intent)
        }
    }

}
class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)
