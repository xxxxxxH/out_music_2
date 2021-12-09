package net.basicmodel

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.example.weeboos.permissionlib.PermissionRequest
import net.utils.Contanst
import java.util.*

class SplashActivity : AppCompatActivity() {
    val handler:Handler = object :Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0){
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_splash)
        PermissionRequest.getInstance().build(this)
            .requestPermission(object : PermissionRequest.PermissionListener {
                override fun permissionGranted() {
                   val msg = Message()
                    msg.what = 0
                    handler.sendMessageDelayed(msg,1000)
                }

                override fun permissionDenied(permissions: ArrayList<String>?) {
                    finish()
                }

                override fun permissionNeverAsk(permissions: ArrayList<String>?) {
                    finish()
                }

            }, Contanst.permissions)
    }
}