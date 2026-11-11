package com.designlife.justdo.common.domain.converters

import com.designlife.justdo.common.domain.entities.Note
import java.util.Date

object NoteConverters {
    fun getNoteEntity(note: Note): com.designlife.justdo.common.data.entities.Note {
        return com.designlife.justdo.common.data.entities.Note(
            title = note.title,
            content = note.content,
            categoryId = note.categoryId,
            emoji = note.emoji,
            coverImage = note.coverImage,
            createdTime = note.createdTime.time,
            lastModified = note.lastModified.time,
            isArchive = note.isArchive
        )
    }

    fun getNote(note: com.designlife.justdo.common.data.entities.Note): Note {
        return Note(
            noteId = note.noteId,
            title = note.title,
            content = note.content,
            categoryId = note.categoryId,
            emoji = note.emoji,
            coverImage = note.coverImage,
            createdTime = Date(note.createdTime),
            lastModified = Date(note.lastModified),
            isArchive = note.isArchive
        )
    }

}