package com.justwen.androidnga.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.justwen.androidnga.base.service.api.IThemeManagerService

abstract class BaseActivity : AppCompatActivity() {

    protected var mNaviBarHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme()
        super.onCreate(savedInstanceState)
        initContentView()
        initHandleBackEvent()
        initEdgeToEdge()
        initStatusBar()
    }

    private fun initHandleBackEvent() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isEnabled = onHandleBackEvent()
                if (!isEnabled) {
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    protected fun onHandleBackEvent(): Boolean {
        return false
    }

    private fun initStatusBar() {
        val decorView = window.decorView
        val controller = WindowCompat.getInsetsController(window, decorView)
        controller.isAppearanceLightStatusBars = false
    }

    private fun initEdgeToEdge() {
        enableEdgeToEdge()
        val contentView = findViewById<View>(android.R.id.content)
        if (contentView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(
                contentView
            ) { _, insets ->
                if (window.decorView.findViewById<View?>(R.id.status_bar) == null) {
                    val stateBars =
                        insets.getInsets(WindowInsetsCompat.Type.statusBars())
                    val parent = contentView.parent as ViewGroup
                    val statusView = View(contentView.context)
                    statusView.id = R.id.status_bar
                    statusView.layoutParams =
                        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, stateBars.top)
                    statusView.setBackgroundColor(
                        getPrimaryColor()
                    )
                    parent.addView(statusView, 0)

                    val navaBars =
                        insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                    mNaviBarHeight = navaBars.bottom
                    contentView.setPadding(0, 0, 0, navaBars.bottom)
                }
                insets
            }
        }
    }

    private fun getPrimaryColor(): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }

    open fun initContentView() {
        setContentView(R.layout.activity_toolbar_template)
        setupToolbar()
    }

    open fun setupToolbar(toolbar: Toolbar = findViewById(R.id.toolbar)) {
        if (supportActionBar == null) {
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeButtonEnabled(true)
            }
        }
    }

    private fun initTheme() {
        val themeManager = ARouter.getInstance().build(IThemeManagerService.ROUTER_PATH)
            .navigation() as IThemeManagerService
        setTheme(themeManager.getTheme())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}