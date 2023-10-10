package com.designlife.justdo.deck.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designlife.justdo.common.domain.entities.Deck
import com.designlife.justdo.common.domain.entities.FlashCard
import com.designlife.justdo.common.domain.entities.Note
import com.designlife.justdo.common.domain.repositories.DeckRepository
import com.designlife.justdo.deck.presentation.events.DeckEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class DeckViewModel(
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val _deckId : MutableState<Long> = mutableStateOf(0L)

    private val _headerTitle : MutableState<String> = mutableStateOf("")
    val headerTitle = _headerTitle

    private val _modifiedTime : MutableState<Long> = mutableStateOf(0L)
    val modifiedTime = _modifiedTime

    private val _cardList : MutableState<MutableList<FlashCard>> = mutableStateOf(mutableListOf());
    val cardList = _cardList

    private var _deckToggle : MutableState<Boolean> = mutableStateOf(false)
    val deckToggle  = _deckToggle

    private var _editState : MutableState<Boolean> = mutableStateOf(false)
    val editState  = _editState

    private val _updateCardsQueue : MutableState<MutableMap<Int,FlashCard>> = mutableStateOf(mutableMapOf());
    val updateCardsQueue = _updateCardsQueue

    fun onEvent(event : DeckEvents){
        when(event){
            is DeckEvents.OnHeaderChange -> {
                _headerTitle.value = event.value
            }
            is DeckEvents.OnCreateCard -> {
//                val cardList = _cardList.value.toMutableList()
//                cardList.add(FlashCard())
                _cardList.value.add(FlashCard())
                _deckToggle.value = true
            }
            is DeckEvents.OnCardRemove -> {
//                val cardList = _cardList.value.toMutableList()
//                cardList.removeAt(event.index)
                if (event.index >= 0 && _cardList.value.size >= 1){
                    _cardList.value.removeAt(event.index)
                }
            }
            is DeckEvents.OnDeckToggle -> {
                if (_cardList.value.isNotEmpty()){
                    _deckToggle.value = !_deckToggle.value
                }else{
                    _deckToggle.value = false
                }
            }
            is DeckEvents.OnEditStateChange -> {
                _editState.value = event.editState
            }
            is DeckEvents.OnUpdateCardChange -> {
                _cardList.value.set(event.index,event.card)
//                _updateCardsQueue.value.forEach { index, flashCard ->
//                    _cardList.value.set(index,flashCard)
//                }
//                _updateCardsQueue.value.clear()
            }
            is DeckEvents.OnPersistCardChanges -> {

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

    fun updateDeck() {
        viewModelScope.launch(Dispatchers.IO) {
            deckRepository.insertDeck(Deck(
                deckName = _headerTitle.value,
                totalCards = _cardList.value.size,
                modifiedDate = Date(System.currentTimeMillis()),
                cards = _cardList.value,
                categoryId = 0L
            ))
        }
    }

}