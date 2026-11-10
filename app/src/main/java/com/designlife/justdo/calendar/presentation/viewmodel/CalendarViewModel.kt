package com.designlife.justdo.calendar.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designlife.justdo.calendar.data.dto.LocalEvent
import com.designlife.justdo.calendar.data.static.EventType
import com.designlife.justdo.calendar.presentation.events.CalendarEvents
import com.designlife.justdo.common.domain.calendar.DateGenerator
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.entities.Category
import com.designlife.justdo.common.domain.entities.Deck
import com.designlife.justdo.common.domain.entities.Note
import com.designlife.justdo.common.domain.entities.Todo
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.DeckRepository
import com.designlife.justdo.common.domain.repositories.NoteRepository
import com.designlife.justdo.common.domain.repositories.TodoRepository
import com.designlife.justdo.home.domain.usecase.LoadIntialDatesUseCase
import com.designlife.justdo.home.domain.usecase.LoadNextDatesSetUseCase
import com.designlife.justdo.home.domain.usecase.LoadPreviousDatesSetUseCase
import com.designlife.justdo.ui.theme.TaskItemLabelColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Date

class CalendarViewModel(
    private val dateGenerator: DateGenerator,
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository,
    private val noteRepository: NoteRepository,
    private val deckRepository: DeckRepository,
    private val loadInitialDateUseCase : LoadIntialDatesUseCase,
    private val loadNextDatesSetUseCase: LoadNextDatesSetUseCase,
    private val loadPreviousDatesSetUseCase: LoadPreviousDatesSetUseCase
) : ViewModel() {

    private var _progressBarVisibility : MutableState<Boolean> = mutableStateOf(false)
    val progressBarVisibility  = _progressBarVisibility

    private val _dateList : MutableState<List<Date>> = mutableStateOf(listOf());
    val dateList = _dateList

    private var _currentDateIndex : MutableState<Int> = mutableStateOf(0)
    val currentDateIndex  = _currentDateIndex

    private val _currentMonth : MutableState<String> = mutableStateOf(IDateGenerator.getMonthFromDate(Date(System.currentTimeMillis())))
    val currentMonth = _currentMonth

    private val _currentYear : MutableState<String> = mutableStateOf(IDateGenerator.getCurrentYear().toString())
    val currentYear = _currentYear

    private val _calendarList : MutableState<List<LocalEvent>> = mutableStateOf(emptyList());
    val calendarList = _calendarList

    private val _categoryList : MutableState<List<Category>> = mutableStateOf(listOf());
    val categoryList = _categoryList

    private val _colorMap : MutableState<Map<Long, Color>> = mutableStateOf(mapOf());

    val TAG = "CVM"

    init {
        viewModelScope.launch(Dispatchers.IO) {

            launch {
                dateGenerator.getDateList().collect {
                    _dateList.value = it.toOrderedUniqueList()
                }
            }

            launch {
                categoryRepository.getAllCategory().collect {
                    _categoryList.value = it
                    fillColorMap(it)
                }
            }

            launch {
                combine(
                    todoRepository.getAllTodo(),
                    noteRepository.getAllNotes(),
                    deckRepository.getAllDecks(),
                    categoryRepository.getAllCategory()
                ) { todos, notes, decks, categories ->

                    val colors = categories.associate {
                        it.id to it.color
                    }

                    buildList {

                        todos.forEach {
                            val color = colors[it.categoryId]
                                ?: TaskItemLabelColor.value

                            add(
                                LocalEvent(
                                    eventId = it.todoId.toLong(),
                                    eventInfo = LocalEvent.EventInfo(
                                        epoch = it.date.time,
                                        title = it.title,
                                        description = it.note,
                                        eventType = EventType.TASK,
                                        categoryColor = color
                                    )
                                )
                            )
                        }

                        notes.forEach {
                            val color = colors[it.categoryId]
                                ?: TaskItemLabelColor.value

                            add(
                                LocalEvent(
                                    eventId = it.noteId,
                                    eventInfo = LocalEvent.EventInfo(
                                        epoch = it.lastModified.time,
                                        title = it.title,
                                        description = it.content,
                                        eventType = EventType.NOTE,
                                        categoryColor = color
                                    )
                                )
                            )
                        }

                        decks.forEach {
                            val color = colors[it.categoryId]
                                ?: TaskItemLabelColor.value

                            add(
                                LocalEvent(
                                    eventId = it.deckId,
                                    eventInfo = LocalEvent.EventInfo(
                                        epoch = it.modifiedDate.time,
                                        title = it.deckName,
                                        description = "Open ${it.totalCards} cards in this deck",
                                        eventType = EventType.DECK,
                                        categoryColor = color
                                    )
                                )
                            )
                        }
                    }

                }.collect { events ->
                    _calendarList.value = emptyList()
                    _calendarList.value = events

                    Log.i(TAG, "Calendar events: ${events.size}")
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

    fun List<Date>.toOrderedUniqueList() : List<Date>{
        val set = LinkedHashSet<Date>()
        this.forEachIndexed { _, date -> set.add(date) }
        return set.toList()
    }

    fun onMonthChange(date: Date) {
        _currentMonth.value = IDateGenerator.getMonthFromDate(date)
    }

    fun onYearChange(date: Date) {
        _currentYear.value = IDateGenerator.getYearFromDate(date)
    }

    fun loadInitialDates(){
        viewModelScope.launch(Dispatchers.Main.immediate) {
            loadInitialDateUseCase()
        }
    }

    fun onEvent(event : CalendarEvents){
        when(event){
            is CalendarEvents.OnProgressBarToggle -> {
                _progressBarVisibility.value = event.toggleValue
            }
            else -> {}
        }
    }

    fun loadNextMonth(){
        val list = mutableListOf<Date>()
        list.addAll(_dateList.value)
        list.addAll(loadNextDatesSetUseCase())
        setNextMonthIndex(list.size)
        _dateList.value = list.toOrderedUniqueList()
    }

    private fun setNextMonthIndex(listSize : Int) {
        _currentDateIndex.value =  listSize-25
    }

    private fun setPreviousMonthIndex() {
        _currentDateIndex.value = 5
    }

    fun loadPreviousMonth(){
        val prevMonth = loadPreviousDatesSetUseCase()
        val list = ArrayList<Date>(prevMonth)
        list.addAll(_dateList.value)
        setPreviousMonthIndex()
        _dateList.value = list.toOrderedUniqueList()
    }


}