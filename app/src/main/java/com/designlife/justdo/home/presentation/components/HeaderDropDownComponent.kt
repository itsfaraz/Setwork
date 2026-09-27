package com.designlife.justdo.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.designlife.justdo.common.utils.enums.ViewType
import com.designlife.justdo.ui.theme.TypographyColor

@Composable
fun SelectedHeaderTitle(
    viewType: ViewType
) {
    val typeList = listOf<String>("All Tasks", "All Notes", "All Decks", "Settings", "Calendar")
    Row(
        modifier = Modifier
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(text = getViewTypeText(viewType, typeList), color = TypographyColor.value)
    }
}

fun getViewTypeText(viewType: ViewType, typeList: List<String>): String {
    return when (viewType) {
        ViewType.TASK -> typeList[0]
        ViewType.NOTE -> typeList[1]
        ViewType.DECK -> typeList[2]
        ViewType.SETTING -> typeList[3]
        ViewType.CALENDAR -> typeList[4]
        ViewType.UNKNOWN -> typeList[0]
    }
}
