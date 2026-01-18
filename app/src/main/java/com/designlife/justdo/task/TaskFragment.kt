package com.designlife.justdo.task

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.designlife.justdo.R
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.presentation.components.CommonCustomHeader
import com.designlife.justdo.common.presentation.components.ProgressBar
import com.designlife.justdo.common.utils.AppServiceLocator
import com.designlife.justdo.common.utils.camelCase
import com.designlife.justdo.common.utils.constants.Constants
import com.designlife.justdo.common.utils.enums.RepeatType
import com.designlife.justdo.common.utils.enums.ScreenType
import com.designlife.justdo.container.presentation.viewmodel.ContainerViewModel
import com.designlife.justdo.container.presentation.viewmodel.ContainerViewModelFactory
import com.designlife.justdo.task.presentation.components.DeleteDialogComponent
import com.designlife.justdo.task.presentation.components.TaskItemDate
import com.designlife.justdo.task.presentation.components.TaskItemView
import com.designlife.justdo.task.presentation.events.TaskEvents
import com.designlife.justdo.task.presentation.viewmodel.TaskViewModel
import com.designlife.justdo.task.presentation.viewmodel.TaskViewModelFactory
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.PrimaryBackgroundColor
import com.designlife.orchestrator.NotificationServiceLocator
import com.designlife.orchestrator.notification.clickmanager.NotificationClickManager
import com.designlife.orchestrator.notification.clickmanager.TaskListener
import com.designlife.orchestrator.notification.repository.TaskNotificationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Calendar

class TaskFragment : Fragment(), TaskListener {

    private lateinit var viewmodel : TaskViewModel
    private lateinit var shareViewModel : ContainerViewModel
    private lateinit var alarmManager : AlarmManager
    private lateinit var notificationManager : NotificationManager
    private lateinit var taskNotificationRepository : TaskNotificationRepository
    private var isOverview : Boolean = false
    private var taskId : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val repeatRepository = AppServiceLocator.provideRepeatRepository()
        val todoRepository = AppServiceLocator.provideTodoRepository(requireActivity().applicationContext)
        val categoryRepository = AppServiceLocator.provideCategoryRepository(requireActivity().applicationContext)
        val todoCategoryRepository = AppServiceLocator.provideTodoCategoryRepository(requireActivity().applicationContext)
        taskNotificationRepository = NotificationServiceLocator.provideNotificationRepository(requireContext(),alarmManager)
        NotificationClickManager.setListener(this)
        val shareViewModelFactory = ContainerViewModelFactory(categoryRepository,repeatRepository)
        shareViewModel = ViewModelProvider(requireActivity(),shareViewModelFactory)[ContainerViewModel::class.java]
        val factory = TaskViewModelFactory(repeatRepository,todoRepository,categoryRepository,todoCategoryRepository,taskNotificationRepository,shareViewModel)
        viewmodel = ViewModelProvider(this,factory)[TaskViewModel::class.java]
        shareViewModel.setupRepeatList(IDateGenerator.getToday())
        navigationArgsDateSet()
    }

    private fun navigationArgsDateSet() {
        arguments?.let { bundle ->
            val todoId = bundle.getInt(Constants.TASK_VIEW_ID)
            val taskViewCheck = bundle.getBoolean(Constants.TASK_VIEW)
            taskViewCheck?.let {
                if (it){
                    isOverview = it
                    todoId?.let {
                        taskId = it
                        viewmodel.onEvent(TaskEvents.LoadTodoById(it))
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val inputText = viewmodel.titleValue.value
                val noteText = viewmodel.noteValue.value
                val selectedDateText = viewmodel.selectedDateText.value
                val selectedTimeText = viewmodel.selectedTimeText.value
                val calendar = Calendar.getInstance()
                val selectedCategory = shareViewModel.categoryList[shareViewModel.selectedCategory.value]
                val selectedRepeatMode = shareViewModel.repeatList.value[shareViewModel.selectedRepeatIndex.value]
                val calendarInstance = shareViewModel.setupRepeatList(viewmodel.rawTaskDateTimeInstance.value.time)
                val progressBar = viewmodel.progressBar.value
                val deletePopupState = viewmodel.deleteTaskPopup.value
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (progressBar) .4F else 1F)
                ) {
                    Box {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = PrimaryBackgroundColor.value),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CommonCustomHeader(headerTitle = if (isOverview) "Task Overview" else "New Task", forTask = true, hasDone = viewmodel.isCompleted.value, onCloseEvent = { findNavController().navigateUp()}, isOverview = isOverview) {
                                if (isOverview){
//                                viewmodel.onEvent(TaskEvents.MarkTaskDone(taskId))
//                                viewmodel.onEvent(TaskEvents.DeleteTaskEvent(taskId))
                                    viewmodel.onEvent(TaskEvents.DeleteTaskPopup(true))
                                }else{
                                    Log.i("ERROR_CHECK", "onCreateView: Compose Click Create Task")
                                    viewmodel.onEvent(TaskEvents.CreateTask(getRepeatType(shareViewModel.selectedRepeatIndex.value),selectedCategory))
                                    findNavController().navigateUp()
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ){
                                item{
                                    TaskItemView(
                                        hasIcon = false,
                                        color = selectedCategory.color,
                                        icon = R.drawable.ic_launcher_background,
                                        labelText = "Add Title",
                                        isNote = false,
                                        placeholder = "New Task Title",
                                        isClickable = isOverview,
                                        inputText = inputText,
                                        isOverview = isOverview,
                                        onInputChange = {
                                            viewmodel.onEvent(TaskEvents.OnTitleChange(it))
                                        }
                                    )
                                }
                                item{
                                    val conditionalVisibility = !(isOverview && noteText.isEmpty())
                                    if (conditionalVisibility){
                                        Spacer(modifier = Modifier.height(14.dp))
                                        TaskItemView(
                                            hasIcon = true,
                                            color = ButtonPrimary.value,
                                            icon = R.drawable.ic_note,
                                            labelText = "Note",
                                            isNote = true,
                                            isClickable = isOverview,
                                            placeholder = "Note to remember ...",
                                            isOverview = isOverview,
                                            inputText = noteText,
                                            onInputChange = {
                                                viewmodel.onEvent(TaskEvents.OnNoteChange(it))
                                            }
                                        )
                                    }
                                }
                                item{
                                    Spacer(modifier = Modifier.height(14.dp))
                                    TaskItemDate(
                                        dateText = selectedDateText,
                                        timeText = selectedTimeText,
                                        icon = R.drawable.ic_schedule,
                                        labelText = "Date",
                                        isClickable = !isOverview,
                                        onDateChange = {
                                            val year = calendar[Calendar.YEAR]
                                            val month = calendar[Calendar.MONTH]
                                            val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
                                            val datePicker = DatePickerDialog(
                                                context,
                                                R.style.CustomDatePickerTheme,
                                                { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                                                    viewmodel.onEvent(TaskEvents.OnDateChange("$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"))
                                                }, year, month, dayOfMonth
                                            )
                                            datePicker.show()
                                        },
                                        onTimeChange = {
                                            val hour = calendar[Calendar.HOUR_OF_DAY]
                                            val minute = calendar[Calendar.MINUTE]

                                            val timePicker = TimePickerDialog(
                                                context,
                                                R.style.CustomTimePickerTheme,
                                                { _, selectedHour: Int, selectedMinute: Int ->
                                                    viewmodel.onEvent(TaskEvents.OnTimeChange("$selectedHour:$selectedMinute"))
                                                }, hour, minute, false
                                            )
                                            timePicker.show()
                                        }
                                    )
                                }
                                item{
                                    Spacer(modifier = Modifier.height(14.dp))
                                    TaskItemView(
                                        hasIcon = true,
                                        color = ButtonPrimary.value,
                                        icon = R.drawable.ic_repeat,
                                        labelText = "Repeat",
                                        isNote = false,
                                        inputText = selectedRepeatMode.first,
                                        onInputChange = {},
                                        isOverview = isOverview,
                                        isClickable = true
                                    ){
                                        if(!isOverview){
                                            val bundle = bundleOf()
                                            bundle.putInt(Constants.SCREEN_TYPE, ScreenType.REPEAT.ordinal)
                                            findNavController().navigate(
                                                R.id.containerFragment,
                                                bundle
                                            )
                                        }
                                    }
                                }
                                item{
                                    Spacer(modifier = Modifier.height(14.dp))
                                    TaskItemView(
                                        hasIcon = true,
                                        color = ButtonPrimary.value,
                                        icon = R.drawable.ic_category,
                                        labelText = "Category",
                                        isNote = false,
                                        inputText = selectedCategory.name.camelCase(),
                                        onInputChange = {},
                                        isOverview = isOverview,
                                        isClickable = true
                                    ){
                                        if (!isOverview){
                                            val bundle = bundleOf()
                                            bundle.putBoolean(Constants.EDIT_MODE,false)
                                            bundle.putInt(Constants.SCREEN_TYPE, ScreenType.CATEGORY.ordinal)
                                            findNavController().navigate(
                                                R.id.containerFragment,
                                                bundle
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (progressBar){
                        ProgressBar()
                    }
                    if (deletePopupState){
                        DeleteDialogComponent(eventSuccuss = {
                            runBlocking {
                                viewmodel.onEvent(TaskEvents.DeleteTasksEvent(taskId,true))
                                viewmodel.onEvent(TaskEvents.DeleteTaskPopup(false))
                                delay(1000)
                                findNavController().navigateUp()
                            }
                        }, eventFail = {
                            viewmodel.onEvent(TaskEvents.DeleteTasksEvent(taskId,false))
                            viewmodel.onEvent(TaskEvents.DeleteTaskPopup(false))
                            findNavController().navigateUp()
                        })
                    }
                }

            }
        }
    }

    private fun getRepeatType(value: Int): RepeatType {
        return when(value){
            0 -> RepeatType.NO_REPEAT
            1 -> RepeatType.DAILY
            2 -> RepeatType.WEEKLY_ON_DAY
            3 -> RepeatType.MONTH_ON_DAY
            4 -> RepeatType.ANNUALLY_ON_MONTH
            5 -> RepeatType.EVERY_WORKING_DAY
            else -> RepeatType.NO_REPEAT
        }
    }


    override fun onUserNotificationEvent(id: Int, title: String) {
        TODO("Not yet implemented")
        taskId = id
    }


}