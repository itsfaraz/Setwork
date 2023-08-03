package com.designlife.justdo.container.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.designlife.justdo.common.utils.enums.RepeatType

@Composable
fun RepeatComponent(
    repeatList : List<Pair<String, RepeatType>>,
    selectedIndex : Int,
    onRepeatModeChange : (index : Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        items(repeatList.size){index ->
            val repeatMode = repeatList.get(index)
            Spacer(modifier = Modifier.height(25.dp))
            CustomRadioItem(
                title = repeatMode.first,
                isSelected = index == selectedIndex,
                isRepeatMode = true,
                onSelectedEvent = {
                    onRepeatModeChange(index)
                }
            )
        }
    }
}