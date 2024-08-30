package com.designlife.justdo.deck.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.entities.Category
import com.designlife.justdo.common.domain.entities.Deck
import com.designlife.justdo.common.domain.entities.FlashCard
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.DeckRepository
import com.designlife.justdo.deck.presentation.events.DeckEvents
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.PrimaryColor1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date

class DeckViewModel(
    private val deckRepository: DeckRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val TAG = "DeckViewModel"

    private val _deckId : MutableState<Long> = mutableStateOf(0L)

    private val _headerTitle : MutableState<String> = mutableStateOf("")
    val headerTitle = _headerTitle

    private val _modifiedTime : MutableState<Long> = mutableStateOf(0L)
    val modifiedTime = _modifiedTime

    private val _cardList : MutableState<MutableList<FlashCard>> = mutableStateOf(mutableListOf());
    val cardList = _cardList

    private var _decPrevState = Triple<List<FlashCard>,String,Int>(emptyList(),"",-1)

    private var _deckToggle : MutableState<Boolean> = mutableStateOf(false)
    val deckToggle  = _deckToggle

    private var _editState : MutableState<Boolean> = mutableStateOf(false)
    val editState  = _editState

    private val _updateCardsQueue : MutableState<MutableMap<Int,FlashCard>> = mutableStateOf(mutableMapOf());
    val updateCardsQueue = _updateCardsQueue

    private var _hasDeckModified : MutableState<Boolean> = mutableStateOf(false)
    val hasDeckModified  = _hasDeckModified

    private val _categoryList : MutableState<List<Category>> = mutableStateOf(listOf());
    val categoryList = _categoryList

    private val _selectedCategoryIndex : MutableState<Int> = mutableStateOf(-1);
    val selectedCategoryIndex = _selectedCategoryIndex

    private val _colorMap : MutableState<Map<Long, Color>> = mutableStateOf(mapOf());
    private val _themeColor : MutableState<Color> = mutableStateOf(ButtonPrimary.value);
    val themeColor = _themeColor

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
            }
            is DeckEvents.OnPersistCardChanges -> {

            }
            is DeckEvents.OnCategoryIndexChange -> {
                _selectedCategoryIndex.value = event.value
                setupColor(event.value)
            }
            is DeckEvents.OnDeckDeleteEvent -> {
                deleteDeckById()
            }
        }
    }

    private fun deleteDeckById() {
        if (_deckId.value != -1L){
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    deckRepository.deleteDeck(_deckId.value)
                }catch (e : Exception){
                    Log.e(TAG,"deleteDeckById: ${e.message}" )
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setupColor(index: Int) {
        _colorMap.value.get(_categoryList.value[index].id)?.let {
            _themeColor.value = it
        }
    }

    fun fetchDeckById(deckId : Long){
        _deckId.value = deckId
        viewModelScope.launch(Dispatchers.IO) {
            val deck = deckRepository.getDeckById(_deckId.value)
            deck.also {
                _headerTitle.value = it.deckName
                _cardList.value.addAll(it.cards)
                _modifiedTime.value = it.modifiedDate.time
            }
            _decPrevState = Triple(deck.cards,deck.deckName,_selectedCategoryIndex.value)
        }
    }

    fun insertDeck() {
        if (_cardList.value.isNotEmpty()){
            _hasDeckModified.value = true
            viewModelScope.launch(Dispatchers.IO) {
                val newDeck = Deck(
                    deckName = if (_headerTitle.value.isEmpty()) getFormattedTitle() else _headerTitle.value,
                    totalCards = _cardList.value.size,
                    modifiedDate = Date(System.currentTimeMillis()),
                    cards = _cardList.value,
                    categoryId = _categoryList.value[_selectedCategoryIndex.value].id
                )
                deckRepository.insertDeck(newDeck)
                _hasDeckModified.value = false
            }
        }
    }

    private fun getFormattedTitle(): String {
        return "Untitled -${IDateGenerator.getGracefullyTimeFromEpoch(System.currentTimeMillis())}"
    }

    fun updateDeck() {
        if (isDeckUpdated()){
            _hasDeckModified.value = true
            val newDeck = Deck(
                deckId = _deckId.value,
                deckName = _headerTitle.value,
                totalCards = _cardList.value.size,
                modifiedDate = Date(System.currentTimeMillis()),
                cards = _cardList.value,
                categoryId = _categoryList.value[_selectedCategoryIndex.value].id
            )
            viewModelScope.launch(Dispatchers.IO) {
                deckRepository.updateDeck(
                    deckId = _deckId.value,
                    deck = newDeck
                )
                _hasDeckModified.value = false
            }
        }
    }

    private fun isDeckUpdated(): Boolean {
        if (_cardList.value != _decPrevState.first)
            return true
        if (_headerTitle.value != _decPrevState.second)
            return true
        if (_selectedCategoryIndex.value != _decPrevState.third)
            return true
        return false
    }

    suspend fun fetchCategories(){
        categoryRepository.getAllCategory().firstOrNull()?.let {
            _categoryList.value = it
            fillColorMap(it)
        }
    }

    private fun fillColorMap(categories: List<Category>) {
        val colorMap = mutableMapOf<Long,Color>()
        categories.forEach {
            colorMap.put(it.id,it.color)
        }
        _colorMap.value = colorMap
    }

}