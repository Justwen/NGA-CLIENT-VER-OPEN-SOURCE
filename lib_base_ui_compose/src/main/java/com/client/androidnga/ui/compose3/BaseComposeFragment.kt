package com.client.androidnga.ui.compose3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.client.androidnga.ui.compose3.theme.AppTheme
import com.client.androidnga.ui.compose3.widget.ScaffoldApp

open class BaseComposeFragment : Fragment() {

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return ComposeView(inflater.context).apply {
//            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//            setContent {
//                AppTheme {
//                    ScaffoldApp(
//                        topAppBarData = getTopAppBarData()
//                    ) {
//                        Surface(
//                            modifier = Modifier.fillMaxSize(),
//                            color = MaterialTheme.colorScheme.background
//                        ) {
//                            ContentView()
//                        }
//                    }
//                }
//            }
//        }
//    }
////
//    open fun getTopAppBarData(): TopAppBarData {
//        val topAppBarData = TopAppBarData(requireActivity().title.toString())
//        topAppBarData.navigationIconAction = { requireActivity().finish() }
//        return topAppBarData
//    }

    @Composable
    open fun ContentView() {

    }
}