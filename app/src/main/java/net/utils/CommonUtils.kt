package net.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.provider.MediaStore
import android.text.Html
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.tencent.mmkv.MMKV
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import net.adapter.VideoAdapter
import net.basicmodel.BuildConfig
import net.basicmodel.R
import net.entity.SongEntity
import net.entity.VideoEntity
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CommonUtils {
    companion object {
        private var i: CommonUtils? = null
            get() {
                field ?: run {
                    field = CommonUtils()
                }
                return field
            }

        @Synchronized
        fun get(): CommonUtils {
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

    fun route2DetailsFragment(fragment: Fragment, name: String, activity: AppCompatActivity) {
        val ft = activity.supportFragmentManager.beginTransaction()
        ft.replace(R.id.framlayout_main, fragment, name)
        ft.addToBackStack(name)
        ft.commit()
    }

    fun showPopUp(view: View?, context: Context, activity: Activity, mediaItems: SongEntity) {
        val popupMenu = PopupMenu(activity, view)
        try {
            val fields = popupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field[popupMenu]
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons = classPopupHelper.getMethod(
                        "setForceShowIcon",
                        Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val menuInflater = popupMenu.menuInflater
        menuInflater.inflate(R.menu.popup_menu_music, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { item ->
            if (item.title == activity.resources.getString(R.string.proparty)) {
                propertyDialog(activity, mediaItems.path, mediaItems.size)
            } else if (item.title == activity.resources.getString(R.string.add_to_playlist)) {
                val data = MMKVUtils.get().getPlayList("music")
                if (!MMKVUtils.get().isExist(data, mediaItems)) {
                    val key = System.currentTimeMillis().toString()
                    MMKVUtils.get().saveKeys("music", key)
                    MMKV.defaultMMKV()!!.encode(key, mediaItems)
                }
            } else if (item.title == activity.resources.getString(R.string.share)) {
                val share = Intent(Intent.ACTION_SEND)
                share.type = "audio/*"
                share.putExtra(
                    Intent.EXTRA_STREAM, Uri.parse( /*"file:///" +*/
                        mediaItems.path
                    )
                )
                activity.startActivity(Intent.createChooser(share, "Share Music"))
            }
            false
        }
    }

    fun propertyDialog(activity: Activity, path: String?, size: String) {
        val inflater = LayoutInflater.from(activity.applicationContext)
        val view: View = inflater.inflate(R.layout.poperty_alert_dialog, null, false)
        val txt_path = view.findViewById<View>(R.id.txt_path) as TextView
        val txt_size = view.findViewById<View>(R.id.txt_size) as TextView
        txt_path.text = path
        try {
            txt_size.text = toNumInUnits(size.toLong())
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        val propertyAlertBuilder = AlertDialog.Builder(activity)
        propertyAlertBuilder.setTitle("Property")
        propertyAlertBuilder.setView(view)
        propertyAlertBuilder.setPositiveButton(Html.fromHtml("<font color='black'>OK</font>"), null)
        propertyAlertBuilder.show()
    }

    fun toNumInUnits(bytes: Long): String {
        var bytes = bytes
        var u = 0
        while (bytes > 1024 * 1024) {
            u++
            bytes = bytes shr 10
        }
        if (bytes > 1024) u++
        return String.format("%.1f %cB", bytes / 1024f, " kMGTPE"[u])
    }

    fun getIndex(data: ArrayList<SongEntity>, entity: SongEntity): Int {
        for ((index, item) in data.withIndex()) {
            if (item.songId == entity.songId) {
                return index
            }
        }
        return -1
    }

    fun formatTime(m: Long): String {
        val hrs = TimeUnit.MILLISECONDS.toHours(m).toInt() % 24
        val min = TimeUnit.MILLISECONDS.toMinutes(m).toInt() % 60
        val sec = TimeUnit.MILLISECONDS.toSeconds(m).toInt() % 60
        return String.format("%02d:%02d:%02d", hrs, min, sec)
    }

    fun formatSize(path: String): String {
        val file = File(path)
        val length = file.length().toDouble()

        //   length = length / 1024;
        return formatFileSize(length)
    }

    fun formatFileSize(size: Double): String {
        var hrSize: String? = null
        val k = size / 1024.0
        val m = k / 1024.0
        val g = m / 1024.0
        val t = g / 1024.0
        val dec = DecimalFormat("0.00")
        hrSize = if (t > 1) {
            dec.format(t) + " TB"
        } else if (g > 1) {
            dec.format(g) + " GB"
        } else if (m > 1) {
            dec.format(m) + " MB"
        } else if (k > 1) {
            dec.format(k) + " KB"
        } else {
            dec.format(size) + " Bytes"
        }
        return hrSize
    }

    fun popupWindow(
        view: View?,
        act: Activity,
        vidModel: VideoEntity,
        pos: Int,
        callFrom: String,
        videoAdapter: VideoAdapter
    ) {
        val mPopupMenu = PopupMenu(act, view, Gravity.BOTTOM)
        try {
            val fields = mPopupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field[mPopupMenu]
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons = classPopupHelper.getMethod(
                        "setForceShowIcon",
                        Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val menuInflater = mPopupMenu.menuInflater
        menuInflater.inflate(R.menu.popup_menu_video, mPopupMenu.menu)
        mPopupMenu.show()
        mPopupMenu.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Property" -> {
                    PropertyDialog(act, vidModel)
                }
                "Rename" -> {
                    try {
                        renameVideo(vidModel, act, callFrom, videoAdapter)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                "Share" -> {
                    val videoURI = FileProvider.getUriForFile(
                        act, BuildConfig.APPLICATION_ID.toString() + ".provider",
                        File(vidModel.filePath)
                    )
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "video/*"
                    share.putExtra(Intent.EXTRA_STREAM, videoURI)
                    act.startActivity(Intent.createChooser(share, "Share Video"))
                }
            }
            false
        }
    }

    fun PropertyDialog(activity: Activity, videoModel: VideoEntity) {
        val inflater = LayoutInflater.from(activity.applicationContext)
        val view: View = inflater.inflate(R.layout.property_video_dialog, null, false)
        val txt_path = view.findViewById<View>(R.id.text_path_value) as TextView
        val txt_size = view.findViewById<View>(R.id.text_video_size_value) as TextView
        val txt_resolution = view.findViewById<View>(R.id.text_resolution_value) as TextView
        val txt_date = view.findViewById<View>(R.id.text_date_value) as TextView
        val txt_duration = view.findViewById<View>(R.id.text_duration) as TextView
        txt_path.text = videoModel.filePath
        txt_date.text = dateformate(videoModel.date)
        txt_resolution.text = videoModel.resolution
        txt_size.text = formatSize(videoModel.filePath)
        txt_duration.text = formatTime(videoModel.duration.toLong())
        val propertyAlertBuilder = androidx.appcompat.app.AlertDialog.Builder(activity)
        propertyAlertBuilder.setTitle("Property")
        propertyAlertBuilder.setView(view)
        propertyAlertBuilder.setPositiveButton("OK", null)
        propertyAlertBuilder.show()
    }

    fun renameVideo(
        track: VideoEntity,
        activity: Activity,
        call_from: String,
        videoAdapter: VideoAdapter
    ) {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.rename_video_dialog, null)
        val inputTitle = view.findViewById<View>(R.id.titleEdit) as EditText
        try {
            val title: String = track.title
            inputTitle.setText(title)
            inputTitle.setSelection(title.length)
        } catch (e: Exception) {
        }
        val alert = androidx.appcompat.app.AlertDialog.Builder(activity)
        alert.setTitle("Rename File")
        alert.setPositiveButton("Rename",
            DialogInterface.OnClickListener { dialog, whichButton ->
                val newTitle = inputTitle.text.toString().trim { it <= ' ' }
                if (TextUtils.isEmpty(newTitle)) {
                    Toast.makeText(activity, "Enter Text", Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                val result = renameVideo1(activity, track, call_from, newTitle, videoAdapter)
                if (result) {
                    Toast.makeText(activity, "rename successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "video can't rename", Toast.LENGTH_SHORT).show()
                }
            })
        alert.setNegativeButton(
            "Cancel"
        ) { dialog, whichButton -> dialog.cancel() }
        alert.setView(view)
        alert.show()
    }

    fun dateformate(str_date: String): String? {
        try {
            val millisecond = str_date.toLong() * 1000
            // or you already have long value of date, use this instead of milliseconds variable.
            return DateFormat.format("dd-MM-yyyy", Date(millisecond)).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun renameVideo1(
        ctx: Context,
        track: VideoEntity,
        call_from: String,
        newTitle: String,
        videoAdapter: VideoAdapter
    ): Boolean {
        var newTitle = newTitle
        try {
            if (!TextUtils.isEmpty(newTitle)) {
                try {
                    newTitle = newTitle.replace(
                        newTitle.substring(newTitle.lastIndexOf(".")).toRegex(),
                        ""
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val selArg = arrayOf<String>(track.id)
                if (File(track.filePath).exists()) {
                    val currentFileName: String = track.filePath.substring(
                        track.filePath.lastIndexOf("/"),
                        track.filePath.length
                    )
                    val filePath: File = File(track.filePath)
                    val dir = filePath.parentFile
                    val from = File(dir, currentFileName)
                    val final_name =
                        newTitle + currentFileName.substring(currentFileName.lastIndexOf("."))
                    val to = File(dir, final_name)
                    from.renameTo(to)
                    val resolver = ctx.contentResolver
                    val valuesMedia = ContentValues()
                    valuesMedia.put(MediaStore.Video.Media.TITLE, final_name)
                    valuesMedia.put(MediaStore.Video.Media.DISPLAY_NAME, final_name)
                    valuesMedia.put(MediaStore.Video.Media.DATA, to.absolutePath)
                    resolver.update(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        valuesMedia,
                        MediaStore.Video.Media._ID + " = ?",
                        selArg
                    )
                    if (call_from == "folder") {

                    } else if (call_from == "search") {

                    } else {
                        if (videoAdapter.data.contains(track)) {
                            val i: Int = videoAdapter.data.indexOf(track)
                            track.title = final_name
                            videoAdapter.data[i].title = final_name
                            videoAdapter.data[i].filePath = to.absolutePath
                            videoAdapter.notifyItemChanged(i)
                        }
                    }
                }
                return true
            }
        } catch (e: Exception) {
            Log.e("exception..rename", e.message + "..")
            e.printStackTrace()
        }
        return false
    }

    fun deleteCache(context: Context?) {
        try {
            val dir = context!!.cacheDir
            deleteDir(dir)
            val cacheSIzeAlert = androidx.appcompat.app.AlertDialog.Builder(
                context
            )
            cacheSIzeAlert.setTitle("Cache Cleared")
            cacheSIzeAlert.setMessage("Cache has been cleared")
            val cacheDialog = cacheSIzeAlert.create()
            cacheSIzeAlert.setPositiveButton(
                "OK"
            ) { dialog, which -> cacheDialog.dismiss() }
            cacheDialog.show()
        } catch (e: Exception) {
        }
    }

    fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(
                    File(
                        dir,
                        children[i]
                    )
                )
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) dir.delete() else {
            false
        }
    }

    fun initializeCache(context: Context) {
        var size: Long = 0
        size += getDirSize(context.cacheDir)
        size += getDirSize(context.externalCacheDir)
        val cacheSIzeAlert = androidx.appcompat.app.AlertDialog.Builder(
            context
        )
        cacheSIzeAlert.setTitle("Cache usage")
        cacheSIzeAlert.setMessage(toNumInUnits(size))
        val cacheDialog = cacheSIzeAlert.create()
        cacheSIzeAlert.setPositiveButton(
            "Ok"
        ) { dialog, which -> cacheDialog.dismiss() }
        cacheDialog.show()
    }

    fun getDirSize(dir: File?): Long {
        var size: Long = 0
        try {
            for (file in dir!!.listFiles()) {
                if (file != null && file.isDirectory) {
                    size += getDirSize(file)
                } else if (file != null && file.isFile) {
                    size += file.length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    fun ShowTimePickerDialog(
        listener: TimePickerDialog.OnTimeSetListener,
        activity: AppCompatActivity
    ) {
        val now = Calendar.getInstance()
        val tdp = TimePickerDialog.newInstance(
            listener,
            now[Calendar.HOUR_OF_DAY],
            now[Calendar.MINUTE], false
        )
        tdp!!.show((activity as AppCompatActivity?)!!.fragmentManager, "TimePickerDialog")
    }

    fun timeFormate(hourOfDay: Int, minut: Int): String {
        var timein12Format = ""
        try {
            val sdf = SimpleDateFormat("H:mm")
            val dateObj = sdf.parse("$hourOfDay:$minut")
            timein12Format = SimpleDateFormat("K:mm a").format(dateObj)
            val f1: java.text.DateFormat =
                SimpleDateFormat("HH:mm") //HH for hour of the day (0 - 23)
            val d = f1.parse("$hourOfDay:$minut")
            val f2: java.text.DateFormat = SimpleDateFormat("h:mm a")
            timein12Format = f2.format(d).toUpperCase() // "12:18am"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return timein12Format
    }
}