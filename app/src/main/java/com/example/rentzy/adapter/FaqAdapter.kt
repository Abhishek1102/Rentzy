package com.example.rentzy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rentzy.R
import com.example.rentzy.model.FaqModel

class FaqAdapter(var context: Context?, var list: MutableList<FaqModel>) :
    RecyclerView.Adapter<FaqAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        var view = layoutInflater.inflate(R.layout.row_faq, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var faqModel: FaqModel = list.get(position)
        holder.tv_question.setText(faqModel.question)
        holder.tv_answer.setText(faqModel.answer)

        holder.tv_question.setOnClickListener {
            if (holder.tv_answer.visibility == View.VISIBLE) {
                holder.tv_answer.visibility = View.GONE
                holder.iv_arrowdown.visibility = View.GONE
                holder.iv_arrowup.visibility = View.VISIBLE
            } else {
                holder.tv_answer.visibility = View.VISIBLE
                holder.iv_arrowdown.visibility = View.VISIBLE
                holder.iv_arrowup.visibility = View.GONE
            }
        }

    }


    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_question: TextView = itemView.findViewById(R.id.tv_question)
        var tv_answer: TextView = itemView.findViewById(R.id.tv_answer)
        var iv_arrowup: ImageView = itemView.findViewById(R.id.iv_arrowup)
        var iv_arrowdown: ImageView = itemView.findViewById(R.id.iv_arrowdown)
    }
}