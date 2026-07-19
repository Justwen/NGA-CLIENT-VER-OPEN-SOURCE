package com.justwent.androidnga.bu.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.JsResult
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.justwen.androidnga.base.activity.ARouterConstants
import com.justwen.androidnga.ui.compose.BaseComposeActivity
import gov.anzong.androidnga.base.util.ToastUtils

@Route(path = ARouterConstants.ACTIVITY_LOGIN)
class LoginActivity : BaseComposeActivity() {

    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ToastUtils.info("不支持QQ和微博登录");
    }

    @Composable
    override fun ContentView() {
        LoginWebView(url = LoginViewModel.URL_LOGIN, onLoginCallback = {
            if (viewModel.checkLoginResult(it)) {
                setResult(RESULT_OK)
            }
        })
    }

    override fun finish() {
        super.finish()
        if (viewModel.checkLoginResult()) {
            setResult(RESULT_OK)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    fun LoginWebView(url: String, onLoginCallback: ValueCallback<Pair<String, String>>) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webChromeClient = object : WebChromeClient() {

                        override fun onJsConfirm(
                            view: WebView?,
                            url: String?,
                            message: String?,
                            result: JsResult?
                        ): Boolean {
                            onLoginCallback.onReceiveValue(Pair(url!!, message!!))
                            return super.onJsConfirm(view, url, message, result)
                        }

                    }
                    webViewClient = object : WebViewClient() {

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            request?.url?.let {
                                viewModel.currentUrl = it.toString()
                                view?.loadUrl(it.toString())
                            }
                            return true
                        }
                    }
                    getSettings().apply {
                        javaScriptEnabled = true
                        javaScriptCanOpenWindowsAutomatically = true
                    }
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }, update = {
                it.loadUrl(url)
            })
    }

}