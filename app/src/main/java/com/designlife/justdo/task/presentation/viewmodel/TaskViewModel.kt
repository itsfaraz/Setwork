package com.designlife.justdo.task.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.entities.Category
import com.designlife.justdo.common.domain.entities.RawTodo
import com.designlife.justdo.common.domain.entities.Todo
import com.designlife.justdo.common.domain.repeat.RepeatRepository
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.TodoCategoryRepository
import com.designlife.justdo.common.domain.repositories.TodoRepository
import com.designlife.justdo.common.utils.enums.RepeatType
import com.designlife.justdo.container.presentation.viewmodel.ContainerViewModel
import com.designlife.justdo.task.presentation.events.TaskEvents
import com.designlife.orchestrator.notification.data.NotificationInfo
import com.designlife.orchestrator.notification.repository.TaskNotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class TaskViewModel(
    private val repeatRepository: RepeatRepository,
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository,
    private val todoCategoryRepository: TodoCategoryRepository,
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

    private val _deleteTaskPopup : MutableState<Boolean> = mutableStateOf(false)
    val deleteTaskPopup = _deleteTaskPopup

    private val _rawTodos = MutableStateFlow<List<RawTodo>>(emptyList())
    private val _categories = MutableStateFlow<List<Category>>(emptyList())

    // Task Overview
    private val _isCompleted : MutableState<Boolean> = mutableStateOf(false)
    val isCompleted = _isCompleted

    private val mutex = Mutex()
    init {
        viewModelScope.launch{
            todoRepository.getAllRawTodo().collect { todos ->
                _rawTodos.value = todos
            }

            categoryRepository.getAllCategory().collect { categories ->
                _categories.value = categories
            }
        }
    }

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
            is TaskEvents.DeleteTaskPopup -> {
//                deleteTodoById(event.todoId)
                _deleteTaskPopup.value = event.state
            }
            is TaskEvents.DeleteTasksEvent -> {
                if (event.taskId == -1) return
                _progressBar.value = true
                viewModelScope.launch(Dispatchers.IO) {
                    if (event.allOccurrence){
                        deleteAllTodoOccurrenceById(event.taskId)
                    }else{
                        deleteTodoById(event.taskId)
                    }
                    withContext(Dispatchers.Main.immediate){
                        _progressBar.value = false
                    }
                }
            }
        }

    }

    @Transaction
    private suspend fun deleteAllTodoOccurrenceById(taskId: Int): Unit {
        Log.i("DATA_CHECK", "deleteAllTodoOccurrenceById: Raw Todo Data List ${_rawTodos.value.size}")
        if (taskId != -1){
            val rawTodo = todoRepository.getRawTodoById(taskId)
            Log.i("DATA_CHECK", "deleteAllTodoOccurrenceById: Raw Todo Date : ${rawTodo.date} & CreatedOn : ${rawTodo.createdOn}")
            _rawTodos
                .value
                .filter { todo ->
                    Log.i("DATA_CHECK", "deleteAllTodoOccurrenceById: Each Raw Todo Date : ${todo.date} & CreatedOn : ${todo.createdOn}")
                    rawTodo.date.equals(todo.date) || rawTodo.createdOn.equals(todo.createdOn)
                }.forEachIndexed { index, todo ->
                    todo?.let {
                        deleteTodoById(todo.todoId.toInt())
                        Log.i("DATA_CHECK", "deleteTodoById: delete index : ${index} with db call")
                    }
                }
        }
    }
    private suspend fun deleteTodoById(todoId: Int) : Boolean {
        mutex.withLock {
                val (todo,category) = todoCategoryRepository.getTodoById(todoId = todoId)
                todo?.let { todo ->
                    category?.let { category ->
                        if (todo.isCompleted){
                            val updatedCategory = category.copy(totalTodo = category.totalTodo - 1, totalCompleted = category.totalCompleted - 1)
                            Log.i("CATEGORY", "deleteAllTodoOccurrenceById: ${updatedCategory}")
                            todoCategoryRepository.deleteTodoById(todoId = todoId,category = updatedCategory)
                        }else{
                            val updatedCategory = category.copy(totalTodo = category.totalTodo - 1)
                            Log.i("CATEGORY", "deleteAllTodoOccurrenceById: ${updatedCategory}")
                            todoCategoryRepository.deleteTodoById(todoId = todoId,category = updatedCategory)
                        }
                    }
                }
                return true
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
        val notificationInfoData = taskList.map { todo: Todo -> NotificationInfo(
            taskTitle = todo.title,
            taskSubTitle = (if (todo.note.isEmpty()) "" else if(todo.note.length > 25) "${todo.note.substring(0,25)} ..." else todo.note),
            date = todo.date ,
            taskId = todo.todoId
        ) }
        Log.i("ERROR_CHECK","setNotifications: before taskNotificationRepository.scheduleNotification")
        taskNotificationRepository.scheduleNotification(notificationInfoData)
        Log.i("ERROR_CHECK","setNotifications: after taskNotificationRepository.scheduleNotification")
    }


}