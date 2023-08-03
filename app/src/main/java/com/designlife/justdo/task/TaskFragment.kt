package com.designlife.justdo.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
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
import com.designlife.justdo.task.presentation.components.TaskItemDate
import com.designlife.justdo.task.presentation.components.TaskItemView
import com.designlife.justdo.task.presentation.events.TaskEvents
import com.designlife.justdo.task.presentation.viewmodel.TaskViewModel
import com.designlife.justdo.task.presentation.viewmodel.TaskViewModelFactory
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.PrimaryBackgroundColor
import com.designlife.justdo.ui.theme.TaskItemLabelColor
import java.util.Calendar

class TaskFragment : Fragment() {

    private lateinit var viewmodel : TaskViewModel
    private lateinit var shareViewModel : ContainerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repeatRepository = AppServiceLocator.provideRepeatRepository()
        val todoRepository = AppServiceLocator.provideTodoRepository(requireActivity().applicationContext)
        val categoryRepository = AppServiceLocator.provideCategoryRepository(requireActivity().applicationContext)
        val factory = TaskViewModelFactory(repeatRepository,todoRepository,categoryRepository)
        viewmodel = ViewModelProvider(this,factory)[TaskViewModel::class.java]
        val shareViewModelFactory = ContainerViewModelFactory(categoryRepository,repeatRepository)
        shareViewModel = ViewModelProvider(requireActivity(),shareViewModelFactory)[ContainerViewModel::class.java]
        shareViewModel.setupRepeatList(IDateGenerator.getToday())
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
                val selectedCategory = shareViewModel.categoryList.value[shareViewModel.selectedCategory.value]
                val selectedRepeatMode = shareViewModel.repeatList.value[shareViewModel.selectedRepeatIndex.value]
                val calendarInstance = shareViewModel.setupRepeatList(viewmodel.rawTaskDateTimeInstance.value.time)
                val progressBar = viewmodel.progressBar.value
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (progressBar) .4F else 1F)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = PrimaryBackgroundColor),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CommonCustomHeader(headerTitle = "New Task", onCloseEvent = { findNavController().navigateUp()}) {
                            viewmodel.onEvent(TaskEvents.CreateTask(getRepeatType(shareViewModel.selectedRepeatIndex.value),selectedCategory))
                            findNavController().navigateUp()
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
                                    inputText = inputText,
                                    onInputChange = {
                                        viewmodel.onEvent(TaskEvents.OnTitleChange(it))
                                    }
                                )
                            }
                            item{
                                Spacer(modifier = Modifier.height(14.dp))
                                TaskItemView(
                                    hasIcon = true,
                                    color = ButtonPrimary,
                                    icon = R.drawable.ic_note,
                                    labelText = "Note",
                                    isNote = true,
                                    placeholder = "Note to remember ...",
                                    inputText = noteText,
                                    onInputChange = {
                                        viewmodel.onEvent(TaskEvents.OnNoteChange(it))
                                    }
                                )
                            }
                            item{
                                Spacer(modifier = Modifier.height(14.dp))
                                TaskItemDate(
                                    dateText = selectedDateText,
                                    timeText = selectedTimeText,
                                    icon = R.drawable.ic_schedule,
                                    labelText = "Date",
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
                                    color = ButtonPrimary,
                                    icon = R.drawable.ic_repeat,
                                    labelText = "Repeat",
                                    isNote = false,
                                    inputText = selectedRepeatMode.first,
                                    onInputChange = {},
                                    isClickable = true
                                ){
                                    val bundle = bundleOf()
                                    bundle.putInt(Constants.SCREEN_TYPE, ScreenType.REPEAT.ordinal)
                                    findNavController().navigate(
                                        R.id.containerFragment,
                                        bundle
                                    )
                                }
                            }
                            item{
                                Spacer(modifier = Modifier.height(14.dp))
                                TaskItemView(
                                    hasIcon = true,
                                    color = ButtonPrimary,
                                    icon = R.drawable.ic_category,
                                    labelText = "Category",
                                    isNote = false,
                                    inputText = selectedCategory.name.camelCase(),
                                    onInputChange = {},
                                    isClickable = true
                                ){
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
                    if (progressBar){
                        ProgressBar()
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
}