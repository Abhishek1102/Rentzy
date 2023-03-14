package com.example.rentzy.activity

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import com.example.rentzy.R
import com.example.rentzy.fragment.Tour_First
import com.example.rentzy.fragment.Tour_Second
import com.example.rentzy.fragment.Tour_Third
import com.example.rentzy.helper.Utils

class AppTourActivity : AppCompatActivity() {

    @BindView(R.id.frame_tour)
    lateinit var frame_tour: FrameLayout

    lateinit var viewPager_tour: ViewPager

    private var viewPagerAdapter: ViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_tour)

        Utils.blackIconStatusBar(this, R.color.white)
        viewPager_tour = findViewById(R.id.viewPager_tour)

        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter!!.add(Tour_First())
        viewPagerAdapter!!.add(Tour_Second())
        viewPagerAdapter!!.add(Tour_Third())

        viewPager_tour.adapter = viewPagerAdapter

    }

    internal class ViewPagerAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm) {
        private val fragments: MutableList<Fragment> = ArrayList()
        fun add(fragment: Fragment) {
            fragments.add(fragment)
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }
    }
}