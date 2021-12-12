package net.fragment

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.layout_fragment_playlist.*
import net.adapter.SongAdapter
import net.basicmodel.R
import net.event.MessageEvent
import net.utils.MMKVUtils
import org.greenrobot.eventbus.EventBus

class HistoryFragment:BaseFragment() {
    override fun getLayout(): Int {
        return R.layout.layout_fragment_playlist
    }

    override fun initView() {
        toolbar.title = "History"
        val data = MMKVUtils.get().getPlayList("history")
        val songAdapter = SongAdapter(data,requireActivity(),true)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = songAdapter
        songAdapter.setOnItemClickListener { adapter, view, position ->
            val entity = data[position]
            EventBus.getDefault().post(MessageEvent("itemClick", entity))
            (activity as AppCompatActivity).supportFragmentManager.popBackStack()
        }
    }
}