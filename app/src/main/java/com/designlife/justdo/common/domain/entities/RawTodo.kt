package com.designlife.justdo.common.domain.entities

data class RawTodo(
    val todoId : Long = 0L,
    val title : String = "",
    val date : Long = 0L,
    val note : String = "",
    val categoryId : Long = 0L,
    val isRepeated : Boolean = false,
    val repeatIndex : Int = 0,
    val isCompleted : Boolean = false,
    val createdOn : Long = 0L
)
