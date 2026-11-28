package com.designlife.setwork.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.designlife.setwork.common.presentation.components.ProgressBar
import com.designlife.justdo.setworkllm.SetworkOLLM

class OChatFragment : Fragment() {

    private var chatWindowProgress = mutableStateOf(true)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatWindowProgress.value = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Box(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .padding(bottom = 20.dp)
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedVisibility(!chatWindowProgress.value) {
                        SetworkOLLM.ChatScreenView()
                    }
                    AnimatedVisibility(chatWindowProgress.value) {
                        ProgressBar()
                    }
                }

            }
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}