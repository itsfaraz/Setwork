package com.designlife.justdo.calendar

import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.designlife.justdo.R
import com.designlife.justdo.calendar.data.dto.LocalEvent
import com.designlife.justdo.calendar.data.static.EventType
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
import com.designlife.justdo.common.utils.NavOptions
import com.designlife.justdo.common.utils.constants.Constants
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Calendar
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds

class CalendarFragment : Fragment() {
    private lateinit var dateListState: LazyListState
    private lateinit var calendarListState: LazyListState
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

    private fun initialSlide() {
        Log.i("_CRASHCHECK", "initialSlide: before call of list index")

        val dateList = viewModel.dateList.value
        val currentDate = viewModel.currentDate.value
        val computeIndex = dateList.indexOf(currentDate)
        scope.launch(Dispatchers.Main.immediate) {
            if (dateList.isNotEmpty()){
                computeIndex.let { index ->
                    if (index != -1){
                        viewModel.onEvent(CalendarEvents.OnIndexSelected(index))
                        dateListState.scrollToItem(index)
                        viewModel.onEvent(CalendarEvents.HighlightTodoByDate(index))
                    }
                }
            }
            if (viewModel.calendarList.value.isNotEmpty()){
                scrollToRollItem(viewModel.eventIndex.value, calendarListState )
            }
        }
        Log.i("_CRASHCHECK", "initialSlide: during call of list index")
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
                calendarListState = rememberLazyListState()
                scope = rememberCoroutineScope()
                val dateList = viewModel.dateList
                val localEvents = viewModel.calendarList
                val currentMonth = viewModel.currentMonth.value
                val currentYear = viewModel.currentYear.value
                val currentDate = viewModel.currentDate.value
                val selectedIndex = viewModel.selectedIndex.value
                LaunchedEffect(viewModel.isLoaded.value) {
                    try {
                        initialSlide()
                    }catch (e : Exception){
                        e.printStackTrace()
                        delay(200.milliseconds)
                        initialSlide()
                    }
                }
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
                            currentDate = currentDate,
                            currentMonth = currentMonth,
                            currentYear = currentYear,
                            selectedIndex = selectedIndex,
                            isLoading = progressState.value,
                            onTodayEvent  = {
                                viewModel.onEvent(CalendarEvents.OnTodayEvent)
                                viewModel.onEvent(CalendarEvents.HighlightDateByEvent(System.currentTimeMillis()))
                                scope.launch(Dispatchers.Main) {
                                    scrollToRollItem(
                                        viewModel.currentDateIndex.value,
                                        dateListState
                                    )
                                }

                            },
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
                            listState = calendarListState,
                            localEvents = localEvents.value,
                            onFirstIndexChangeEvent = {epoch ->
                                viewModel.onEvent(CalendarEvents.HighlightDateByEvent(epoch))
                                scope.launch(Dispatchers.Main) {
                                    scrollToRollItem(
                                        viewModel.currentDateIndex.value,
                                        dateListState
                                    )
                                }
                            },
                            onLocalEvent = { localEvent ->
                                when(localEvent.eventInfo.eventType){
                                    EventType.TASK -> {
                                        navigateToTaskViewById(localEvent.eventId.toInt())
                                    }
                                    EventType.NOTE -> {
                                        navigateToNoteViewById(localEvent.eventId.toInt())
                                    }
                                    EventType.DECK -> {
                                        navigateToDeckViewById(localEvent.eventId.toInt())
                                    }
                                    else -> {}
                                }
                            }
                        )
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

    private fun navigateToTaskViewById(todoId: Int) {
        val bundle = bundleOf()
        bundle.putBoolean(Constants.TASK_VIEW, true)
        bundle.putInt(Constants.TASK_VIEW_ID, todoId)
        findNavController().navigate(
            R.id.taskFragment,
            bundle,
            NavOptions.navOptionStack
        )
    }

    private fun navigateToNoteViewById(noteId: Int) {
        val bundle = bundleOf()
        bundle.putBoolean(Constants.NOTE_VIEW, true)
        bundle.putInt(Constants.NOTE_VIEW_ID, noteId)
        findNavController().navigate(
            R.id.noteFragment,
            bundle,
            NavOptions.navOptionStack
        )
    }

    private fun navigateToDeckViewById(deckId: Int) {
        val bundle = bundleOf()
        bundle.putBoolean(Constants.DECK_VIEW, true)
        bundle.putInt(Constants.DECK_VIEW_ID, deckId)
        Log.i("NOTIFICATION_FLOW", "HomeFragment :: navigateToNoteViewById: navigated to todoId : $deckId")
        findNavController().navigate(
            R.id.deckFragment,
            bundle,
            NavOptions.navOptionStack
        )
    }

}