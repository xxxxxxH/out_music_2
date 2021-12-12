package net.fragment.musicFragment

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.layout_fragment_album.*
import net.adapter.AlbumAdapter
import net.basicmodel.R
import net.entity.AlbumEntity
import net.fragment.BaseFragment
import net.fragment.DetailsFragment
import net.utils.CommonUtils
import net.utils.DataManager

class AlbumFragment:BaseFragment() {
    override fun getLayout(): Int {
        return R.layout.layout_fragment_album
    }

    override fun initView() {
        val data = DataManager.get().getAllAlbums(requireContext())
        val albumAdapter = AlbumAdapter(data,requireActivity())
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = albumAdapter
        albumAdapter.setOnItemClickListener { adapter, view, position ->
            val entity:AlbumEntity = data[position]
            val fragment = DetailsFragment(
                entity.id,
                entity.title,
                CommonUtils.get().getImgUri(entity.id)!!,
                "AlbumFragment"
            )
            CommonUtils.get().route2DetailsFragment(
                fragment,
                "AlbumFragment",
                activity as AppCompatActivity
            )
        }
    }
}