package com.designlife.justdo.home.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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

class HomeViewModel(
    private val dateGenerator: DateGenerator,
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository,
    private val loadInitialDateUseCase : LoadIntialDatesUseCase,
    private val loadNextDatesSetUseCase: LoadNextDatesSetUseCase,
    private val loadPreviousDatesSetUseCase: LoadPreviousDatesSetUseCase
) : ViewModel() {

    private val _dateList : MutableState<List<Date>> = mutableStateOf(listOf());
    val dateList = _dateList

    private val _categoryList : MutableState<List<Category>> = mutableStateOf(listOf());
    val categoryList = _categoryList

    private val _todoList : MutableState<List<Todo>> = mutableStateOf(listOf());
    val todoList = _todoList

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

    private var _sheetVisibility : MutableState<Boolean> = mutableStateOf(false)
    val sheetVisibility  = _sheetVisibility

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
        }
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
            }
        }
    }
    fun fetchAllTodo() {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.getAllTodo().collect{
                _todoList.value = it
            }
        }
    }
}