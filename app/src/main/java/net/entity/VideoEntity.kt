package net.entity

import java.io.Serializable

data class VideoEntity(
    var id: String = "",
    var filePath: String = "",
    var title: String = "",
    var duration: String = "",
    var resolution: String = "",
    var date: String = ""
):Serializable
