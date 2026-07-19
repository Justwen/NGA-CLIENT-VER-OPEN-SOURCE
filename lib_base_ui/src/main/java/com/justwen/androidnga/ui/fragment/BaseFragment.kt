package com.justwen.androidnga.ui.fragment

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

open class BaseFragment : Fragment {

    private val mViewModelProvider: ViewModelProvider by lazy { ViewModelProvider(this) }

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    constructor() : super()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isEnabled = onHandleBackEvent()
                if (!isEnabled) {
                    activity?.onBackPressedDispatcher?.onBackPressed()
                    isEnabled = true
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(this, callback)
    }

    protected open fun onHandleBackEvent(): Boolean {
        activity?.finish()
        return true
    }

    protected fun setTitle(title: String) {
        activity?.title = title
    }

    protected fun getViewModelProvider(): ViewModelProvider {
        return mViewModelProvider;
    }

}
