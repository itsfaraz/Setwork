package com.designlife.justdo.note.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designlife.justdo.common.domain.entities.Note
import com.designlife.justdo.common.domain.repositories.NoteRepository
import com.designlife.justdo.note.presentation.events.NoteEvents
import com.designlife.justdo.task.presentation.events.TaskEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _noteId : MutableState<Long> = mutableStateOf(0L)

    private val _titleValue : MutableState<String> = mutableStateOf("")
    val titleValue = _titleValue

    private val _contentValue : MutableState<String> = mutableStateOf("")
    val contentValue = _contentValue

    private val _categoryId : MutableState<Long> = mutableStateOf(0L)
    val categoryId = _categoryId

    private val _emojiValue : MutableState<String> = mutableStateOf("📓")
    val emojiValue = _emojiValue

    private val _coverImage : MutableState<String> = mutableStateOf("")
    val coverImage = _coverImage

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
            is NoteEvents.OnEmojiChange -> {
                if (!event.value.equals("📓"))
                    _emojiValue.value = event.value
            }
        }
    }

    fun insertNote(){
        viewModelScope.launch(Dispatchers.IO) {
            if (_contentValue.value.isNotEmpty()){
                noteRepository.insertTodo(Note(
                    title = _titleValue.value,
                    content = _contentValue.value,
                    categoryId = _categoryId.value,
                    emoji = _emojiValue.value,
                    coverImage = _coverImage.value
                ))
            }
        }
    }

}