package com.daniel.cathybktour.view

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daniel.cathybktour.R
import com.daniel.cathybktour.databinding.ActivityCommonWebViewBinding
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

class CommonWebViewActivity : AppCompatActivity() {

    companion object {

        val TAG = CommonWebViewActivity::class.java.simpleName

    }

    lateinit var binding: ActivityCommonWebViewBinding
    lateinit var currentLink: String
    lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_common_web_view, null, false)
        setContentView(binding.root)

        getBundleData()
        initView()
        initListener()

    }

    private fun initListener() {

        binding.toolbar.ivBack.setOnClickListener {

            onBackPressed()

        }

    }

    private fun initView() {

        binding.wvCommonWebview.settings.let {

            it.setSupportZoom(true)
            it.builtInZoomControls = true;
            it.javaScriptEnabled = true
            it.saveFormData = true
            it.allowFileAccess = true
            it.allowContentAccess = true
            it.domStorageEnabled = true
            it.displayZoomControls = false
            it.blockNetworkImage = true

            it.layoutAlgorithm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            else
                WebSettings.LayoutAlgorithm.NARROW_COLUMNS

            it.loadWithOverviewMode = true
            it.useWideViewPort = true
            it.databaseEnabled = true

            it.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW


        }

        binding.wvCommonWebview.webViewClient = MyWebViewClient()
        binding.wvCommonWebview.webChromeClient = WebChromeClient()
        binding.wvCommonWebview.loadUrl(currentLink)
        binding.toolbar.tvToolbarTitle.text = title

    }

    private fun getBundleData() {

        var bundle = intent.extras

        if (bundle != null) {

            currentLink = bundle.getString("url").toString()
            title = bundle.getString("title").toString()

        }

    }

    private inner class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {

            Log.d(TAG, "url load - request = ${request.url}")
            var url = request.url.toString()
            val sharerFacebook = url.contains("facebook")
            val sharerMail = url.contains("mailto")

            currentLink = url

            if (sharerFacebook) {

                binding.wvCommonWebview.loadUrl(url)

                return true

            } else if (sharerMail) {

                val mailLastIndexOf = url.indexOf("mailto:") + 7
                val mailIndexOf = url.indexOf("?subject=")
                val subjectLastIndexOf = url.lastIndexOf("subject=") + 8
                val subjectIndexOf = url.indexOf("&body=")
                val strMail = url.substring(mailLastIndexOf, mailIndexOf)
                var strSubject = ""
                var strBody = ""
                try {
                    strSubject = URLDecoder.decode(url.substring(subjectLastIndexOf, subjectIndexOf), "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                try {
                    strBody = URLDecoder.decode(currentLink, "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                val mailto = "mailto:" + strMail +
                        "?subject=" + strSubject +
                        "&body=" + strBody
                val sendLink = Uri.parse(mailto)
                val intent = Intent(Intent.ACTION_VIEW, sendLink)
                startActivity(intent)
                return true

            } else {

                binding.wvCommonWebview.loadUrl(url)

                return true
            }


        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)


        }

        override fun onPageFinished(view: WebView, url: String?) {
            super.onPageFinished(view, url)

            view.settings.blockNetworkImage = false
            view.loadUrl("javascript:document.getElementsByName('viewport')[0].setAttribute('content', 'initial-scale=1.0,maximum-scale=10.0');");

        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            super.onReceivedError(view, request, error)

            // 在這裡判斷錯誤類型，並採取相應的行動
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when (error.errorCode) {
                    ERROR_HOST_LOOKUP -> {
                        // DNS解析錯誤
                    }

                    ERROR_CONNECT -> {
                        // 連接服務器錯誤
                    }

                    else -> {

                    }
                }
            }

            binding.wvCommonWebview.visibility = View.GONE
            binding.tvError.visibility = View.VISIBLE

        }
    }


    override fun onBackPressed() {

        if (binding.wvCommonWebview.canGoBack()) {

            binding.wvCommonWebview.goBack()

        } else {

            super.onBackPressed()

        }

    }

}