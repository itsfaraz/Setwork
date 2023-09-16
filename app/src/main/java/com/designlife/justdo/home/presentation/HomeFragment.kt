package com.designlife.justdo.home.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.designlife.justdo.R
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.repositories.appstore.AppStoreRepository
import com.designlife.justdo.common.presentation.components.BottomSheet
import com.designlife.justdo.common.presentation.components.ProgressBar
import com.designlife.justdo.common.utils.AppServiceLocator
import com.designlife.justdo.common.utils.constants.Constants.EDIT_MODE
import com.designlife.justdo.common.utils.constants.Constants.SCREEN_TYPE
import com.designlife.justdo.common.utils.constants.Constants.TASK_VIEW
import com.designlife.justdo.common.utils.constants.Constants.TASK_VIEW_ID
import com.designlife.justdo.common.utils.enums.BottomSheetItem
import com.designlife.justdo.common.utils.enums.ScreenType
import com.designlife.justdo.home.domain.usecase.LoadIntialDatesUseCase
import com.designlife.justdo.home.domain.usecase.LoadNextDatesSetUseCase
import com.designlife.justdo.home.domain.usecase.LoadPreviousDatesSetUseCase
import com.designlife.justdo.home.presentation.components.CategoryComponent
import com.designlife.justdo.home.presentation.components.DateComponent
import com.designlife.justdo.home.presentation.components.HeaderComponent
import com.designlife.justdo.home.presentation.components.TodoItemList
import com.designlife.justdo.home.presentation.events.HomeEvents
import com.designlife.justdo.home.presentation.viewmodel.HomeViewModel
import com.designlife.justdo.home.presentation.viewmodel.HomeViewModelFactory
import com.designlife.justdo.ui.theme.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var dateListState : LazyListState
    private lateinit var todoListState : LazyListState
    private lateinit var scope : CoroutineScope
    private lateinit var appStoreRepository: AppStoreRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dateGenerator = IDateGenerator()
        val loadDatesUseCase = LoadIntialDatesUseCase(dateGenerator)
        val loadNextDatesUseCase = LoadNextDatesSetUseCase(dateGenerator)
        val loadPreviousDatesUseCase = LoadPreviousDatesSetUseCase(dateGenerator)
        val todoRepository = AppServiceLocator.provideTodoRepository(requireActivity().applicationContext)
        val categoryRepository = AppServiceLocator.provideCategoryRepository(requireActivity().applicationContext)
        val factory = HomeViewModelFactory(dateGenerator,todoRepository,categoryRepository,loadDatesUseCase,loadNextDatesUseCase,loadPreviousDatesUseCase)
        viewModel = ViewModelProvider(this,factory)[HomeViewModel::class.java]

        appStoreRepository = AppServiceLocator.provideAppStoreRepository(requireContext())
        checkNotificationView()
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.onEvent(HomeEvents.OnProgressBarToggle(true))
            scope.launch {
                viewModel.loadInitialDates()
            }
            scope.launch {
                viewModel.fetchAllTodo()
            }
            scope.launch {
                viewModel.fetchAllCategory()
            }
            initialSlide()
        }
    }

    private fun checkNotificationView() {
        CoroutineScope(Dispatchers.IO).launch {
            val todoId = appStoreRepository.getTodoId()
            if (todoId != -1){
                navigateToTaskViewById(todoId)
                appStoreRepository.updateTodoId(-1)
            }
        }
    }

    private suspend fun initialSlide() {
            delay(800)
            val job : Job = scope.launch(Dispatchers.Default) {
                val index = viewModel.dateList.value.indexOf(viewModel.currentDate.value)
                viewModel.onEvent(HomeEvents.OnIndexSelected(index))
                scope.launch {
                    scrollToRollItem(viewModel.todoIndex.value+1 ,todoListState)
                }
                scope.launch {
                    scrollToRollItem(index,dateListState)
                }
            }
            job.join()
            viewModel.onEvent(HomeEvents.OnProgressBarToggle(false))
    }

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                dateListState = rememberLazyListState()
                scope = rememberCoroutineScope()
                val bottomSheetState = rememberModalBottomSheetState(
                    initialValue = ModalBottomSheetValue.Hidden
                )
                var sheetLayoutVisible = viewModel.sheetVisibility.value
                val currentDateIndex = viewModel.currentDateIndex.value
                val selectedIndex = viewModel.selectedIndex.value
                val dateList = viewModel.dateList.value
                val categoryList = viewModel.categoryList.value
                val todoList = viewModel.todoList.value
                todoListState = rememberLazyListState()
                val colorMap = viewModel.colorMap.value
                val currentMonth = viewModel.currentMonth.value
                val currentYear = viewModel.currentYear.value
                val currentDate = viewModel.currentDate.value
                val todayDateIndex = dateList.indexOf(currentDate)
                val selectedDateTodoIndex = viewModel.todoIndex.value
                val selectedCategoryIndex = viewModel.selectedCategoryIndex.value
                val progressBar = viewModel.progressBarVisibility.value
                val sheetVisibility = viewModel.sheetVisibility.value
                val isBottomSheetToggled = remember {
                    mutableStateOf(false)
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(if (viewModel.progressBarVisibility.value) 0.7F else 1F)
                            .blur(radius = if (viewModel.progressBarVisibility.value) 7.dp else 0.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(PrimaryColorHome2, PrimaryColorHome1)
                                )
                            )
                        ) {
                            HeaderComponent(
                                headerText = "All Tasks",
                                onEventClick = {
                                    scope.launch(Dispatchers.Main) {
                                        viewModel.onEvent(HomeEvents.OnIndexSelected(todayDateIndex))
                                        dateListState.scrollToItem(todayDateIndex)
                                        viewModel.onEvent(HomeEvents.HighlightTodoByDate(todayDateIndex))
                                        todoListState.scrollToItem(viewModel.todoIndex.value)
                                    }
                                },
                                currentDate = Date(System.currentTimeMillis()),
                            )
                            Spacer(modifier = Modifier.height(40.dp))
                            DateComponent(
                                listState = dateListState,
                                currentDate = currentDate,
                                currentMonth = currentMonth,
                                currentYear = currentYear,
                                dateList = dateList,
                                onEventClick = {
                                    viewModel.onEvent(HomeEvents.HighlightTodoByDate(it))
                                    scope.launch(Dispatchers.Main) {
                                        scrollToRollItem(viewModel.todoIndex.value,todoListState)
                                        viewModel.onEvent(HomeEvents.OnIndexSelected(it))
                                    }
                                },
                                onChangeVisibleDate = {
                                    viewModel.onYearChange(it)
                                    viewModel.onMonthChange(it)
                                },
                                loadPreviousTrigger = {
                                    scope.launch(Dispatchers.Main) {
                                        viewModel.loadPreviousMonth()
                                        scrollToRollItem(viewModel.currentDateIndex.value,dateListState)
                                    }
                                },
                                loadNextTrigger = {
                                    scope.launch(Dispatchers.Main) {
                                        viewModel.loadNextMonth()
                                        scrollToRollItem(viewModel.currentDateIndex.value,dateListState)
                                    }
                                },
                                selectedIndex = selectedIndex
                            )
                            Spacer(modifier = Modifier.height(40.dp))
                            CategoryComponent(categoryList = categoryList, selectedCategoryIndex = selectedCategoryIndex){ categoryIndex ->
                                viewModel.onEvent(HomeEvents.OnCategorySortSelected(categoryIndex))
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            TodoItemList(
                                listState = todoListState,
                                todoList = todoList,
                                colorMap = colorMap,
                                onFirstIndexChangeEvent = {date ->
                                    viewModel.onEvent(HomeEvents.HighlightDateByTodo(date))
                                    scope.launch(Dispatchers.Main) {
                                        scrollToRollItem(viewModel.currentDateIndex.value,dateListState)
                                    }
                                },
                            ){todoId ->
                                navigateToTaskViewById(todoId)
                            }
                        }
                        FloatingActionButton(
                            modifier = Modifier
                                .padding(20.dp)
                                .wrapContentSize(),
                            onClick = {
                                viewModel.updateSheetVisibility(true)
                            },
                            backgroundColor = ButtonPrimary
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "FAB", tint = PrimaryColorHome2)
                        }
                        if (sheetVisibility){
                            val dialog = BottomSheet.dialog(
                                context = requireActivity(),
                                onCloseEvent = {
                                    isBottomSheetToggled.value = false
                                    viewModel.updateSheetVisibility(false)
                                },
                                onTaskEvent = {

                                    val bundle = bundleOf()
                                    bundle.putBoolean(TASK_VIEW,false)
                                    findNavController().navigate(
                                        R.id.taskFragment,
                                        bundle
                                    )
                                },
                                onNoteEvent = {

                                },
                                onDeckEvent = {

                                }
                            )
                            if (!isBottomSheetToggled.value){
                                dialog.show()
                                isBottomSheetToggled.value = true
                            }
                        }
                    }
                    if (viewModel.progressBarVisibility.value){
                        ProgressBar()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.archiveTodos(viewModel.todoList.value)
    }

    private fun navigateToTaskViewById(todoId: Int) {
        val bundle = bundleOf()
        bundle.putBoolean(TASK_VIEW,true)
        bundle.putInt(TASK_VIEW_ID,todoId)
        findNavController().navigate(
            R.id.taskFragment,
            bundle
        )
    }

    private suspend fun scrollToRollItem(currentDateIndex: Int, listState : LazyListState) {
        listState.animateScrollToItem(currentDateIndex)
    }
}

