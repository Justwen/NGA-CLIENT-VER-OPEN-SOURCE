package com.justwen.androidnga.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView

open class WebViewFragment : BaseFragment() {

    protected var webView: WebView? = null

    protected var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.let {
            url = it.getString("url", null)
            val title = it.getString("title", "")
            if (title.isNotEmpty()) {
                setTitle(title)
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        webView = WebView(inflater.context)
        return webView
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        webView?.settings?.javaScriptEnabled = true
        url?.let {
            webView?.loadUrl(it)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        webView?.onResume()
        super.onResume()
    }

    override fun onPause() {
        webView?.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        webView?.destroy()
        webView = null
        super.onDestroyView()
    }

    override fun onHandleBackEvent(): Boolean {
        webView?.let {
            if (it.canGoBack()) {
                it.goBack()
                return true
            }
        }
        return super.onHandleBackEvent()
    }

}