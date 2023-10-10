package com.designlife.justdo.deck.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.designlife.justdo.common.domain.entities.FlashCard
import kotlinx.coroutines.CoroutineScope

@Composable
fun PreviewCardListComponent(
    listState: LazyListState,
    cards: List<FlashCard>,
    visibleItemIndex : Int,
    onPreviewCardEvent : (index : Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        state = listState,
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        items(
            cards.size
        ) { index ->
            PreviewCardComponent(
                card = cards[index],
                isHighlighted = index == visibleItemIndex
            ){
                onPreviewCardEvent(index)
            }
        }
    }
}