package net.fragment.musicFragment

import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.layout_fragment_song.*
import net.adapter.SongAdapter
import net.basicmodel.R
import net.event.MessageEvent
import net.fragment.BaseFragment
import net.utils.SongManager
import org.greenrobot.eventbus.EventBus

class SongsFragment : BaseFragment() {
    private var songAdapter:SongAdapter?=null
    override fun getLayout(): Int {
        return R.layout.layout_fragment_song
    }

    override fun initView() {
        val data = SongManager.get().getAllSongs(requireContext())
        songAdapter = SongAdapter(data,requireActivity())
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = songAdapter
        songAdapter!!.setOnItemClickListener { adapter, view, position ->
            EventBus.getDefault().post(MessageEvent("itemClick",data[position]))
        }
    }
}