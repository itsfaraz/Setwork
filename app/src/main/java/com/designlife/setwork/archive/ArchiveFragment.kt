package com.designlife.setwork.archive

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.designlife.setwork.R
import com.designlife.setwork.archive.presentation.components.ArchiveComponent
import com.designlife.setwork.archive.presentation.events.ArchiveEvents
import com.designlife.setwork.calendar.presentation.events.CalendarEvents
import com.designlife.setwork.common.domain.repositories.CategoryRepository
import com.designlife.setwork.common.domain.repositories.DeckRepository
import com.designlife.setwork.common.domain.repositories.NoteRepository
import com.designlife.setwork.common.domain.repositories.TodoRepository
import com.designlife.setwork.common.presentation.components.CommonCustomHeader
import com.designlife.setwork.common.presentation.components.TopPaddingComponent
import com.designlife.setwork.common.utils.enums.ViewType
import com.designlife.setwork.archive.presentation.viewmodel.ArchiveViewModel
import com.designlife.setwork.archive.presentation.viewmodel.ArchiveViewModelFactory
import com.designlife.setwork.calendar.data.static.EventType
import com.designlife.setwork.common.utils.NavOptions
import com.designlife.setwork.common.utils.constants.Constants
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ArchiveFragment : Fragment() {
    private lateinit var viewModel: ArchiveViewModel
    private val todoRepository : TodoRepository by inject()
    private val categoryRepository : CategoryRepository by inject()
    private val noteRepository : NoteRepository by inject()
    private val deckRepository : DeckRepository by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ArchiveViewModelFactory(
            todoRepository,
            categoryRepository,
            noteRepository,
            deckRepository
        )
        viewModel = ViewModelProvider(this, factory)[ArchiveViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val archiveListState = rememberLazyListState()
                val localEvents = viewModel.archiveList
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopPaddingComponent()
                        CommonCustomHeader(
                            headerTitle = "Archive's",
                            forTask = false,
                            hasDone = false,
                            onCloseEvent = { findNavController().navigateUp()},
                            isOverview = true,
                            viewType = ViewType.UNKNOWN) {}
                        ArchiveComponent(
                            listState = archiveListState,
                            localEvents = localEvents.value,
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

                            },
                            onDeleteEvent = { localEvent ->
                                when(localEvent.eventInfo.eventType){
                                    EventType.TASK -> {
                                        viewModel.onEvent(ArchiveEvents.DeleteTaskById(localEvent.eventId.toInt()))
                                    }
                                    EventType.NOTE -> {
                                        viewModel.onEvent(ArchiveEvents.DeleteNoteById(localEvent.eventId))
                                    }
                                    EventType.DECK -> {
                                        viewModel.onEvent(ArchiveEvents.DeleteDeckById(localEvent.eventId))
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
        bundle.putLong("deckId", deckId.toLong())
        Log.i("NOTIFICATION_FLOW", "HomeFragment :: navigateToNoteViewById: navigated to todoId : $deckId")
        findNavController().navigate(
            R.id.deckFragment,
            bundle,
            NavOptions.navOptionStack
        )
    }

}