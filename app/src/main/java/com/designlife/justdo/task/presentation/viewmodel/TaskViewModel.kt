package com.designlife.justdo.task.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.entities.Category
import com.designlife.justdo.common.domain.entities.Todo
import com.designlife.justdo.common.domain.repeat.RepeatRepository
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.TodoRepository
import com.designlife.justdo.common.utils.enums.RepeatType
import com.designlife.justdo.task.presentation.events.TaskEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class TaskViewModel(
    private val repeatRepository: RepeatRepository,
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _titleValue : MutableState<String> = mutableStateOf("")
    val titleValue = _titleValue

    private val _noteValue : MutableState<String> = mutableStateOf("")
    val noteValue = _noteValue

    private val _selectedDateText : MutableState<String> = mutableStateOf(IDateGenerator.getGracefullyDateFromDate(
        Date(System.currentTimeMillis())
    ))
    val selectedDateText = _selectedDateText

    private val _selectedTimeText : MutableState<String> = mutableStateOf(IDateGenerator.getGracefullyTimeFromEpoch(System.currentTimeMillis()))
    val selectedTimeText = _selectedTimeText

    // Date , Time
    private val _rawTaskDateTimeInstance : MutableState<Calendar> = mutableStateOf(Calendar.getInstance())
    val rawTaskDateTimeInstance = _rawTaskDateTimeInstance

    private val _progressBar : MutableState<Boolean> = mutableStateOf(false)
    val progressBar = _progressBar

    fun onEvent(event : TaskEvents){
        when(event){
            is TaskEvents.OnTitleChange -> {
                _titleValue.value = event.value
            }
            is TaskEvents.OnNoteChange -> {
                _noteValue.value = event.value
            }
            is TaskEvents.OnDateChange -> {
                val(day,month,year) = event.value.split("/")
                _selectedDateText.value = IDateGenerator.getGracefullyDateFrom(day.toInt(),month.toInt(),year.toInt())
                rawTaskDateTimeInstance.value.set(year.toInt(), month.toInt() - 1, day.toInt())
            }
            is TaskEvents.OnTimeChange -> {
                val(hour,minute) = event.value.split(":")
                _selectedTimeText.value = IDateGenerator.getGracefullyTimeFrom(hour.toInt(),minute.toInt())
                val calendar = Calendar.getInstance()
                _rawTaskDateTimeInstance.value.apply {
                    set(Calendar.HOUR_OF_DAY, hour.toInt())
                    set(Calendar.MINUTE, minute.toInt())
                }
            }
            is TaskEvents.CreateTask -> {
                createTask(event.repeatType,event.category)
            }
        }

    }

    private fun createTask(repeatType: RepeatType,category: Category) {
        _progressBar.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val todo = Todo(
                title = _titleValue.value,
                note = _noteValue.value,
                isRepeated = false,
                categoryId = category.id,
                date = _rawTaskDateTimeInstance.value.time,
                isCompleted = false
            )
            val taskList = repeatRepository.createRepeatedEvents(todo,repeatType,_rawTaskDateTimeInstance.value.time)
            val totalTodo = category.totalTodo + taskList.size
            categoryRepository.updateCategory(category.copy(totalTodo = totalTodo))
            todoRepository.insertAllTodo(taskList)
            _progressBar.value = false
        }
    }



}