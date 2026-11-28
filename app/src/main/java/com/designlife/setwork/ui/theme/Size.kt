package com.designlife.setwork.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.designlife.setwork.settings.presentation.enums.AppListHeight



fun updateSystemListSize(listSize : AppListHeight){
    when(listSize){
        AppListHeight.MEDIUM -> {
            todoListHeight.value = 8.dp
            noteListHeight.value = 8.dp
            deckListHeight.value = 8.dp
        }
        AppListHeight.SMALL -> {
            todoListHeight.value = 4.dp
            noteListHeight.value = 6.dp
            deckListHeight.value = 6.dp
        }
        AppListHeight.LARGE -> {
            todoListHeight.value = 10.dp
            noteListHeight.value = 10.dp
            deckListHeight.value = 10.dp
        }
    }
}

val todoListHeight = mutableStateOf<Dp>(8.dp)
val noteListHeight = mutableStateOf<Dp>(8.dp)
val deckListHeight = mutableStateOf<Dp>(8.dp)