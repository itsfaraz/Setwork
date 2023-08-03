package com.designlife.justdo.home.presentation

import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.designlife.justdo.R
import com.designlife.justdo.common.data.room.dao.AppDatabase
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.converters.ColorConverter
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.TodoRepository
import com.designlife.justdo.common.presentation.components.BottomSheetComponent
import com.designlife.justdo.common.utils.AppServiceLocator
import com.designlife.justdo.common.utils.constants.Constants.EDIT_MODE
import com.designlife.justdo.common.utils.constants.Constants.SCREEN_TYPE
import com.designlife.justdo.common.utils.enums.BottomSheetItem
import com.designlife.justdo.common.utils.enums.ScreenType
import com.designlife.justdo.container.presentation.viewmodel.ContainerViewModel
import com.designlife.justdo.container.presentation.viewmodel.ContainerViewModelFactory
import com.designlife.justdo.home.domain.usecase.LoadIntialDatesUseCase
import com.designlife.justdo.home.domain.usecase.LoadNextDatesSetUseCase
import com.designlife.justdo.home.domain.usecase.LoadPreviousDatesSetUseCase
import com.designlife.justdo.home.presentation.components.CategoryComponent
import com.designlife.justdo.home.presentation.components.DateComponent
import com.designlife.justdo.home.presentation.components.HeaderComponent
import com.designlife.justdo.home.presentation.viewmodel.HomeViewModel
import com.designlife.justdo.home.presentation.viewmodel.HomeViewModelFactory
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.PrimaryColor1
import com.designlife.justdo.ui.theme.PrimaryColor2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private var scrollCount = 0
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
        viewModel.loadInitialDates()
        viewModel.fetchAllTodo()
        viewModel.fetchAllCategory()

    }

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val bottomSheetState = rememberModalBottomSheetState(
                    initialValue = ModalBottomSheetValue.Hidden
                )
                var sheetLayoutVisible = viewModel.sheetVisibility.value
                val listState = rememberLazyListState()
                val scope = rememberCoroutineScope()
                val currentDateIndex = viewModel.currentDateIndex.value
                val selectedIndex = viewModel.selectedIndex.value
                val dateList = viewModel.dateList.value
                val categoryList = viewModel.categoryList.value
                val todoList = viewModel.todoList.value
                val currentMonth = viewModel.currentMonth.value
                val currentYear = viewModel.currentYear.value
                val currentDate = viewModel.currentDate.value
                val todayDateIndex = dateList.indexOf(currentDate)
                val isTodayInEnd = IDateGenerator.isTodayInEnd(currentDate) // Scroll 2 times

                LaunchedEffect(todayDateIndex){
                    if (scrollCount != 2){
                        scrollToRollCurrentDate(todayDateIndex,listState,scope)
                        if (isTodayInEnd){
                            scrollCount++;
                        }else{
                            scrollCount = 2;
                        }
                    }
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(PrimaryColor1, PrimaryColor2)
                            )
                        )
                    ) {
                        HeaderComponent(
                            headerText = "All Tasks",
                            onEventClick = {
                                scrollToRollCurrentDate(todayDateIndex,listState, scope)
                            },
                            currentDate = Date(System.currentTimeMillis()),
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        DateComponent(
                            listState = listState,
                            currentDate = currentDate,
                            currentMonth = currentMonth,
                            currentYear = currentYear,
                            dateList = dateList,
                            onEventClick = {
                                viewModel.setSelectedIndex(it)
                                viewModel.fetchDateDataByDate(it)
                            },
                            onChangeVisibleDate = {
                                viewModel.onYearChange(it)
                                viewModel.onMonthChange(it)
                            },
                            loadPreviousTrigger = {
                                viewModel.loadPreviousMonth()
                                scrollToRollCurrentDate(currentDateIndex,listState,scope)
                            },
                            loadNextTrigger = {
                                viewModel.loadNextMonth()
                                scrollToRollCurrentDate(currentDateIndex,listState,scope)
                            },
                            selectedIndex = selectedIndex
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        CategoryComponent(categoryList = categoryList){

                        }
                    }
                    FloatingActionButton(
                        modifier = Modifier
                            .padding(20.dp)
                            .wrapContentSize(),
                        onClick = {
                            viewModel.updateSheetVisibility(true)
                            scope.launch(Dispatchers.Main) {
                                bottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
                            }
                        },
                        backgroundColor = ButtonPrimary
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "FAB", tint = PrimaryColor2)
                    }
                    if (sheetLayoutVisible){
                        BottomSheetComponent(sheetState = bottomSheetState,sheetLayoutVisible, onSelectSheetItem = {
                            when(it){
                                BottomSheetItem.CATEGORY -> {
                                    val bundle = bundleOf()
                                    bundle.putBoolean(EDIT_MODE,true)
                                    bundle.putInt(SCREEN_TYPE,ScreenType.CATEGORY.ordinal)
                                    findNavController().navigate(
                                        R.id.containerFragment,
                                        bundle
                                    )
                                }
                                BottomSheetItem.NOTE -> {}
                                BottomSheetItem.TASK -> {
                                    findNavController().navigate(R.id.taskFragment)
                                }
                            }
                        }) {
                            viewModel.updateSheetVisibility(false)
                            scope.launch {
                                bottomSheetState.hide()
                            }
                        }
                    }
                }

            }
        }
    }

    private fun scrollToRollCurrentDate(currentDateIndex: Int,listState : LazyListState,scope : CoroutineScope) {
        scope.launch(Dispatchers.Main) {
            listState.animateScrollToItem(currentDateIndex)
        }
    }
}

