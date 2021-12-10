package net.entity

import android.net.Uri
import java.io.Serializable

/**
 * Copyright (C) 2021,2021/12/10, a Tencent company. All rights reserved.
 *
 * User : v_xhangxie
 *
 * Desc :
 */
data class SongEntity(
    var songId: Long = 0,
    var album: String = "",
    var albumId: Long = 0,
    var artist: String = "",
    var duration: Long = 0,
    var img_uri: Uri? = null,
    var path: String = "",
    var title: String = "",
    var history_date: String = "",
    var queue_id: Int = 0,
    var size: String = "",
    var trackNumber: Int = 0,
    var artistId: Long = 0,
    var date: Int = 0
) : Serializable
