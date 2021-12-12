package net.fragment.musicFragment

import androidx.recyclerview.widget.LinearLayoutManager
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.layout_fragment_song.*
import net.adapter.SongAdapter
import net.basicmodel.R
import net.entity.SongEntity
import net.event.MessageEvent
import net.fragment.BaseFragment
import net.utils.CommonUtils
import net.utils.DataManager
import net.utils.MMKVUtils
import org.greenrobot.eventbus.EventBus

class SongsFragment(var data: ArrayList<SongEntity>?) : BaseFragment() {
    private var songAdapter: SongAdapter? = null
    override fun getLayout(): Int {
        return R.layout.layout_fragment_song
    }

    override fun initView() {
        if (data!!.isEmpty())
            data = DataManager.get().getAllSongs(requireContext())
        songAdapter = SongAdapter(data!!, requireActivity(), false)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = songAdapter
        songAdapter!!.addChildClickViewIds(R.id.more)
        songAdapter!!.setOnItemClickListener { adapter, view, position ->
            val entity = data!![position]
            EventBus.getDefault().post(MessageEvent("itemClick", entity))
            val data = MMKVUtils.get().getPlayList("history")
            if (!MMKVUtils.get().isExist(data, entity)) {
                val key = System.currentTimeMillis().toString()
                MMKVUtils.get().saveKeys("history", key)
                MMKV.defaultMMKV()!!.encode(key, entity)
            }
        }
        songAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            CommonUtils.get().showPopUp(view, requireContext(), requireActivity(), data!![position])
        }
    }
}