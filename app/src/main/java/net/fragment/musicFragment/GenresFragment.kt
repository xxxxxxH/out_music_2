package net.fragment.musicFragment

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.layout_fragment_genre.*
import net.adapter.GenresAdapter
import net.basicmodel.R
import net.entity.GenersEntity
import net.fragment.BaseFragment
import net.fragment.DetailsFragment
import net.utils.CommonUtils
import net.utils.DataManager

class GenresFragment : BaseFragment() {
    override fun getLayout(): Int {
        return R.layout.layout_fragment_genre
    }

    override fun initView() {
        val data = DataManager.get().getGenres(requireContext())
        val genresAdapter = GenresAdapter(data, requireActivity())
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = genresAdapter
        genresAdapter.setOnItemClickListener { adapter, view, position ->
            val entity: GenersEntity = data[position]
            val fragment = DetailsFragment(
                entity.generId,
                entity.generName,
                CommonUtils.get().getImgUri(entity.albumId)!!,
                "GenresFragment"
            )
            CommonUtils.get().route2DetailsFragment(
                fragment,
                "GenresFragment",
                activity as AppCompatActivity
            )
        }
    }
}