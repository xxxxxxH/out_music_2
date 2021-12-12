package net.basicmodel

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class PrivacyActivity : AppCompatActivity() {
    private var webview: WebView? = null
    private var progressBar: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_privacy_policy)
        webview = findViewById<View>(R.id.webview) as WebView
        val settings = webview!!.settings
        settings.javaScriptEnabled = true
        webview!!.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        val alertDialog = AlertDialog.Builder(this).create()
        progressBar = ProgressDialog.show(this@PrivacyActivity, "Please Wait...", "Loading...")
        webview!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                if (progressBar!!.isShowing) {
                    progressBar!!.dismiss()
                }
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                alertDialog.setTitle("Error")
                alertDialog.setMessage(description)
                alertDialog.setButton("OK",
                    DialogInterface.OnClickListener { dialog, which -> return@OnClickListener })
                alertDialog.show()
            }
        }

        // change your privacy-policy link here
        webview!!.loadUrl("http://goolge.com/policy")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "Main"
    }
}


