package com.designlife.justdo.common.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNotes(notes: List<Note>)

    @Transaction
    @Query("SELECT * FROM NOTE")
    fun getAllNotes() : Flow<List<Note>>

    @Transaction
    @Query("SELECT * FROM NOTE WHERE noteId=:noteId")
    suspend fun getNoteById(noteId : Long) : Note

    @Transaction
    @Query("SELECT * FROM NOTE WHERE title=:title AND content=:description")
    suspend fun findNoteByTitleAndContent(title: String,description : String) : Note

    @Transaction
    @Query("UPDATE NOTE SET title=:title ,content=:content ,emoji=:emoji ,categoryId=:categoryId ,coverImage=:coverImage WHERE noteId=:noteId")
    suspend fun updateNoteById(noteId : Long,title : String,content : String,emoji : String,categoryId : Long,coverImage : ByteArray?)

    @Transaction
    @Query("UPDATE NOTE SET isArchive=:isArchive  WHERE noteId=:noteId")
    suspend fun archiveNoteById(noteId : Long, isArchive : Boolean = true)

    @Transaction
    @Query("DELETE FROM NOTE WHERE noteId=:noteId")
    suspend fun deleteNoteById(noteId : Long)

}