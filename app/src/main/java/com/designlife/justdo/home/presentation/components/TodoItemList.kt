package com.designlife.justdo.home.presentation.components

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.designlife.justdo.common.domain.entities.Todo
import com.designlife.justdo.ui.theme.TaskItemLabelColor

@Composable
fun TodoItemList(
    listState : LazyListState,
    todoList : List<Todo>,
    colorMap : Map<Long,Color>,
    onFirstIndexChangeEvent : (index : Int) -> Unit,
    onTodoClickEvent : (todoId : Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ){
        items(
            items = todoList,
            key = {todo -> todo.date},
            contentType = {todo -> todo.categoryId}
        ){ todo->
            todo.let {item->
                if (colorMap.containsKey(item.categoryId)){
                    val color : Color = colorMap.get(item.categoryId) ?: TaskItemLabelColor
                    TodoItem(
                        color = color,
                        todo = item
                    ){
                        onTodoClickEvent(todo.todoId)
                    }
                    LaunchedEffect(listState.firstVisibleItemIndex){
                        onFirstIndexChangeEvent(listState.firstVisibleItemIndex)
                    }
                }
            }
        }
    }
}