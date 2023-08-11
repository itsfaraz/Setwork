package com.designlife.justdo.home.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designlife.justdo.common.domain.calendar.DateGenerator
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.entities.Category
import com.designlife.justdo.common.domain.entities.Todo
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.TodoRepository
import com.designlife.justdo.home.domain.usecase.LoadIntialDatesUseCase
import com.designlife.justdo.home.domain.usecase.LoadNextDatesSetUseCase
import com.designlife.justdo.home.domain.usecase.LoadPreviousDatesSetUseCase
import com.designlife.justdo.home.presentation.events.HomeEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import kotlin.time.Duration.Companion.days

class HomeViewModel(
    private val dateGenerator: DateGenerator,
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository,
    private val loadInitialDateUseCase : LoadIntialDatesUseCase,
    private val loadNextDatesSetUseCase: LoadNextDatesSetUseCase,
    private val loadPreviousDatesSetUseCase: LoadPreviousDatesSetUseCase
) : ViewModel() {
    private val TAG = "TAG"

    private val _dateList : MutableState<List<Date>> = mutableStateOf(listOf());
    val dateList = _dateList

    private val _categoryList : MutableState<List<Category>> = mutableStateOf(listOf());
    val categoryList = _categoryList

    private val _todoList : MutableState<List<Todo>> = mutableStateOf(listOf());
    val todoList = _todoList

    private val _colorMap : MutableState<Map<Long, Color>> = mutableStateOf(mapOf());
    val colorMap = _colorMap

    private val _currentMonth : MutableState<String> = mutableStateOf(IDateGenerator.getMonthFromDate(Date(System.currentTimeMillis())))
    val currentMonth = _currentMonth

    private val _currentYear : MutableState<String> = mutableStateOf(IDateGenerator.getCurrentYear().toString())
    val currentYear = _currentYear

    private val _currentDate : MutableState<Date> = mutableStateOf(IDateGenerator.getToday())
    val currentDate = _currentDate

    private var _currentDateIndex : MutableState<Int> = mutableStateOf(0)
    val currentDateIndex  = _currentDateIndex

    private var _selectedIndex : MutableState<Int> = mutableStateOf(-1)
    val selectedIndex  = _selectedIndex

    private var _todoIndex : MutableState<Int> = mutableStateOf(0)
    val todoIndex  = _todoIndex

    private var _sheetVisibility : MutableState<Boolean> = mutableStateOf(false)
    val sheetVisibility  = _sheetVisibility

    private var _selectedCategoryIndex : MutableState<Int> = mutableStateOf(-1)
    val selectedCategoryIndex  = _selectedCategoryIndex

    private var todoSortedList = listOf<Todo>()
    private var todoUnSortedList =  listOf<Todo>()

    private var _isSorted : Boolean = false

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dateGenerator.getDateList().collect{
                _dateList.value = it
            }
        }
    }

    fun onEvent(event : HomeEvents){
        when(event){
            is HomeEvents.OnIndexSelected -> {
                _selectedIndex.value = event.index
            }
            is HomeEvents.OnTodoEvent -> {
                _todoIndex.value = event.index
            }
            is HomeEvents.HighlightTodoByDate -> {
                val calendar = Calendar.getInstance()
                calendar.time = _dateList.value[event.visibleIndex]
                val index = getTodoIndexByDate(calendar.time)
                _todoIndex.value = if (index >= 0) index else 0
            }
            is HomeEvents.HighlightDateByTodo -> {
                viewModelScope.launch(Dispatchers.Default) {
                    try{
                        val date = _todoList.value[event.visibleIndex].date
                        val dateEpoch = IDateGenerator.getEpochForDate(date)
                        val index = _dateList.value.indexOf(IDateGenerator.getDateFromDate(date))
                        if(index != -1){
                            _selectedIndex.value = index
                            _currentDateIndex.value = index
                        }
                        Log.d(TAG, "onEvent: Selected Index : ${_selectedIndex.value}")
                    }catch (e : Exception){
                        Log.e(TAG, "onEvent: $e")
                    }
                }
            }
            is HomeEvents.OnCategorySortSelected -> {
                _isSorted = !(_selectedCategoryIndex.value == event.categoryIndex)
                _selectedCategoryIndex.value = if (event.categoryIndex == _selectedCategoryIndex.value) -1 else event.categoryIndex
                applySortByCategory()
            }
        }
    }

    private fun applySortByCategory() {
        todoList.value = todoUnSortedList
        if (_isSorted){
            todoList.value = todoList.value.filter { it.categoryId == _categoryList.value[_selectedCategoryIndex.value].id}
        }
    }

    private fun searchDateIndex(list: List<Date>, date: Date): Int {
        var left = 0
        var right = list.size-1
        while (left < right){
            var mid = left + ((right-left)/2)
            val midEpoch = IDateGenerator.getEpochForDate(list[mid])
            val dateEpoch = IDateGenerator.getEpochForDate(date)
            if (midEpoch == dateEpoch){
                return mid
            }else if (midEpoch > dateEpoch){
                left = mid+1
            }else if (midEpoch < dateEpoch){
                right = mid-1
            }
        }
        return -1
    }

    public fun fetchDateDataByDate(index : Int){
        // call use case and perform operation
    }

    fun onMonthChange(date: Date) {
        _currentMonth.value = IDateGenerator.getMonthFromDate(date)
    }

    fun onYearChange(date: Date) {
        _currentYear.value = IDateGenerator.getYearFromDate(date)
    }


    fun loadInitialDates(){
        viewModelScope.launch(Dispatchers.Default) {
            loadInitialDateUseCase()
        }
    }

    fun loadNextMonth(){
        val list = mutableListOf<Date>()
        list.addAll(_dateList.value)
        list.addAll(loadNextDatesSetUseCase())
        setNextMonthIndex(list.size)
        _dateList.value = list
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
        _dateList.value = list
    }

    fun updateSheetVisibility(value : Boolean) {
        _sheetVisibility.value = value
    }

    fun fetchAllCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.getAllCategory().collect{
                _categoryList.value = it
                fillColorMap(it)
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

    fun fetchAllTodo() {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.getAllTodo().collect{
                val sortedList =  it.sortedBy { it.date }
                _todoList.value = sortedList
                todoUnSortedList = sortedList
                currentTodoIndex(sortedList)
            }
        }
    }

    private fun currentTodoIndex(todos: List<Todo>) {
        val calendar = Calendar.getInstance()
        calendar.time = _currentDate.value
        val index =  getTodoIndexByDate(calendar.time)
        _todoIndex.value = if (index-1 >= 0) index-1 else _todoIndex.value
    }

    private fun getTodoIndexByDate(selectedDate : Date): Int {
        var dateTodo : Todo? = null
        try {
            dateTodo = _todoList.value.filter { IDateGenerator.getFormattedDate(it.date).equals(IDateGenerator.getFormattedDate(selectedDate)) }.sortedBy { it.date }[0]
        }catch (e : Exception){
            Log.e(TAG, "getTodoIndexByDate: $e")
        }

        return _todoList.value.indexOf(dateTodo)
    }

    private fun todoSort(todos: List<Todo>): List<Todo> {
        return todos.sortedBy {
            it.date
        }
    }
}