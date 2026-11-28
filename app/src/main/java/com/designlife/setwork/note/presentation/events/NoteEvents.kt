package com.designlife.setwork.note.presentation.events

import android.content.Context
import android.graphics.Bitmap


sealed class NoteEvents{
    data class OnTitleChange(val value : String) : NoteEvents()
    data class OnContentChange(val value : String) : NoteEvents()
//    data class OnEmojiChange(val value : String) : NoteEvents()
    data class OnCoverChange(val value : Bitmap) : NoteEvents()
    data class OnCategoryChange(val value : Long) : NoteEvents()
    data class OnCategoryIndexChange(val value : Int) : NoteEvents()
    data class OnDeleteNote(val context: Context) : NoteEvents()
    data class OnThreeDotToggle(val state : Boolean) : NoteEvents()
    data class OnPdfExport(val context : Context) : NoteEvents()
    data object OnDuplicateEvent : NoteEvents()
    data class OnPngExport(val context : Context) : NoteEvents()
    data object OnAIChatToggle : NoteEvents()
    data object OnReminderToggle : NoteEvents()
    data class OnDateChange(val value : String) : NoteEvents()
    data class OnTimeChange(val value : String) : NoteEvents()
    data class LoadNoteById(val id : Int) : NoteEvents()

}
