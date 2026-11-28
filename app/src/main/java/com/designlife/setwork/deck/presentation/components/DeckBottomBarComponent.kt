package com.designlife.setwork.deck.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.designlife.setwork.R
import com.designlife.setwork.ui.theme.Shapes
import com.designlife.setwork.ui.theme.UIComponentBackground
import com.designlife.setwork.ui.theme.cutTopRoundedCorners

@Composable
fun DeckBottomBarComponent(
    listState: LazyListState,
    viewModeVisible: Boolean,
    onShowStackEvent: (value : Boolean) -> Unit,
    onNextCardEvent: (index : Int) -> Unit,
    onPreviousCardEvent: (index : Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(Shapes.cutTopRoundedCorners(15.dp))
            .background(UIComponentBackground.value),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (viewModeVisible) {
            IconButton(
                modifier = Modifier.weight(1F),
                onClick = { onPreviousCardEvent(listState.firstVisibleItemIndex) }) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = R.drawable.ic_bottom_arrow_left),
                    contentDescription = "Left Arrow Icon",
                    tint = Color.Gray
                )
            }
        }
        if (viewModeVisible){
            IconButton(
                modifier = Modifier.weight(1F),
                onClick = { onShowStackEvent(!viewModeVisible) }) {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(id = R.drawable.ic_bottom_stack),
                    contentDescription = "Stack Card Icon",
                    tint = Color.Gray
                )
            }
        }

        if (viewModeVisible) {
            IconButton(
                modifier = Modifier.weight(1F),
                onClick = {
                    onNextCardEvent(listState.firstVisibleItemIndex)
                }
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = R.drawable.ic_bottom_arrow_right),
                    contentDescription = "Right Arrow Icon",
                    tint = Color.Gray
                )
            }
        }
    }
}