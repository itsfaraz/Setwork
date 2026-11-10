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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private var _decPrevState = Triple<List<FlashCard>,String,Int>(emptyList(),"",0)

    private var _deckToggle : MutableState<Boolean> = mutableStateOf(false)
    val deckToggle  = _deckToggle

    private var _editState : MutableState<Boolean> = mutableStateOf(false)
    val editState  = _editState

    private val _updateCardsQueue : MutableState<MutableMap<Int,FlashCard>> = mutableStateOf(mutableMapOf())
    val updateCardsQueue = _updateCardsQueue

    private var _hasDeckModified : MutableState<Boolean> = mutableStateOf(false)
    val hasDeckModified  = _hasDeckModified

    private val _categoryList : MutableState<List<Category>> = mutableStateOf(mutableListOf())
    val categoryList = _categoryList

    private val _selectedCategoryIndex : MutableState<Int> = mutableStateOf(0)
    val selectedCategoryIndex = _selectedCategoryIndex

    private val _colorMap : MutableState<Map<Long, Color>> = mutableStateOf(mapOf())
    private val _themeColor : MutableState<Color> = mutableStateOf(ButtonPrimary.value)
    val themeColor = _themeColor
    private var _isUpdated : MutableState<Boolean> = mutableStateOf(false)

    private var _atomicWrite = MutableSharedFlow<Unit>()
    val atomicWrite  = _atomicWrite.asSharedFlow()

    private var _progressState : MutableState<Boolean> = mutableStateOf(false)
    val progressState  = _progressState


    fun onEvent(event : DeckEvents){
        when(event){
            is DeckEvents.OnHeaderChange -> {
                _headerTitle.value = event.value
            }
            is DeckEvents.OnCreateCard -> {
                _cardList.value.add(FlashCard())
                _deckToggle.value = true
            }
            is DeckEvents.OnCardRemove -> {
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
                archiveDeckById()
            }
            is DeckEvents.OnInsert -> {
                insertDeck()
            }
            is DeckEvents.OnUpdate -> {
                updateDeck()
            }
            is DeckEvents.OnClear -> {
                clean()
            }
            is DeckEvents.OnCategoryDemand -> {
                fetchCategories()
            }
            is DeckEvents.OnDeckDemand -> {
                fetchDeckById(event.id)
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

    private fun archiveDeckById() {
        if (_deckId.value != -1L){
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    deckRepository.archiveDeck(_deckId.value)
                }catch (e : Exception){
                    Log.e(TAG,"deleteDeckById: ${e.message}" )
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setupColor(index: Int) {
        viewModelScope.launch {
            categoryRepository.getAllCategory().firstOrNull()?.let {
                _categoryList.value = it
                _colorMap.value.get(_categoryList.value[index].id)?.let {
                    _themeColor.value = it
                }
            }
        }
    }

    private fun fetchDeckById(deckId : Long){
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

    private fun insertDeck() {
        if (_isUpdated.value) {
            updateDeck()
            return
        }
        _isUpdated.value = true
        _hasDeckModified.value = true
        _progressState.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val id = _categoryList.value
                .getOrNull(_selectedCategoryIndex.value)?.id
                ?: return@launch
            Deck(
                deckName = _headerTitle.value.ifEmpty { getFormattedTitle() },
                totalCards = _cardList.value.size,
                modifiedDate = Date(System.currentTimeMillis()),
                cards = _cardList.value,
                isArchive = false,
                categoryId = id
            ).also { newDeck ->
                deckRepository.insertDeck(newDeck)
            }
            withContext(Dispatchers.Main){
                _progressState.value = false
                _hasDeckModified.value = false
            }
        }
    }

    private fun getFormattedTitle(): String {
        return "Untitled -${IDateGenerator.getGracefullyTimeFromEpoch(System.currentTimeMillis())}"
    }

    private fun updateDeck() {
        if (!isDeckUpdated()) {
            return
        }
        _progressState.value = true
        _hasDeckModified.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val id = _categoryList.value
                .getOrNull(_selectedCategoryIndex.value)?.id
                ?: return@launch

            val updatedDeck = Deck(
                deckId = _deckId.value,
                deckName = _headerTitle.value,
                totalCards = _cardList.value.size,
                modifiedDate = Date(System.currentTimeMillis()),
                cards = _cardList.value,
                isArchive = false,
                categoryId = id
            )

            deckRepository.updateDeck(
                deckId = _deckId.value,
                deck = updatedDeck
            )

            withContext(Dispatchers.Main){
                _hasDeckModified.value = false
                _progressState.value = false
                _isUpdated.value = false
            }
        }
    }

    private fun isDeckUpdated(): Boolean {
        if (_cardList.value != _decPrevState.first) return true
        if (_headerTitle.value != _decPrevState.second) return true
        if (_selectedCategoryIndex.value != _decPrevState.third) return true
        return false
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategory().firstOrNull()?.let {
                if (it.isNotEmpty()){
                    withContext(Dispatchers.Main.immediate) {
                        Log.i("CATEGORY", "fetchCategories: ${it.size} ")
                        _categoryList.value = it
                        fillColorMap(it)
                    }
                }
            }
        }
    }

    private fun fillColorMap(categories: List<Category>) {
        val colorMap = mutableMapOf<Long,Color>()
        categories.forEach {
            colorMap.put(it.id,it.color)
        }
        _colorMap.value = colorMap
    }

    private fun clean() {
        _deckId.value = 0L
        _headerTitle.value = ""
        _modifiedTime.value = 0L
        _cardList.value = mutableListOf()
        _decPrevState = Triple<List<FlashCard>,String,Int>(emptyList(),"",-1)
        _deckToggle.value = false
        _editState.value = false
        _updateCardsQueue.value = mutableMapOf()
        _hasDeckModified.value = false
        _categoryList.value = mutableListOf()
        _selectedCategoryIndex.value = -1
        _colorMap.value = mapOf()
        _themeColor.value = ButtonPrimary.value
        _isUpdated.value = false
        onCleared()
    }
}