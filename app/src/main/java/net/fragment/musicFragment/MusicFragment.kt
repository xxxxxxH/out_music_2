package net.fragment.musicFragment

import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.layout_fragment_music.*
import net.adapter.ViewPagerAdapter
import net.basicmodel.R
import net.fragment.BaseFragment
import net.utils.Contanst
import net.utils.TabUtils

class MusicFragment : BaseFragment() {
    var adapter: ViewPagerAdapter? = null
    override fun getLayout(): Int {
        return R.layout.layout_fragment_music
    }

    override fun initView() {
        adapter = ViewPagerAdapter(childFragmentManager)
        adapter?.let {
            it.addFrag(SongsFragment(),"song")
            it.addFrag(ArtistFragment(),"artist")
            it.addFrag(AlbumFragment(),"album")
            it.addFrag(GenresFragment(),"genre")
            viewpager.adapter = it
            tablayout.setupWithViewPager(viewpager)
        }
    }
}