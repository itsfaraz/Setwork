package com.designlife.setwork.home.presentation.components

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.designlife.setwork.common.domain.entities.Note
import com.designlife.setwork.ui.theme.TaskItemLabelColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItemList(
    listState: LazyStaggeredGridState,
    noteList: List<Note>,
    colorMap: Map<Long, Color>,
    onSwipeRightEvent : () -> Unit,
    onSwipeLeftEvent : () -> Unit,
    onNoteClickEvent: (index: Int) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier
            .pointerInput(Unit) {
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
            }
            .padding(horizontal = 6.dp)
            .fillMaxSize(),
        verticalItemSpacing = 8.dp,
        columns = StaggeredGridCells.Fixed(2),
        state = listState
    ) {
        itemsIndexed(
            items = noteList.filter { !it.isArchive },
            contentType = { _, note -> note.noteId },
            key = { _, note -> note.noteId },
        ) { index, note ->
            if (colorMap.containsKey(note.categoryId)) {
                val color: Color = colorMap.get(note.categoryId) ?: TaskItemLabelColor.value
                NoteItem(
                    note = note,
                    noteTheme = color,
                    onClick = { onNoteClickEvent(index) }
                )
            }

        }
        item {
            Spacer(modifier = Modifier.height(350.dp))
        }
    }
}