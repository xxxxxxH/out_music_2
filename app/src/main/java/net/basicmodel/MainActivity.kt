package net.basicmodel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import net.fragment.musicFragment.MusicFragment
import net.fragment.settingFragment.SettingFragment
import net.fragment.videoFragment.VideoFragment
import net.utils.Contanst
import net.utils.TabUtils

class MainActivity : AppCompatActivity() {
    var views:ArrayList<Fragment> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView(){
        views.add(MusicFragment())
        views.add(VideoFragment())
        views.add(SettingFragment())
        TabUtils.get().initViewPager(views,tab,viewpager,this,Contanst.bottomTitle)
    }
}