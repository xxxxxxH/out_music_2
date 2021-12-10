package net.utils

import android.util.Log
import com.cpacm.FloatingMusicMenu
import kotlinx.android.synthetic.main.layout_float.*
import net.event.MessageEvent
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Copyright (C) 2021,2021/12/10, a Tencent company. All rights reserved.
 *
 * User : v_xhangxie
 *
 * Desc :
 */
class TimerManager {
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    companion object{
        private var i:TimerManager?= null
        get() {
            field?:run {
                field = TimerManager()
            }
            return field
        }
        @Synchronized
        fun get():TimerManager{
            return i!!
        }
    }

    fun initTimer(){
        timerTask = object : TimerTask() {
            override fun run() {
                EventBus.getDefault().post(MessageEvent("update"))
            }
        }
        timer = Timer()
    }

    fun destroyTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
        if (timerTask != null) {
            timerTask!!.cancel()
            timerTask = null
        }
    }

    fun start(){
        timer!!.schedule(timerTask,0,1000)
    }
}