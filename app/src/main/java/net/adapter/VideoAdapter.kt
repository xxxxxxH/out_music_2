package net.adapter

import android.content.ContentUris
import android.provider.MediaStore
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import net.basicmodel.R
import net.entity.VideoEntity
import net.utils.CommonUtils

class VideoAdapter(data: MutableList<VideoEntity>?) :
    BaseQuickAdapter<VideoEntity, BaseViewHolder>(R.layout.item_video, data) {
    override fun convert(holder: BaseViewHolder, item: VideoEntity) {
        Glide.with(context).load(
            ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                item.id.toLong()
            )
        ).into(holder.getView(R.id.img_video))
        holder.setText(R.id.txt_time, CommonUtils.get().formatTime(item.duration.toLong()))
            .setText(R.id.txt_title, item.title)
            .setText(R.id.txt_size, CommonUtils.get().formatSize(item.filePath))
    }
}