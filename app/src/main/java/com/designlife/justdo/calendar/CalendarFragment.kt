package com.designlife.justdo.calendar

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.designlife.justdo.calendar.data.dto.LocalEvent
import com.designlife.justdo.calendar.presentation.components.CalendarComponent
import com.designlife.justdo.calendar.presentation.components.CalendarHeaderComponent
import com.designlife.justdo.calendar.presentation.events.CalendarEvents
import com.designlife.justdo.calendar.presentation.viewmodel.CalendarViewModel
import com.designlife.justdo.calendar.presentation.viewmodel.CalendarViewModelFactory
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.DeckRepository
import com.designlife.justdo.common.domain.repositories.NoteRepository
import com.designlife.justdo.common.domain.repositories.TodoRepository
import com.designlife.justdo.common.domain.repositories.appstore.AppStoreRepository
import com.designlife.justdo.common.utils.enums.ViewType
import com.designlife.justdo.home.domain.usecase.LoadIntialDatesUseCase
import com.designlife.justdo.home.domain.usecase.LoadNextDatesSetUseCase
import com.designlife.justdo.home.domain.usecase.LoadPreviousDatesSetUseCase
import com.designlife.justdo.home.presentation.events.HomeEvents
import com.designlife.justdo.home.presentation.viewmodel.HomeViewModel
import com.designlife.justdo.home.presentation.viewmodel.HomeViewModelFactory
import com.designlife.justdo_provider.SetworkProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Calendar
import java.util.Date

class CalendarFragment : Fragment() {
    private lateinit var dateListState: LazyListState
    private lateinit var viewModel: CalendarViewModel
    private val todoRepository : TodoRepository by inject()
    private val categoryRepository : CategoryRepository by inject()
    private val noteRepository : NoteRepository by inject()
    private val deckRepository : DeckRepository by inject()
    private lateinit var scope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dateGenerator = IDateGenerator()
        val loadDatesUseCase = LoadIntialDatesUseCase(dateGenerator)
        val loadNextDatesUseCase = LoadNextDatesSetUseCase(dateGenerator)
        val loadPreviousDatesUseCase = LoadPreviousDatesSetUseCase(dateGenerator)
        val factory = CalendarViewModelFactory(
            dateGenerator,
            todoRepository,
            categoryRepository,
            noteRepository,
            deckRepository,
            loadDatesUseCase,
            loadNextDatesUseCase,
            loadPreviousDatesUseCase
        )
        viewModel = ViewModelProvider(this, factory)[CalendarViewModel::class.java]


        lifecycleScope.launch {
            viewModel.onEvent(CalendarEvents.OnProgressBarToggle(true))
            coroutineScope {
//                launch { viewModel.fetchAllDecks() }
                launch { viewModel.loadInitialDates() }
//                launch { viewModel.fetchAllTodo() }
//                launch { viewModel.fetchAllCategory() }
//                launch { viewModel.fetchAllNotes() }
            }
            viewModel.onEvent(CalendarEvents.OnProgressBarToggle(false))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val progressState = viewModel.progressBarVisibility
                dateListState = rememberLazyListState()
                scope = rememberCoroutineScope()
                val dateList = viewModel.dateList
                val localEvents = viewModel.calendarList
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        CalendarHeaderComponent(
                            today = Date(System.currentTimeMillis()),
                            viewType = ViewType.CALENDAR,
                            dateList = dateList.value,
                            listState = dateListState,
                            isLoading = progressState.value,
                            onMenuEvent  = {},
                            onTodayEvent  = {},
                            onDateEvent = {},
                            onChangeVisibleDate = {
                                viewModel.onYearChange(it)
                                viewModel.onMonthChange(it)
                            },
                            loadPreviousTrigger = {
                                scope.launch(Dispatchers.Main) {
                                    viewModel.loadPreviousMonth()
                                    scrollToRollItem(
                                        viewModel.currentDateIndex.value,
                                        dateListState
                                    )
                                }
                            },
                            loadNextTrigger = {
                                scope.launch(Dispatchers.Main) {
                                    viewModel.loadNextMonth()
                                    scrollToRollItem(
                                        viewModel.currentDateIndex.value,
                                        dateListState
                                    )
                                }
                            }
                        )
                        CalendarComponent(
                            localEvents = localEvents.value
                        ){ eventId ->
                            when(eventId){
                                else -> { // Todo
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun scrollToRollItem(currentDateIndex: Int, listState: LazyListState) {
        if (currentDateIndex != -1) {
            listState.animateScrollToItem(currentDateIndex)
        }
    }
}