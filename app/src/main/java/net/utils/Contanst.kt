package net.utils

import android.Manifest

object Contanst {
    val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE
    )
    val bottomTitle = arrayOf(
        "Music", "Video", "Setting"
    )
    val topTitle = arrayOf(
        "SONGS", "ARTISTS", "ALBUMS", "GENRES"
    )
}