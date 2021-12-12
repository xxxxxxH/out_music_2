package net.fragment.musicFragment

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.layout_fragment_artist.*
import net.adapter.ArtistAdapter
import net.basicmodel.R
import net.entity.ArtistEntity
import net.entity.BaseEntity
import net.fragment.BaseFragment
import net.fragment.DetailsFragment
import net.utils.CommonUtils
import net.utils.DataManager

class ArtistFragment : BaseFragment() {
    override fun getLayout(): Int {
        return R.layout.layout_fragment_artist
    }

    override fun initView() {
        val data = DataManager.get().getAllArtists(requireContext())
        val artistAdapter = ArtistAdapter(data, requireActivity())
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = artistAdapter
        artistAdapter.setOnItemClickListener { adapter, view, position ->
            val entity :ArtistEntity = data[position]
            val fragment = DetailsFragment(
               entity.id,
                entity.name,
                DataManager.get().getAlbumCover(requireContext(), arrayOf<String>(entity.id.toString())),
                "ArtistFragment"
            )
            CommonUtils.get().route2DetailsFragment(
                fragment, "ArtistFragment",
                activity as AppCompatActivity
            )
        }
    }
}