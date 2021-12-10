package net.utils

import android.content.ContentUris
import android.net.Uri

/**
 * Copyright (C) 2021,2021/12/10, a Tencent company. All rights reserved.
 *
 * User : v_xhangxie
 *
 * Desc :
 */
class CommonUtils {
    companion object{
        private var i:CommonUtils?=null
        get() {
            field?:run {
                field = CommonUtils()
            }
            return field
        }

        @Synchronized
        fun get():CommonUtils{
            return i!!
        }
    }

    fun getImgUri(album_id: Long): Uri? {
        return try {
            ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"),
                album_id
            )
        } catch (e: Exception) {
            null
        }
    }
}