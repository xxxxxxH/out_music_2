package net.entity

data class ArtistEntity(
    var albumCount: Int = -1,
    var id: Long = -1,
    var name: String = "",
    var songCount: Int = -1
) : BaseEntity()
