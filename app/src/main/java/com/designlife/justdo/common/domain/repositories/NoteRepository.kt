package com.designlife.justdo.common.domain.repositories

import com.designlife.justdo.common.data.room.dao.NoteDao
import com.designlife.justdo.common.domain.converters.NoteConverters
import com.designlife.justdo.common.domain.entities.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepository(private val noteDao: NoteDao) {

    suspend fun insertTodo(note: Note) : Long{
        return noteDao.insertNote(NoteConverters.getNoteEntity(note))
    }

    suspend fun getAllNotes() : Flow<List<Note>> {
       return noteDao.getAllNotes().map { rawNoteList ->
           rawNoteList.map{ rawNote ->
               NoteConverters.getNote(rawNote)
           }
       }
    }

    suspend fun getNoteById(noteId : Long) : Note {
        return NoteConverters.getNote(noteDao.getNoteById(noteId))
    }

    suspend fun updateNote(noteId: Long, note: Note) : Unit {
        noteDao.updateNoteById(
            noteId = noteId,
            title = note.title,
            content = note.content,
            emoji = note.emoji,
            categoryId = note.categoryId,
            coverImage = note.coverImage
        )
    }

    suspend fun deleteNote(noteId : Long) : Unit {
        noteDao.deleteNoteById(noteId)
    }

}