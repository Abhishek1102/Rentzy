package com.example.rentzy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentzy.R
import com.example.rentzy.model.NotificationModel
import org.w3c.dom.Text

class NotificationAdapter(var context: Context?, var list: MutableList<NotificationModel>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        var view = layoutInflater.inflate(R.layout.row_notification,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var notificationModel:NotificationModel = list.get(position)
        Glide.with(context!!).load(list.get(position).notification_icon).into(holder.iv_notificationicon)
        holder.tv_notificationtitle.setText(notificationModel.notification_name)
        holder.tv_notificationdescription.setText(notificationModel.notification_description)
        holder.tv_notificationtime.setText(notificationModel.notification_time)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var iv_notificationicon:ImageView = itemView.findViewById(R.id.iv_notificationicon)
        var tv_notificationtitle:TextView = itemView.findViewById(R.id.tv_notificationtitle)
        var tv_notificationdescription:TextView = itemView.findViewById(R.id.tv_notificationdescription)
        var tv_notificationtime:TextView = itemView.findViewById(R.id.tv_notificationtime)
    }
}