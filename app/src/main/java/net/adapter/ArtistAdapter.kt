package net.adapter

import android.app.Activity
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import net.basicmodel.R
import net.entity.ArtistEntity
import net.utils.DataManager
import net.utils.ScreenUtils

class ArtistAdapter(data: MutableList<ArtistEntity>, val activity: Activity) :
    BaseQuickAdapter<ArtistEntity, BaseViewHolder>(R.layout.layout_item_song, data) {
    override fun convert(holder: BaseViewHolder, item: ArtistEntity) {
        holder.getView<RelativeLayout>(R.id.root).let {
            it.layoutParams = it.layoutParams.apply {
                height = ScreenUtils.getScreenSize(activity)[0] / 9
            }
        }
        holder.getView<ImageView>(R.id.cover).let {
            it.layoutParams = it.layoutParams.apply {
                width = ScreenUtils.getScreenSize(activity)[1] / 4
                height = ScreenUtils.getScreenSize(activity)[1] / 4
            }
            Glide.with(activity)
                .load(DataManager.get().getAlbumCover(context, arrayOf<String>(item.id.toString())))
                .placeholder(R.mipmap.splash_icon).into(it)
        }
        holder.setText(R.id.song, item.name)
            .setText(R.id.artist, "${item.albumCount} albums | ${item.songCount} songs")
            .setGone(R.id.more, true)
    }
}