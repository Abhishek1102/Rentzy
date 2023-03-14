package com.example.rentzy.activity

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import butterknife.BindView
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.rentzy.R
import com.example.rentzy.adapter.DashboardAdapter
import com.example.rentzy.fragment.*
import com.example.rentzy.helper.Utils

class MainActivity : AppCompatActivity() {

    @BindView(R.id.frame_main)
    lateinit var frame_authentication: FrameLayout

    //meow
    var meo: MeowBottomNavigation? = null
    private val ID_HOME = 1
    private val ID_FAVOURITES = 2
    private val ID_ADD = 3
    private val ID_MORE = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Utils.blackIconStatusBar(this@MainActivity, R.color.white)

        meo = findViewById<View>(R.id.bottom_nav) as MeowBottomNavigation
        meo!!.add(MeowBottomNavigation.Model(1, R.drawable.home_icon))
        meo!!.add(MeowBottomNavigation.Model(2, R.drawable.heart_icon))
        meo!!.add(MeowBottomNavigation.Model(3, R.drawable.add))
        meo!!.add(MeowBottomNavigation.Model(4, R.drawable.more))

        addfragment(DashboardFragment(), "DashboardFragment")

        meo!!.setOnClickMenuListener {
            //                Toast.makeText(getApplicationContext(), "Clicked item " + item.getId(), Toast.LENGTH_SHORT).show();
        }

        meo!!.setOnShowListener {
            var select_fragment: Fragment? = null;
            if (it.id == ID_HOME) {
                addfragment(DashboardFragment(), "DashboardFragment")
            } else if (it.id == ID_FAVOURITES) {
                addfragment(FavouritesFragment(), "FavouritesFragment")
            } else if (it.id == ID_ADD) {
                addfragment(AddFragment(), "AddFragment")
            } else if (it.id == ID_MORE) {
                addfragment(MoreFragment(), "MoreFragment")
            }
        }

        if (!meo!!.isShowing(ID_HOME)) {
            meo!!.show(ID_HOME, true)
            meo!!.setOnReselectListener { }
        }

    }

    private fun addfragment(fragment: Fragment, tag: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_main, fragment, tag)
        fragmentTransaction.commit()
    }

    private fun addFullfragment(fragment: Fragment, tag: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_full, fragment, tag)
        fragmentTransaction.commit()
    }


}


