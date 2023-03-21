package com.example.rentzy.adapter

import android.R
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mygreetingsapp.helper.AppConstant
import com.example.rentzy.fragment.HouseProfileFragment
import com.example.rentzy.helper.SecurePreferences
import com.example.rentzy.model.DashboardModel
import android.os.Handler


class DashboardAdapter(
    var context: Context?,
    var list: MutableList<DashboardModel>,
    private val onClicked: (DashboardModel) -> Unit
) :
    RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    fun setFilteredList(list: List<DashboardModel>) {
        this.list = list as MutableList<DashboardModel>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        var view = layoutInflater.inflate(com.example.rentzy.R.layout.row_dashboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dashboardModel: DashboardModel = list.get(position)
        Glide.with(context!!).load(list.get(position).house_image).into(holder.iv_listhouse)
        holder.tv_houseName.setText(list.get(position).house_name)
        holder.tv_houseaddress.setText(list.get(position).house_address)
        holder.tv_houseRent.setText("\u20b9" + list.get(position).house_rent)

        holder.iv_heart.setOnClickListener {
            holder.iv_heart.visibility = View.GONE
            holder.iv_noheart.visibility = View.VISIBLE
        }

        holder.iv_noheart.setOnClickListener {
            holder.iv_heart.visibility = View.VISIBLE
            holder.iv_noheart.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
//            val houseProfileFragment = HouseProfileFragment(list.get(position))
//            openfragment(houseProfileFragment, "HouseProfileFragment")
            AppConstant.showProgressDialog(context)
            Handler().postDelayed({

                //passing value in form of model to method in dashboardfragment
                onClicked(dashboardModel)

            }, 2000)


        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_listhouse: ImageView =
            itemView.findViewById(com.example.rentzy.R.id.iv_listHouseImage)
        var tv_houseRent: TextView = itemView.findViewById(com.example.rentzy.R.id.tv_houseRent)
        var tv_houseName: TextView = itemView.findViewById(com.example.rentzy.R.id.tv_houseName)
        var tv_houseaddress: TextView =
            itemView.findViewById(com.example.rentzy.R.id.tv_houseAddress)
        var iv_heart: ImageView = itemView.findViewById(com.example.rentzy.R.id.iv_listheart)
        var iv_noheart: ImageView = itemView.findViewById(com.example.rentzy.R.id.iv_listnoHeart)
    }

}