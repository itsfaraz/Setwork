package com.designlife.justdo.common.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Note(
    val noteId : Long = 0L,
    val title : String,
    val content : String,
    val emoji : String = "📓",
    val categoryId : Long,
    val coverImage : String = ""
)
