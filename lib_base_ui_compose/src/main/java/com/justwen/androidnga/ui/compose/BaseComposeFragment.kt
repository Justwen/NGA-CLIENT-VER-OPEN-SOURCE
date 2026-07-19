package com.justwen.androidnga.ui.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.justwen.androidnga.ui.compose.theme.AppTheme
import com.justwen.androidnga.ui.compose.widget.ScaffoldApp
import com.justwen.androidnga.ui.compose.widget.TopAppBarData

open class BaseComposeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(inflater.context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    ScaffoldApp(
                        topAppBarData = getTopAppBarData()
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colors.background
                        ) {
                            ContentView()
                        }
                    }
                }
            }
        }
    }

    open fun getTopAppBarData(): TopAppBarData {
        val topAppBarData = TopAppBarData(requireActivity().title.toString())
        topAppBarData.navigationIconAction = { requireActivity().finish() }
        return topAppBarData
    }

    @Composable
    open fun ContentView() {

    }
}