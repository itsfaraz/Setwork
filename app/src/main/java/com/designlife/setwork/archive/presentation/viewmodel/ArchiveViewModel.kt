package com.designlife.setwork.archive.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designlife.setwork.archive.presentation.events.ArchiveEvents
import com.designlife.setwork.calendar.data.dto.LocalEvent
import com.designlife.setwork.calendar.data.static.EventType
import com.designlife.setwork.common.domain.repositories.CategoryRepository
import com.designlife.setwork.common.domain.repositories.DeckRepository
import com.designlife.setwork.common.domain.repositories.NoteRepository
import com.designlife.setwork.common.domain.repositories.TodoRepository
import com.designlife.setwork.ui.theme.TaskItemLabelColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ArchiveViewModel(
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository,
    private val noteRepository: NoteRepository,
    private val deckRepository: DeckRepository
) : ViewModel() {

    private var _progressBarVisibility : MutableState<Boolean> = mutableStateOf(false)
    val progressBarVisibility  = _progressBarVisibility

    private val _archiveList : MutableState<List<LocalEvent>> = mutableStateOf(emptyList());
    val archiveList = _archiveList
    val TAG = "CVM"

    init {
        _progressBarVisibility.value = true
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                combine(
                    todoRepository.getAllTodo(),
                    noteRepository.getAllNotes(),
                    deckRepository.getAllDecks(),
                    categoryRepository.getAllCategory()
                ) { todos, notes, decks, categories ->

                    val colors = categories.associate {
                        it.id to it.color
                    }

                    buildList {

                        todos.forEach {
                            if (it.isArchive){
                                val color = colors[it.categoryId]
                                    ?: TaskItemLabelColor.value

                                add(
                                    LocalEvent(
                                        eventId = it.todoId.toLong(),
                                        eventInfo = LocalEvent.EventInfo(
                                            epoch = it.date.time,
                                            title = it.title,
                                            description = it.note,
                                            eventType = EventType.TASK,
                                            categoryColor = color
                                        )
                                    )
                                )
                            }
                        }

                        notes.forEach {
                            if (it.isArchive){
                                val color = colors[it.categoryId]
                                    ?: TaskItemLabelColor.value

                                add(
                                    LocalEvent(
                                        eventId = it.noteId,
                                        eventInfo = LocalEvent.EventInfo(
                                            epoch = it.lastModified.time,
                                            title = it.title,
                                            description = it.content,
                                            eventType = EventType.NOTE,
                                            categoryColor = color
                                        )
                                    )
                                )
                            }
                        }

                        decks.forEach {
                            if (it.isArchive){
                                val color = colors[it.categoryId]
                                    ?: TaskItemLabelColor.value

                                add(
                                    LocalEvent(
                                        eventId = it.deckId,
                                        eventInfo = LocalEvent.EventInfo(
                                            epoch = it.modifiedDate.time,
                                            title = it.deckName,
                                            description = "Open ${it.totalCards} cards in this deck",
                                            eventType = EventType.DECK,
                                            categoryColor = color
                                        )
                                    )
                                )
                            }
                        }
                    }

                }.collect { events ->
                    _archiveList.value = emptyList()
                    _archiveList.value = events
                    _progressBarVisibility.value = false
                    Log.i(TAG, "Calendar events: ${events.size}")
                }
            }
        }
    }


    fun onEvent(event : ArchiveEvents){
        when(event){
            is ArchiveEvents.OnProgressBarToggle -> {
                _progressBarVisibility.value = event.toggleValue
            }
            is ArchiveEvents.DeleteTaskById -> {
                viewModelScope.launch {
                    todoRepository.deleteTodo(event.id)
                }
            }
            is ArchiveEvents.DeleteNoteById -> {
                viewModelScope.launch {
                    noteRepository.deleteNote(event.id)
                }
            }
            is ArchiveEvents.DeleteDeckById -> {
                viewModelScope.launch {
                    deckRepository.deleteDeck(event.id)
                }
            }
            else -> {}
        }
    }
}