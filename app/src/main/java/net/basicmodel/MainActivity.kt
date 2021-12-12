package net.basicmodel

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_drawer.*
import kotlinx.android.synthetic.main.layout_float.*
import net.entity.SongEntity
import net.event.MessageEvent
import net.fragment.HistoryFragment
import net.fragment.PlayListFragment
import net.fragment.musicFragment.MusicFragment
import net.fragment.settingFragment.SettingFragment
import net.fragment.videoFragment.VideoFragment
import net.utils.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener {
    var views: ArrayList<Fragment> = ArrayList()
    var data: ArrayList<SongEntity>? = null
    private var mediaPlayerManager: MediaPlayerManager? = null
    private var isPause: Boolean = false
    var currentPos = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
        MMKV.initialize(this)
        initView()
        initClick()
    }

    private fun initView() {
        data = DataManager.get().getAllSongs(this)
        views.add(MusicFragment(data))
        views.add(VideoFragment())
        views.add(SettingFragment())
        TabUtils.get().initViewPager(views, tab, viewpager, this, Contanst.bottomTitle)
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                tool.visibility = if (position == 0) View.VISIBLE else View.GONE
                fmm.visibility = if (position == 0) View.VISIBLE else View.GONE
                if (position > 0) {
                    EventBus.getDefault().post(MessageEvent("pause"))
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
        slideLayout?.let {
            it.layoutParams = it.layoutParams.apply {
                width = ScreenUtils.getScreenSize(this@MainActivity)[1] / 4 * 3
            }
        }
        if (!data.isNullOrEmpty()) {
            Glide.with(this).load(data!![0].img_uri).into(slideCover)
            slideTitle.text = data!![0].artist
        }
    }

    private fun initClick() {
        previous.setOnClickListener {
            if (currentPos <= 0) {
                return@setOnClickListener
            } else {
                EventBus.getDefault().post(MessageEvent("itemClick", data!![(currentPos - 1)]))
            }
        }
        option.setOnClickListener {
            mediaPlayerManager?.let {
                if (!TextUtils.isEmpty(it.musicPath)) {
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
            if (currentPos == data!!.size - 1 || currentPos == -1) {
                return@setOnClickListener
            } else {
                EventBus.getDefault().post(MessageEvent("itemClick", data!![(currentPos + 1)]))
            }
        }

        menu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        nv.setNavigationItemSelectedListener {
            when (it.itemId) {
//                R.id.nav_myfolders -> {
//                    //Toast.makeText(this,"${it.title}",Toast.LENGTH_SHORT).show()
//                }
                R.id.nav_myplaylist -> {
                    CommonUtils.get()
                        .route2DetailsFragment(PlayListFragment(), "PlayListFragment", this)
                }
                R.id.nav_myhistory -> {
                    CommonUtils.get()
                        .route2DetailsFragment(HistoryFragment(), "PlayListFragment", this)
                }
                R.id.nav_share -> {
                    try {
                        val i = Intent(Intent.ACTION_SEND)
                        i.type = "text/plain"
                        i.putExtra(Intent.EXTRA_SUBJECT, "Music Player")
                        var sAux = """
            
            ${resources.getString(R.string.share_app_msg)}
            
            
            """.trimIndent()
                        sAux =
                            sAux + "https://play.google.com/store/apps/details?id=" + this.packageName
                        i.putExtra(Intent.EXTRA_TEXT, sAux)
                        startActivity(Intent.createChooser(i, "choose one"))
                    } catch (e: java.lang.Exception) {
                    }
                }
                R.id.nav_rateus -> {
                    val i = Intent("android.intent.action.VIEW")
                    i.data =
                        Uri.parse("https://play.google.com/store/apps/details?id=" + this.packageName)
                    startActivity(i)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
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
            "pause" -> {
                isPause = true
                fmm.stop()
                mediaPlayerManager = MediaPlayerManager.get()
                if (mediaPlayerManager!!.isPlaying())
                    mediaPlayerManager!!.pause()
                setOptionButton(mediaPlayerManager!!)
            }
            "itemClick" -> {
                isPause = false
                val entity: SongEntity = msg[1] as SongEntity
                currentPos = CommonUtils.get().getIndex(data!!, entity)
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