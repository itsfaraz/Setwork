package com.designlife.justdo.task.presentation.viewmodel

import android.util.Log
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
import com.designlife.justdo.container.presentation.viewmodel.ContainerViewModel
import com.designlife.justdo.task.presentation.events.TaskEvents
import com.designlife.orchestrator.notification.repository.TaskNotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class TaskViewModel(
    private val repeatRepository: RepeatRepository,
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository,
    private val taskNotificationRepository: TaskNotificationRepository,
    private val shareViewModel: ContainerViewModel
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

    // Task Overview
    private val _isCompleted : MutableState<Boolean> = mutableStateOf(false)
    val isCompleted = _isCompleted

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
            is TaskEvents.OnProgressBarToggle -> {
                _progressBar.value = event.toggleValue
            }
            is TaskEvents.LoadTodoById -> {
                fetchTodoById(event.todoId)
            }
            is TaskEvents.MarkTaskDone -> {
                updateTodoDone(event.todoId)
            }
            is TaskEvents.DeleteTaskEvent -> {
                deleteTodoById(event.todoId)
            }
        }

    }

    private fun deleteTodoById(todoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.deleteTodo(todoId)
        }
    }

    private fun updateTodoDone(todoId: Int) {
        _progressBar.value = true
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.updateTodo(todoId)
            _isCompleted.value = true
            _progressBar.value = false
        }
    }

    private fun fetchTodoById(todoId: Int) {
        _progressBar.value = true
        viewModelScope.launch(Dispatchers.Main) {
            val todo = async(Dispatchers.IO) {  todoRepository.getTodoById(todoId) }.await()
            shareViewModel.setupRepeatList(todo.date)
            _titleValue.value = todo.title
            _noteValue.value = todo.note
            _selectedDateText.value
            _selectedDateText.value = IDateGenerator.getGracefullyDateFromDate(todo.date)
            _selectedTimeText.value = IDateGenerator.getGracefullyTimeFromEpoch(todo.date.time)
            shareViewModel.selectedCategory.value = shareViewModel.getCatiegoryIndexById(todo.categoryId)
            shareViewModel.selectedRepeatIndex.value = todo.repeatIndex
            _isCompleted.value = todo.isCompleted
            _progressBar.value = false
        }
    }

    private fun createTask(repeatType: RepeatType,category: Category) {
        _progressBar.value = true
        Log.i("ERROR_CHECK","createTask: start")
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("ERROR_CHECK","createTask: viewModelScope.launch")
            val todo = Todo(
                title = _titleValue.value,
                note = _noteValue.value,
                isRepeated = false,
                categoryId = category.id,
                date = _rawTaskDateTimeInstance.value.time,
                isCompleted = false,
                createdOn = System.currentTimeMillis(),
                todoId = 0,
                repeatIndex = shareViewModel.selectedRepeatIndex.value
            )
            Log.i("ERROR_CHECK","createTask: createRepeatedEvents")
            val taskList = repeatRepository.createRepeatedEvents(todo,repeatType,_rawTaskDateTimeInstance.value.time)
            val totalTodo = category.totalTodo + taskList.size
            Log.i("ERROR_CHECK","createTask:  categoryRepository.updateCategory")
            categoryRepository.updateCategory(category.copy(totalTodo = totalTodo))
            Log.i("ERROR_CHECK","createTask:  todoRepository.insertAllTodo")
            todoRepository.insertAllTodo(taskList)
            Log.i("ERROR_CHECK","createTask: setNotifications")
            setNotifications(taskList)
            _progressBar.value = false
        }
    }

    private fun setNotifications(taskList : List<Todo>){
        Log.i("ERROR_CHECK","setNotifications: setNotifications")
        val triplets = taskList.map { todo: Todo -> Triple(
            first = todo.date,
            second = todo.title,
            third = todo.todoId
        ) }
        Log.i("ERROR_CHECK","setNotifications: before taskNotificationRepository.scheduleNotification")
        taskNotificationRepository.scheduleNotification(triplets)
        Log.i("ERROR_CHECK","setNotifications: after taskNotificationRepository.scheduleNotification")
    }


}