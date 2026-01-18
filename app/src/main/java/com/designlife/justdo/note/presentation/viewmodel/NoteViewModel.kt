package com.designlife.justdo.note.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designlife.justdo.common.domain.entities.Category
import com.designlife.justdo.common.domain.entities.Note
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.NoteRepository
import com.designlife.justdo.common.utils.ImageConverter
import com.designlife.justdo.note.presentation.events.NoteEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date

class NoteViewModel(
    private val noteRepository: NoteRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val TAG : String = this.javaClass.simpleName

    private val _noteId : MutableState<Long> = mutableStateOf(0L)

    private val _titleValue : MutableState<String> = mutableStateOf("")
    val titleValue = _titleValue

    private val _contentValue : MutableState<String> = mutableStateOf("")
    val contentValue = _contentValue

    private val _categoryId : MutableState<Long> = mutableStateOf(0L)
    val categoryId = _categoryId

    private val _coverImage : MutableState<Bitmap?> = mutableStateOf(null)
    val coverImage = _coverImage

    private val _createdTime : MutableState<Long> = mutableStateOf(0L)
    val createdTime = _createdTime

    private val _categoryList : MutableState<List<Category>> = mutableStateOf(listOf());
    val categoryList = _categoryList

    private val _modifiedTime : MutableState<Long> = mutableStateOf(0L)
    val modifiedTime = _modifiedTime

    private val _selectedCategoryIndex : MutableState<Int> = mutableStateOf(-1);
    val selectedCategoryIndex = _selectedCategoryIndex

    private var _hasNoteModified : MutableState<Boolean> = mutableStateOf(false)
    val hasNoteModified  = _hasNoteModified

    private var _notePrevState = Triple<Note,Int,Bitmap?>(Note(),0,null)

    private val _progressBar : MutableState<Boolean> = mutableStateOf(false)
    val progressBar = _progressBar

    private val saveImageTimeMillis : Long = 200

    fun onEvent(event : NoteEvents){
        when(event){
            is NoteEvents.OnTitleChange -> {
                _titleValue.value = event.value
            }
            is NoteEvents.OnContentChange -> {
                _contentValue.value = event.value
            }
            is NoteEvents.OnCategoryChange -> {
                _categoryId.value = event.value
            }
            is NoteEvents.OnCoverChange -> {
               _coverImage.value = event.value
            }
            is NoteEvents.OnCategoryIndexChange -> {
                _selectedCategoryIndex.value = event.value
            }
            is NoteEvents.OnDeleteNote -> {
                deleteNoteById()
            }
        }
    }

    private fun deleteNoteById() {
        if (_noteId.value != -1L){
            try {
                viewModelScope.launch(Dispatchers.IO){
                    noteRepository.deleteNote(_noteId.value)
                }
            }catch (e : Exception){
                Log.e(TAG, "deleteNoteById: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun fetchNoteById(noteId : Long){
        _progressBar.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val note = noteRepository.getNoteById(noteId)
            note.also {
                _titleValue.value = it.title
                _contentValue.value = it.content
                _noteId.value = it.noteId
                _categoryId.value = it.categoryId
            }
            _coverImage.value = async { ImageConverter.getBitMapFromByteArray(note.coverImage) }.await()
            setNoteState(note)
            _progressBar.value = false
        }
    }

    private fun setNoteState(note: Note) {
        _notePrevState = Triple(note.copy(),_selectedCategoryIndex.value,_coverImage.value)
    }

    fun insertNote(){
        if (_contentValue.value.isNotEmpty()){
            Log.i("INSERT_FLOW", "insertNote: Condition Check True")
            _hasNoteModified.value = true
            _progressBar.value = true
            CoroutineScope((Dispatchers.IO)).launch {
                Log.i("INSERT_FLOW", "insertNote: Context Change")
                val coverImage = async { ImageConverter.getByteArrayFromBitMap(_coverImage.value) }.await()
                Log.i("INSERT_FLOW", "insertNote: Cover Image Converted ${coverImage}")
                noteRepository.insertNote(Note(
                    title = _titleValue.value,
                    content = _contentValue.value,
                    categoryId = _categoryList.value[_selectedCategoryIndex.value].id,
                    emoji = _categoryList.value[_selectedCategoryIndex.value].emoji,
                    coverImage = coverImage,
                    createdTime = Date(System.currentTimeMillis()),
                    lastModified = Date(System.currentTimeMillis())
                ))
                Log.i("INSERT_FLOW", "insertNote: Note Inserted")
                _progressBar.value = false
                _hasNoteModified.value = false
            }
        }
    }

    fun updateNote() {
        Log.i("UPDATE_FLOW", "updateNote: Before isNoteUpdated")
        if (isNoteUpdated()){
            Log.i("UPDATE_FLOW", "updateNote: After isNoteUpdated")
            _hasNoteModified.value = true
            _progressBar.value = true
            CoroutineScope((Dispatchers.IO)).launch {
                val coverImage = async {
                    ImageConverter.getByteArrayFromBitMap(if (_coverImage.value != null) _coverImage.value else _notePrevState.third)
                }.await()
                Log.i("UPDATE_FLOW", "updateNote: Category ${_categoryList.value[_selectedCategoryIndex.value].name}")
                val note = Note(
                    noteId = _noteId.value,
                    title = _titleValue.value,
                    content = _contentValue.value,
                    emoji =_categoryList.value[_selectedCategoryIndex.value].emoji,
                    categoryId = _categoryList.value[_selectedCategoryIndex.value].id,
                    coverImage = coverImage,
                    createdTime = Date(_createdTime.value),
                    lastModified = Date(System.currentTimeMillis())
                )
                noteRepository.updateNote(
                    _noteId.value,
                    note
                )
                _progressBar.value = false
                _hasNoteModified.value = false
            }
        }


    }

    private fun isNoteUpdated(): Boolean {
        if (_notePrevState.second != _selectedCategoryIndex.value)
            return true
        _notePrevState.first.also {
            if (_titleValue.value != it.title)
                return true
            if (_contentValue.value != it.content)
                return true
        }
        if (_notePrevState.third != _coverImage.value)
            return true
        return false
    }

    suspend fun fetchCategories(){
        categoryRepository.getAllCategory().firstOrNull()?.let {
            _categoryList.value = it
        }
    }
}