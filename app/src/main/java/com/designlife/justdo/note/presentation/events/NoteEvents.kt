package com.designlife.justdo.note.presentation.events

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri

sealed class NoteEvents{
    data class OnTitleChange(val value : String) : NoteEvents()
    data class OnContentChange(val value : String) : NoteEvents()
//    data class OnEmojiChange(val value : String) : NoteEvents()
    data class OnCoverChange(val value : Bitmap) : NoteEvents()
    data class OnCategoryChange(val value : Long) : NoteEvents()
    data class OnCategoryIndexChange(val value : Int) : NoteEvents()
    data class OnDeleteNote(val context: Context) : NoteEvents()
}
