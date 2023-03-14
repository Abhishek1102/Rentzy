package com.example.rentzy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentzy.R
import com.example.rentzy.model.Slider_Model

class ImagesSliderAdapter(var context: Context?, var list: MutableList<Slider_Model>) :
    RecyclerView.Adapter<ImagesSliderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        var view = layoutInflater.inflate(R.layout.row_slider, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sliderModel:Slider_Model = list.get(position)
        Glide.with(context!!).load(list.get(position).image_link).into(holder.iv_slider)

        holder.itemView.setOnClickListener {
            Toast.makeText(context,"You clicked "+sliderModel.image_id,Toast.LENGTH_LONG).show()
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_slider: ImageView = itemView.findViewById(R.id.iv_slider)
    }
}