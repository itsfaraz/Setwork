package com.designlife.justdo.note.presentation.events

sealed class NoteEvents{
    data class OnTitleChange(val value : String) : NoteEvents()
    data class OnContentChange(val value : String) : NoteEvents()
    data class OnEmojiChange(val value : String) : NoteEvents()
    data class OnCoverChange(val value : String) : NoteEvents()
    data class OnCategoryChange(val value : Long) : NoteEvents()
}
