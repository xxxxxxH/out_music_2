package net.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.SlidingTabLayout

class TabUtils {
    companion object {
        private var i: TabUtils? = null
            get() {
                field ?: run {
                    field = TabUtils()
                }
                return field
            }

        @Synchronized
        fun get(): TabUtils {
            return i!!
        }
    }

    fun initViewPager(
        views: ArrayList<Fragment>,
        tab: SlidingTabLayout,
        viewpager: ViewPager,
        activity: FragmentActivity,
        title: Array<String>
    ) {
        tab.setViewPager(viewpager, title, activity, views)
    }
}