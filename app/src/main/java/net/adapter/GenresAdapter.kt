package net.adapter

import android.app.Activity
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import net.basicmodel.R
import net.entity.GenersEntity
import net.utils.ScreenUtils

class GenresAdapter(data: MutableList<GenersEntity>, val activity: Activity) :
    BaseQuickAdapter<GenersEntity, BaseViewHolder>(R.layout.layout_item_song, data) {
    override fun convert(holder: BaseViewHolder, item: GenersEntity) {
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
            Glide.with(activity).load(item.generUri).placeholder(R.mipmap.splash_icon).into(it)
        }
        holder.setText(R.id.song, item.generName)
            .setText(R.id.artist, "${item.songCount} songs")
            .setGone(R.id.more, true)
    }
}