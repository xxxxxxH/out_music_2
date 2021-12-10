package net.basicmodel

import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_float.*
import net.entity.SongEntity
import net.event.MessageEvent
import net.fragment.musicFragment.MusicFragment
import net.fragment.settingFragment.SettingFragment
import net.fragment.videoFragment.VideoFragment
import net.utils.Contanst
import net.utils.MediaPlayerManager
import net.utils.TabUtils
import net.utils.TimerManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener {
    var views: ArrayList<Fragment> = ArrayList()
    private var mediaPlayerManager: MediaPlayerManager? = null
    private var isPause: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
        initView()
        initClick()
    }

    private fun initView() {
        views.add(MusicFragment())
        views.add(VideoFragment())
        views.add(SettingFragment())
        TabUtils.get().initViewPager(views, tab, viewpager, this, Contanst.bottomTitle)
    }

    private fun initClick() {
        previous.setOnClickListener {

        }
        option.setOnClickListener {
            mediaPlayerManager?.let {
                if (!TextUtils.isEmpty(it.musicPath)){
                    isPause = if (it.isPlaying()) {
                        fmm.stop()
                        it.pause()
                        true
                    } else {
                        fmm.start()
                        it.start()
                        false
                    }
                    setOptionButton(it)
                }
            }
        }
        next.setOnClickListener {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        TimerManager.get().destroyTimer()
    }

    private fun setCover(uri: Uri) {
        fmm.setMusicCover(
            BitmapFactory.decodeStream(
                this.contentResolver.openInputStream(
                    uri
                )
            )
        )
    }

    private fun setOptionButton(mediaPlayerManager: MediaPlayerManager) {
        if (mediaPlayerManager.isPlaying()) {
            option.setImageResource(R.drawable.ic_pause)
        } else {
            option.setImageResource(R.drawable.ic_play)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: MessageEvent) {
        val msg = event.getMessage()
        when (msg[0]) {
            "itemClick" -> {
                val entity: SongEntity = msg[1] as SongEntity
                setCover(entity.img_uri!!)
                mediaPlayerManager = MediaPlayerManager.get()
                mediaPlayerManager!!.getMediaPlayer().setOnCompletionListener(this)
                mediaPlayerManager!!.musicPath = entity.path
                mediaPlayerManager!!.setPath()
                mediaPlayerManager!!.start()
                TimerManager.get().destroyTimer()
                TimerManager.get().initTimer()
                TimerManager.get().start()
                fmm.start()
                setOptionButton(mediaPlayerManager!!)
            }
            "update" -> {
                if (!isPause) {
                    val percent = mediaPlayerManager!!.getCurrentPercent().toFloat()
                    Log.i("xxxxxxH", "percent = $percent")
                    fmm.start()
                    fmm.setProgress(percent)
                } else {
                    fmm.stop()
                }
            }
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        fmm.stop()
        TimerManager.get().destroyTimer()
    }
}