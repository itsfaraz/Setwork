package com.designlife.justdo.home.presentation.components

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.designlife.justdo.common.domain.entities.Deck
import com.designlife.justdo.ui.theme.TaskItemLabelColor

@Composable
fun DeckItemList(
    listState: LazyListState,
    deckList: List<Deck>,
    colorMap: Map<Long, Color>,
    onSwipeRightEvent : () -> Unit,
    onSwipeLeftEvent : () -> Unit,
    onDeckClickEvent: (index: Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .pointerInput(Unit){
                detectHorizontalDragGestures(
                    onDragEnd = {
                        // gesture finished
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        if (dragAmount > 0) {
                            onSwipeLeftEvent()
                        } else {
                            onSwipeRightEvent()
                        }
                    }
                )
            }.fillMaxSize(),
        state = listState
    ) {
        itemsIndexed(
            items = deckList.filter { !it.isArchive },
            contentType = { _, deck -> deck.categoryId },
            key = { _, deck -> deck.deckId },
        ) { index, deck ->
            deck.let { item ->
                if (colorMap.containsKey(item.categoryId)) {
                    val color: Color = colorMap.get(item.categoryId) ?: TaskItemLabelColor.value
                    DeckItem(
                        deck = item,
                        deckTheme = color
                    ) {
                        onDeckClickEvent(index)
                    }
                }
            }
        }
        item{
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}