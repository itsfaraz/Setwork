package com.designlife.setwork.common.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.designlife.setwork.ui.theme.ComponentBackground

@Composable
fun TopPaddingComponent () =
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(
                    ComponentBackground.value
                )
    ){

    }