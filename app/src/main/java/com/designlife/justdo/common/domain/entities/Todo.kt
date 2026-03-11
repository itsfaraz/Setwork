package com.designlife.justdo.common.domain.entities

import java.util.Date

data class Todo(
    val todoId : Int,
    val title : String,
    val date : Date,
    val note : String,
    val categoryId : Long,
    val isRepeated : Boolean,
    val repeatIndex : Int,
    val isCompleted : Boolean,
    val createdOn : Long,
)
