package net.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.MediaStore
import net.entity.SongEntity

/**
 * Copyright (C) 2021,2021/12/10, a Tencent company. All rights reserved.
 *
 * User : v_xhangxie
 *
 * Desc :
 */
class SongManager {
    companion object {
        private var i: SongManager? = null
            get() {
                field ?: run {
                    field = SongManager()
                }
                return field
            }

        @Synchronized
        fun get(): SongManager {
            return i!!
        }
    }

    @SuppressLint("Recycle")
    fun getAllSongs(context: Context): ArrayList<SongEntity> {
        val result = ArrayList<SongEntity>()
        val selectionStatement = "is_music=1 AND title != ''"
        var cur = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                "_id",
                "title",
                "artist",
                "album",
                "duration",
                "track",
                "artist_id",
                "album_id",
                "_data",
                "_size",
                "mime_type"
            ),
            selectionStatement,
            null,
            "duration DESC"
        )
        try {
            cur?.let {
                if (it.moveToFirst()) {
                    do {
                        val id: Long = it.getLong(0)
                        val title: String = it.getString(1)
                        val artist: String = it.getString(2)
                        val album: String = it.getString(3)
                        val duration: Int = it.getInt(4)
                        val trackNumber: Int = it.getInt(5)
                        val artistId: Long = it.getInt(6).toLong()
                        val albumId: Long = it.getLong(7)
                        val data: String = it.getString(8)
                        val size: String = it.getString(9)
                        if (!it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE))
                                .contains("audio/amr") && !it.getString(
                                it.getColumnIndexOrThrow(
                                    MediaStore.Audio.Media.MIME_TYPE
                                )
                            ).contains("audio/aac")
                        ) {
                            val entity = SongEntity(
                                id,
                                album,
                                albumId,
                                artist,
                                duration.toLong(),
                                CommonUtils.get().getImgUri(albumId),
                                data,
                                title,
                                "",
                                0,
                                size,
                                trackNumber,
                                artistId,
                                0
                            )
                            result.add(entity)
                        }
                    } while (it.moveToNext())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cur?.close()
        } finally {
            cur?.close()
        }
        return result
    }
}