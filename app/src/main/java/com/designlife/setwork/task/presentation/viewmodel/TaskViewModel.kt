package com.designlife.setwork.task.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.designlife.setwork.common.domain.calendar.IDateGenerator
import com.designlife.setwork.common.domain.entities.Category
import com.designlife.setwork.common.domain.entities.RawTodo
import com.designlife.setwork.common.domain.entities.Todo
import com.designlife.setwork.common.domain.repeat.RepeatRepository
import com.designlife.setwork.common.domain.repositories.CategoryRepository
import com.designlife.setwork.common.domain.repositories.TodoCategoryRepository
import com.designlife.setwork.common.domain.repositories.TodoRepository
import com.designlife.setwork.common.utils.enums.RepeatType
import com.designlife.setwork.container.presentation.viewmodel.ContainerViewModel
import com.designlife.setwork.task.presentation.events.TaskEvents
import com.designlife.justdo_provider.SetworkProvider
import com.designlife.justdo_provider.data.ProviderTask
import com.designlife.orchestrator.NotificationScheduler
import com.designlife.orchestrator.data.NotificationInfo
import com.designlife.orchestrator.data.NotificationStatus
import com.designlife.orchestrator.data.NotificationType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.lang.System
import java.util.Calendar
import java.util.Date
import kotlin.math.absoluteValue

class TaskViewModel(
    private val setworkProvider: SetworkProvider,
    private val repeatRepository: RepeatRepository,
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository,
    private val todoCategoryRepository: TodoCategoryRepository,
    private val notificationScheduler: NotificationScheduler,
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
                        archiveAllTodoOccurrenceById(event.taskId)
                    }else{
                        archiveTodoById(event.taskId)
                    }
                    withContext(Dispatchers.Main.immediate){
                        _progressBar.value = false
                    }
                }
            }
            is TaskEvents.DeletePopToggleOff -> {
                _deleteTaskPopup.value = false
            }
            is TaskEvents.OnCategoryIndexChange -> {

            }
        }

    }

    @Transaction
    private suspend fun archiveAllTodoOccurrenceById(taskId: Int) {
        if (taskId != -1){
            val rawTodo = todoRepository.getRawTodoById(taskId)
            _rawTodos
                .value
                .filter { todo ->
                    rawTodo.date.equals(todo.date) || rawTodo.createdOn.equals(todo.createdOn)
                }.forEachIndexed { _, todo ->
                    todo.let {
                        archiveTodoById(todo.todoId.toInt())
                    }
                }
        }
    }

    @Transaction
    private suspend fun deleteAllTodoOccurrenceById(taskId: Int) {
        if (taskId != -1){
            val rawTodo = todoRepository.getRawTodoById(taskId)
            _rawTodos
                .value
                .filter { todo ->
                    rawTodo.date.equals(todo.date) || rawTodo.createdOn.equals(todo.createdOn)
                }.forEachIndexed { _, todo ->
                    todo.let {
                        deleteTodoById(todo.todoId.toInt())
                    }
                }
        }
    }


    private suspend fun archiveTodoById(todoId : Int) : Boolean {
        try {
            todoRepository.archiveTodo(todoId)
        }catch (e : Exception){
            e.printStackTrace()
            return false
        }
        return true
    }

    @SuppressWarnings
    private suspend fun deleteTodoById(todoId: Int) : Boolean {
            try {
                mutex.withLock {
                    val (todo,category) = todoCategoryRepository.getTodoById(todoId = todoId)
                    todo.let { todoTask ->
                        category.let { category ->
                            if (todoTask.isCompleted){
                                val updatedCategory = category.copy(totalTodo = category.totalTodo - 1, totalCompleted = category.totalCompleted - 1)
                                todoCategoryRepository.deleteTodoById(todoId = todoId,category = updatedCategory)
                            }else{
                                val updatedCategory = category.copy(totalTodo = category.totalTodo - 1)
                                todoCategoryRepository.deleteTodoById(todoId = todoId,category = updatedCategory)
                            }
                            setworkProvider.removeTask(ProviderTask(id = todoId.toLong()))
                        }
                    }
                    return true
                }
            }catch (e : Exception){
                e.printStackTrace()
                return false
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
        viewModelScope.launch(Dispatchers.IO) {
            val todo = Todo(
                title = _titleValue.value,
                note = _noteValue.value,
                isRepeated = false,
                categoryId = category.id,
                date = _rawTaskDateTimeInstance.value.time,
                isCompleted = false,
                createdOn = System.currentTimeMillis(),
                todoId = System.currentTimeMillis().hashCode().absoluteValue,
                repeatIndex = shareViewModel.selectedRepeatIndex.value,
                isArchive = false
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
            setworkProvider.addTask(ProviderTask(
                id = todo.todoId.toLong(),
                title = todo.title,
                color = category.color,
                description = todo.note,
                completed = false,
                time = todo.date.time,
            ))
            _progressBar.value = false
        }
    }

    private fun setNotifications(taskList : List<Todo>) {
        Log.i("ERROR_CHECK","setNotifications: setNotifications")
        val notificationInfoData = taskList.map { todo: Todo ->
            NotificationInfo(
                taskTitle = todo.title,
                taskSubTitle = (if (todo.note.isEmpty()) "" else if (todo.note.length > 25) "${
                    todo.note.substring(
                        0,
                        25
                    )
                } ..." else todo.note),
                scheduledTime = todo.date.time,
                createdTime = System.currentTimeMillis(),
                deliveredTime = 0L,
                notificationType = NotificationType.TASK_NOTIFY,
                notificationStatus = NotificationStatus.ACTIVE,
                taskId = todo.todoId
            )
        }
        Log.i("ERROR_CHECK","setNotifications: before taskNotificationRepository.scheduleNotification")
        notificationScheduler.scheduleBulkNotification(notificationInfoData)
        Log.i("ERROR_CHECK","setNotifications: after taskNotificationRepository.scheduleNotification")
    }


}