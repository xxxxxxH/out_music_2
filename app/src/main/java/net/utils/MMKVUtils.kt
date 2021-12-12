package net.utils

import com.tencent.mmkv.MMKV
import net.entity.SongEntity
import java.util.*
import kotlin.collections.ArrayList

class MMKVUtils {
    companion object {
        private var i: MMKVUtils? = null
            get() {
                field ?: run {
                    field = MMKVUtils()
                }
                return field
            }

        @Synchronized
        fun get(): MMKVUtils {
            return i!!
        }
    }

    fun saveKeys(key: String?, keyValues: String?) {
        var keys = MMKV.defaultMMKV()!!.decodeStringSet(key)
        if (keys == null) {
            keys = HashSet()
        }
        keys.add(keyValues)
        MMKV.defaultMMKV()!!.encode(key, keys)
    }

    fun getKeys(key: String?): ArrayList<String> {
        val data = ArrayList<String>()
        val keys = MMKV.defaultMMKV()!!.decodeStringSet(key)
        if (keys != null) {
            data.addAll(keys)
        }
        return data
    }

    fun getPlayList(key: String): ArrayList<SongEntity> {
        val result = ArrayList<SongEntity>()
        val keySet = MMKV.defaultMMKV()!!.decodeStringSet(key)
        keySet?.let { it ->
            for (item: String in it) {
                val entity = MMKV.defaultMMKV()!!.decodeParcelable(item, SongEntity::class.java)
                entity?.let { it1 ->
                    result.add(it1)
                }
            }
        }
        return result
    }

    fun isExist(data: ArrayList<SongEntity>, entity: SongEntity): Boolean {
        for (item in data) {
            if (item.songId == entity.songId) {
                return true
            }
        }
        return false
    }
}