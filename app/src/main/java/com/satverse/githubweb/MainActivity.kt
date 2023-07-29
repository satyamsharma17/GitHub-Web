package com.satverse.githubweb

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val INPUT_FILE_REQUEST_CODE = 1
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private val url = "https://github.com/" // Replace with your website URL

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        webSettings.allowFileAccess = true
        webSettings.loadsImagesAutomatically = true
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.setSupportMultipleWindows(true) // This forces ChromeClient enabled.

        webView.loadUrl(url)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return false
            }
        }

        webView.webChromeClient = object : WebChromeClient() {

            override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: FileChooserParams): Boolean {
                mFilePathCallback = filePathCallback
                startActivityForResult(fileChooserParams.createIntent(), INPUT_FILE_REQUEST_CODE)
                return true
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        val results: Array<Uri>? = WebChromeClient.FileChooserParams.parseResult(resultCode, data)
        mFilePathCallback?.onReceiveValue(results)
        mFilePathCallback = null
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}