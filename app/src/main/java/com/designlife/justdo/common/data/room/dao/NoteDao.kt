package com.designlife.justdo.common.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.designlife.justdo.common.data.entities.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Transaction
    @Insert
    suspend fun insertNote(note: Note) : Long

    @Transaction
    @Query("SELECT * FROM NOTE")
    fun getAllNotes() : Flow<List<Note>>

    @Transaction
    @Query("SELECT * FROM NOTE WHERE noteId=:noteId")
    suspend fun getNoteById(noteId : Long) : Note

    @Transaction
    @Query("UPDATE NOTE SET title=:title ,content=:content ,emoji=:emoji ,categoryId=:categoryId ,coverImage=:coverImage WHERE noteId=:noteId")
    suspend fun updateNoteById(noteId : Long,title : String,content : String,emoji : String,categoryId : Long,coverImage : String)

    @Transaction
    @Query("DELETE FROM NOTE WHERE noteId=:noteId")
    suspend fun deleteNoteById(noteId : Long)

}