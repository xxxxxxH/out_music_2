package net.fragment

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.layout_fragment_details.*
import net.adapter.SongAdapter
import net.basicmodel.R
import net.entity.SongEntity
import net.event.MessageEvent
import net.utils.DataManager
import net.utils.MMKVUtils
import org.greenrobot.eventbus.EventBus

class DetailsFragment(val id: Long, val title: String, val imgCover: Uri, val type: String) :
    BaseFragment() {

    var data: ArrayList<SongEntity>? = null
    override fun getLayout(): Int {
        return R.layout.layout_fragment_details
    }

    override fun initView() {
        collapsing_toolbar.title = title
        Glide.with(requireContext()).load(imgCover).into(cover)
        when (type) {
            "ArtistFragment" -> {
                data = DataManager.get().getSongsByArtist(requireContext(), id)
            }
            "AlbumFragment" -> {
                data = DataManager.get().getSongsByAlbum(requireContext(), id)
            }
            "GenresFragment" -> {
                data = DataManager.get().getSongsByGenres(requireContext(), id, title)
            }
        }
        val songAdapter = SongAdapter(data!!, requireActivity(), true)
        detailRecycler.layoutManager = LinearLayoutManager(activity)
        detailRecycler.adapter = songAdapter
        songAdapter.setOnItemClickListener { adapter, view, position ->
            val entity = data!![position]
            EventBus.getDefault().post(MessageEvent("itemClick",entity))
            val data = MMKVUtils.get().getPlayList("history")
            if (!MMKVUtils.get().isExist(data, entity)) {
                val key = System.currentTimeMillis().toString()
                MMKVUtils.get().saveKeys("history", key)
                MMKV.defaultMMKV()!!.encode(key, entity)
            }
            (activity as AppCompatActivity).supportFragmentManager.popBackStack()
        }
    }

}