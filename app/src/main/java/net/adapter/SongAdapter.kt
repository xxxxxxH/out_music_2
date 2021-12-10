package net.adapter

import android.app.Activity
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import net.basicmodel.R
import net.entity.SongEntity
import net.utils.ScreenUtils

/**
 * Copyright (C) 2021,2021/12/10, a Tencent company. All rights reserved.
 *
 * User : v_xhangxie
 *
 * Desc :
 */
class SongAdapter(data: ArrayList<SongEntity>, val activity: Activity) :
    BaseQuickAdapter<SongEntity, BaseViewHolder>(R.layout.layout_item_song, data) {
    override fun convert(holder: BaseViewHolder, item: SongEntity) {
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
            Glide.with(activity).load(item.img_uri).into(it)
        }
        holder.setText(R.id.song, item.title)
            .setText(R.id.artist, item.artist)
    }
}