package com.designlife.setwork.common.data.entities

import com.designlife.setwork.common.domain.entities.Category
import com.designlife.setwork.common.domain.entities.Deck
import com.designlife.setwork.common.domain.entities.Note
import com.designlife.setwork.common.domain.entities.Todo

data class AppData(
    val todos : List<Todo>,
    val decks : List<Deck>,
    val notes : List<Note>,
    val categories : List<Category>,
    val createdAt : Long,
)
