package net.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import net.entity.*

class DataManager {
    companion object {
        private var i: DataManager? = null
            get() {
                field ?: run {
                    field = DataManager()
                }
                return field
            }

        @Synchronized
        fun get(): DataManager {
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
                it.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cur?.close()
        } finally {
            cur?.close()
        }
        return result
    }

    @SuppressLint("Recycle")
    fun getAllArtists(context: Context): ArrayList<ArtistEntity> {
        val result = ArrayList<ArtistEntity>()
        val cur = context.contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            arrayOf("_id", "artist", "number_of_albums", "number_of_tracks"),
            null,
            null,
            "artist_key DESC"
        )
        try {
            cur?.let {
                if (it.moveToFirst()) {
                    do {
                        val albumCount = it.getInt(2)
                        val id = it.getLong(0)
                        val name = it.getString(1)
                        val songCount = it.getInt(3)
                        val entity = ArtistEntity(albumCount, id, name, songCount)
                        result.add(entity)
                    } while (it.moveToNext())
                }
                it.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cur?.close()
        } finally {
            cur?.close()
        }
        return result
    }

    @SuppressLint("Recycle")
    fun getAllAlbums(context: Context): ArrayList<AlbumEntity> {
        val result = ArrayList<AlbumEntity>()
        val cur = context.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            arrayOf("_id", "album", "artist", "artist_id", "numsongs", "minyear"),
            null, null,
            "album_key"
        )
        try {
            cur?.let {
                if (it.moveToFirst()) {
                    do {
                        val id: Long = it.getLong(0)
                        val title: String = it.getString(1)
                        val artistName: String = it.getString(2)
                        val artistId: Long = it.getLong(3)
                        val songCount: Int = it.getInt(4)
                        val year: Int = it.getInt(5)
                        val entity = AlbumEntity(
                            artistId,
                            artistName,
                            id,
                            songCount,
                            title,
                            CommonUtils.get().getImgUri(id),
                            year
                        )
                        result.add(entity)
                    } while (it.moveToNext())
                }
                it.close()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            cur?.close()
        } finally {
            cur?.close()
        }
        return result
    }

    @SuppressLint("Recycle")
    fun getGenres(context: Context): ArrayList<GenersEntity> {
        val result = ArrayList<GenersEntity>()
        val cur = context.contentResolver.query(
            MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Audio.Genres.NAME, MediaStore.Audio.Genres._ID),
            null,
            null,
            ""
        )
        try {
            cur?.let {
                if (it.moveToFirst()) {
                    do {
                        val entity = GenersEntity()
                        val name =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME))
                        entity.generName = name
                        val id = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID))
                            .toLong()
                        entity.generId = id
                        val c = context.contentResolver.query(
                            MediaStore.Audio.Genres.Members.getContentUri(
                                "external",
                                id
                            ),
                            arrayOf(
                                "title",
                                "_data",
                                "duration",
                                "_id",
                                "album",
                                "artist",
                                "album_id"
                            ),
                            null,
                            null,
                            null
                        )
                        if (c != null) {
                            if (c.moveToFirst()) {
                                val albumIDLong = c.getLong(c.getColumnIndex("album_id"))
                                val img_uri: Uri = CommonUtils.get().getImgUri(albumIDLong)!!
                                val song_count = c.count
                                entity.generUri = img_uri
                                entity.albumId = albumIDLong
                                entity.songCount = song_count
                            }
                        }
                        c?.close()
                        result.add(entity)
                    } while (it.moveToNext())
                }
                it.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cur?.close()
        } finally {
            cur?.close()
        }
        return result
    }

    @SuppressLint("Recycle")
    fun getAlbumCover(context: Context, whereVal: Array<String>): Uri {
        var albumID: Long = 0
        val cur = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf("album_id"),
            MediaStore.Audio.Media.ARTIST_ID + "=?",
            whereVal,
            MediaStore.Audio.Media.TITLE
        )
        try {
            cur?.let {
                if (it.moveToFirst()) {
                    albumID = it.getLong(it.getColumnIndex("album_id"))
                }
                it.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cur?.close()
        } finally {
            cur?.close()
        }
        return CommonUtils.get().getImgUri(albumID)!!
    }

    @SuppressLint("Recycle")
    fun getSongsByArtist(context: Context, id: Long): ArrayList<SongEntity> {
        val result = ArrayList<SongEntity>()
        val cur = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                "_id",
                "title",
                "artist",
                "album",
                "duration",
                "track",
                "album_id",
                "_data",
                "_size"
            ),
            "is_music=1 AND title != '' AND artist_id=$id",
            null,
            ""
        )
        cur?.let {
            if (it.moveToFirst()) {
                try {
                    do {
                        val iId: Long = it.getLong(0)
                        val title: String = it.getString(1)
                        val artist: String = it.getString(2)
                        val album: String = it.getString(3)
                        val duration: Int = it.getInt(4)
                        val trackNumber: Int = it.getInt(5)
                        val albumId: Long = it.getInt(6).toLong()
                        val data: String = it.getString(7)
                        val size: String = it.getString(8)
                        val artistId: Long = id
                        val entity = SongEntity(
                            iId, album, albumId, artist, duration.toLong(),
                            CommonUtils.get().getImgUri(albumId),
                            data, title, "",
                            0, size, trackNumber, artistId, 0
                        )
                        result.add(entity)
                    } while (it.moveToNext())
                } catch (e: Exception) {
                    it.close()
                } finally {
                    it.close()
                }
            }
        }
        return result
    }

    @SuppressLint("Recycle")
    fun getSongsByAlbum(context: Context, aId: Long): ArrayList<SongEntity> {
        val result = ArrayList<SongEntity>()
        val cur = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                "_id",
                "title",
                "artist",
                "album",
                "duration",
                "track",
                "artist_id",
                "_data",
                "_size"
            ),
            "is_music=1 AND title != '' AND album_id=$aId",
            null,
            ""
        )
        cur?.let {
            if (it.moveToFirst()) {
                try {
                    do {
                        val id: Long = it.getLong(0)
                        val title: String = it.getString(1)
                        val artist: String = it.getString(2)
                        val album: String = it.getString(3)
                        val duration: Int = it.getInt(4)
                        var trackNumber: Int = it.getInt(5)
                        while (trackNumber >= 1000) {
                            trackNumber -= 1000
                        }
                        val artistId: Long = it.getInt(6).toLong()
                        val data: String = it.getString(7)
                        val size: String = it.getString(8)
                        val albumId: Long = aId
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
                    } while (it.moveToNext())
                } catch (e: Exception) {
                    it.close()
                } finally {
                    it.close()
                }
            }
        }
        return result
    }


    @SuppressLint("Recycle")
    fun getSongsByGenres(context: Context, gId: Long, album: String): ArrayList<SongEntity> {
        val result = ArrayList<SongEntity>()
        val cur = context.contentResolver.query(
            MediaStore.Audio.Genres.Members.getContentUri(
                "external",
                gId
            ),
            arrayOf(
                "title",
                "_data",
                "duration",
                "_id",
                "album",
                "artist",
                "album_id",
                "_size"
            ),
            null,
            null,
            null
        )
        cur?.let {
            if (it.moveToFirst()) {
                try {
                    do {
                        val id = it.getLong(it.getColumnIndex("_id"))
                        val title = it.getString(it.getColumnIndex("title"))
                        val duration = it.getLong(it.getColumnIndex("duration"))
                        val artist = it.getString(it.getColumnIndex("artist"))
                        val data = it.getString(it.getColumnIndex("_data"))
                        val albumId = it.getLong(it.getColumnIndex("album_id"))
                        val size = it.getString(it.getColumnIndex("_size"))
                        val entity = SongEntity(
                            id,
                            album,
                            albumId,
                            artist,
                            duration,
                            CommonUtils.get().getImgUri(albumId),
                            data,
                            title,
                            "",
                            0,
                            size,
                            0,
                            0,
                            0
                        )
                        result.add(entity)
                    } while (it.moveToNext())
                } catch (e: Exception) {
                    e.printStackTrace()
                    it.close()
                } finally {
                    it.close()
                }
            }
        }
        return result
    }

    @SuppressLint("Recycle")
    fun getAllVideos(context: Context): ArrayList<VideoEntity> {
        val result = ArrayList<VideoEntity>()
        val cur = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.Video.VideoColumns.DURATION,
                MediaStore.Video.VideoColumns.RESOLUTION,
                MediaStore.Video.VideoColumns.DATE_TAKEN,
                MediaStore.Video.VideoColumns.DATE_ADDED,
                MediaStore.Video.VideoColumns.DISPLAY_NAME
            ),
            null,
            null,
            null
        )
        cur?.let {
            try {
                if (it.moveToFirst()) {
                    do {
                        val id = it.getString(it.getColumnIndexOrThrow("_id"))
                        val filePath = it.getString(it.getColumnIndexOrThrow("_data"))
                        val title = it.getString(it.getColumnIndexOrThrow("_display_name"))
                        val duration = it.getString(it.getColumnIndexOrThrow("duration"))
                        val resolution = it.getString(it.getColumnIndexOrThrow("resolution"))
                        val date = it.getString(it.getColumnIndexOrThrow("date_added"))
                        val entity = VideoEntity(id, filePath, title, duration, resolution, date)
                        result.add(entity)
                    } while (it.moveToNext())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                it.close()
            } finally {
                it.close()
            }
        }
        return result
    }
}