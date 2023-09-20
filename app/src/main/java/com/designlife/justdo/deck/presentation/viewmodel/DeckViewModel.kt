package com.designlife.justdo.deck.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.designlife.justdo.common.domain.entities.FlashCard
import com.designlife.justdo.common.domain.entities.Note
import com.designlife.justdo.common.domain.repositories.DeckRepository
import com.designlife.justdo.deck.presentation.events.DeckEvents

class DeckViewModel(
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val _deckId : MutableState<Long> = mutableStateOf(0L)

    private val _headerTitle : MutableState<String> = mutableStateOf("")
    val headerTitle = _headerTitle

    private val _modifiedTime : MutableState<Long> = mutableStateOf(0L)
    val modifiedTime = _modifiedTime

    private val _cardList : MutableState<List<FlashCard>> = mutableStateOf(listOf());
    val cardList = _cardList

    private var _deckToggle : MutableState<Boolean> = mutableStateOf(false)
    val deckToggle  = _deckToggle


    fun onEvent(event : DeckEvents){
        when(event){
            is DeckEvents.OnHeaderChange -> {
                _headerTitle.value = event.value
            }
            is DeckEvents.OnCreateCard -> {
                val cardList = _cardList.value.toMutableList()
                cardList.add(FlashCard())
                _cardList.value = cardList
            }
            is DeckEvents.OnCardRemove -> {
                val cardList = _cardList.value.toMutableList()
                cardList.removeAt(event.index)
                _cardList.value = cardList
            }
            is DeckEvents.OnDeckToggle -> {
                if (_cardList.value.isNotEmpty()){
                    _deckToggle.value = !_deckToggle.value
                }else{
                    _deckToggle.value = false
                }
            }
        }
    }

    fun fetchNoteById(noteId : Long){
//        viewModelScope.launch(Dispatchers.IO) {
//            val note = noteRepository.getNoteById(noteId)
//            note.also {
//                _titleValue.value = it.title
//                _contentValue.value = it.content
//                _noteId.value = it.noteId
//                _categoryId.value = it.categoryId
//                _coverImage.value = it.coverImage
//                _emojiValue.value = it.emoji
//            }
//        }
    }

    fun insertNote(){
//        viewModelScope.launch(Dispatchers.IO) {
//            if (_contentValue.value.isNotEmpty()){
//                noteRepository.insertTodo(Note(
//                    title = _titleValue.value,
//                    content = _contentValue.value,
//                    categoryId = _categoryId.value,
//                    emoji = _emojiValue.value,
//                    coverImage = _coverImage.value,
//                    createdTime = Date(System.currentTimeMillis()),
//                    lastModified = Date(System.currentTimeMillis())
//                ))
//            }
//        }
    }

    fun updateNote() {
//        viewModelScope.launch(Dispatchers.IO) {
//            val note = Note(
//                noteId = _noteId.value,
//                title = _titleValue.value,
//                content = _contentValue.value,
//                emoji = _emojiValue.value,
//                categoryId = _categoryId.value,
//                coverImage = _coverImage.value,
//                createdTime = Date(_createdTime.value),
//                lastModified = Date(System.currentTimeMillis())
//            )
//            noteRepository.updateNote(_noteId.value,note)
//        }
    }

}