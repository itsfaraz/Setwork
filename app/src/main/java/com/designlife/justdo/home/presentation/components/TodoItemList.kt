package com.designlife.justdo.home.presentation.components

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.designlife.justdo.common.domain.entities.Todo
import com.designlife.justdo.ui.theme.TaskItemLabelColor

@Composable
fun TodoItemList(
    listState: LazyListState,
    todoList: List<Todo>,
    colorMap: Map<Long, Color>,
    onSwipeRightEvent : () -> Unit,
    onSwipeLeftEvent : () -> Unit,
    onFirstIndexChangeEvent: (index: Int) -> Unit,
    onTodoClickEvent: (todoId: Int) -> Unit,

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
            }
            .fillMaxSize(),
        state = listState
    ) {
        items(
            items = todoList.filterNot { it.isArchive },
            key = { todo -> todo.todoId },
            contentType = { todo -> todo.categoryId }
        ) { todo ->
            todo.let { item ->
                if (colorMap.containsKey(item.categoryId)) {
                    val color: Color = colorMap.get(item.categoryId) ?: TaskItemLabelColor.value
                    TodoItem(
                        color = color,
                        todo = item
                    ) {
                        onTodoClickEvent(todo.todoId)
                    }
                    LaunchedEffect(listState.firstVisibleItemIndex) {
                        onFirstIndexChangeEvent(listState.firstVisibleItemIndex)
                    }
                }
            }
        }
        item{
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}