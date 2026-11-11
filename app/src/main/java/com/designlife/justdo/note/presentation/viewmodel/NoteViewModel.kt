package com.designlife.justdo.note.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designlife.justdo.common.AppOutput
import com.designlife.justdo.common.domain.entities.Category
import com.designlife.justdo.common.domain.entities.Note
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.NoteRepository
import com.designlife.justdo.common.utils.ImageConverter
import com.designlife.orchestrator.NotificationScheduler
import com.designlife.orchestrator.data.NotificationInfo
import com.designlife.orchestrator.data.NotificationStatus
import com.designlife.orchestrator.data.NotificationType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class NoteViewModel(
    private val noteRepository: NoteRepository,
    private val categoryRepository: CategoryRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    data class State(
        val noteId: Long = 0L,
        val title: String = "",
        val content: String = "",
        val categories: List<Category> = emptyList(),
        val selectedCategoryIndex: Int = -1,
        val coverImage: Bitmap? = null,
        val createdTime: Long = System.currentTimeMillis(),
        val isLoading: Boolean = false,
        val reminderCalendar: Calendar? = null,
        val reminderEnabled: Boolean = false,
        val aiEnabled: Boolean = false,
        val toolbarVisible: Boolean = false
    )

    private val _state = mutableStateOf(State())
    val state = _state

    private val mutex = Mutex()
    private var autoSaveJob: Job? = null


    fun init(noteId: Long?, categoryIndex: Int?) {
        viewModelScope.launch {
            val categories = categoryRepository.getAllCategory().firstOrNull().orEmpty()

            _state.value = _state.value.copy(
                categories = categories,
                selectedCategoryIndex = categoryIndex ?: 0
            )

            if (noteId != null && noteId != -1L) {
                loadNote(noteId)
            }
        }
    }


    fun initExisting(noteId: Long?) {
        viewModelScope.launch {
            val categories = categoryRepository.getAllCategory().firstOrNull().orEmpty()
            _state.value = _state.value.copy(
                categories = categories
            )
            if (noteId != null && noteId != -1L) {
                loadNote(noteId)
            }
        }
    }


    private suspend fun loadNote(id: Long) {
        _state.value = _state.value.copy(isLoading = true)

        val note = noteRepository.getNoteById(id)

        _state.value = _state.value.copy(
            noteId = note.noteId,
            title = note.title,
            content = note.content,
            createdTime = note.createdTime.time,
            selectedCategoryIndex = _state.value.categories.indexOfFirst {
                it.id == note.categoryId
            },
            coverImage = ImageConverter.getBitMapFromByteArray(note.coverImage),
            isLoading = false,
        )
    }

    // ----------------------------
    // INTENTS (ALL USER ACTIONS)
    // ----------------------------
    fun onTitleChange(v: String) {
        updateState { copy(title = v) }
    }

    fun onContentChange(v: String) {
        updateState { copy(content = v) }
    }

    fun onCategoryChange(index: Int) {
        updateState { copy(selectedCategoryIndex = index) }
    }

    fun onCoverChange(bitmap: Bitmap) {
        updateState { copy(coverImage = bitmap) }
    }

    fun toggleReminder() {
        updateState { copy(reminderEnabled = !reminderEnabled) }
    }

    fun toggleAI() {
        updateState { copy(aiEnabled = !aiEnabled) }
    }

    fun toggleToolbar() {
        updateState { copy(toolbarVisible = !toolbarVisible) }
    }

    fun onDateChange(day: Int, month: Int, year: Int) {
        val cal = _state.value.reminderCalendar ?: Calendar.getInstance()
        cal.set(year, month - 1, day)
        updateState { copy(reminderCalendar = cal, reminderEnabled = true) }
    }

    fun onTimeChange(hour: Int, minute: Int) {
        val cal = _state.value.reminderCalendar ?: Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        updateState { copy(reminderCalendar = cal, reminderEnabled = true) }
    }

    // ----------------------------
    // STATE UPDATE CORE
    // ----------------------------
    private fun updateState(block: State.() -> State) {
        _state.value = _state.value.block()
        triggerAutoSave()
    }

    private fun triggerAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(500)
            saveInternal()
        }
    }

    fun saveNow() {
        viewModelScope.launch {
            saveInternal()
        }
    }

    private suspend fun saveInternal() {
        mutex.withLock {
            val s = _state.value
            val category = s.categories.getOrNull(s.selectedCategoryIndex) ?: return

            val coverBytes = withContext(Dispatchers.Default) {
                ImageConverter.getByteArrayFromBitMap(s.coverImage)
            }

            if (s.noteId == 0L) {
                val id = noteRepository.insertNote(
                    Note(
                        title = s.title.ifEmpty { "Untitled" },
                        content = s.content,
                        categoryId = category.id,
                        emoji = category.emoji,
                        coverImage = coverBytes,
                        createdTime = Date(s.createdTime),
                        lastModified = Date(),
                        isArchive = false
                    )
                )

                _state.value = s.copy(noteId = id)

            } else {
                noteRepository.updateNote(
                    s.noteId,
                    Note(
                        noteId = s.noteId,
                        title = s.title,
                        content = s.content,
                        categoryId = category.id,
                        emoji = category.emoji,
                        coverImage = coverBytes,
                        createdTime = Date(s.createdTime),
                        lastModified = Date(),
                        isArchive = false
                    )
                )
            }

            scheduleNotificationIfNeeded(s)
        }
    }

    private fun scheduleNotificationIfNeeded(s: State) {
        if (!s.reminderEnabled || s.reminderCalendar == null) return

        notificationScheduler.scheduleNotification(
            NotificationInfo(
                taskId = s.noteId.toInt(),
                taskTitle = s.title.ifEmpty { "Setwork Note" },
                taskSubTitle = s.content.take(25),
                scheduledTime = s.reminderCalendar.timeInMillis,
                notificationType = NotificationType.NOTE_NOTIFY,
                notificationStatus = NotificationStatus.ACTIVE
            )
        )
    }

    // ----------------------------
    // DELETE / DUPLICATE / EXPORT
    // ----------------------------
    fun deleteNote() {
        val id = _state.value.noteId
        if (id == 0L) return

        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteNote(id)
        }
    }

    fun archiveNote() {
        val id = _state.value.noteId
        if (id == 0L) return
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.archiveNote(id)
        }
    }

    fun duplicateNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val s = _state.value
            val category = s.categories.getOrNull(s.selectedCategoryIndex) ?: return@launch

            val cover = ImageConverter.getByteArrayFromBitMap(s.coverImage)

            noteRepository.insertNote(
                Note(
                    title = "Duplicate - ${s.title}",
                    content = s.content,
                    categoryId = category.id,
                    emoji = category.emoji,
                    coverImage = cover,
                    createdTime = Date(),
                    lastModified = Date(),
                    isArchive = false
                )
            )
        }
    }

    fun exportPdf(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val s = _state.value
            AppOutput.exportAsPdf(context, s.title, s.content)
        }
    }

    fun exportPng(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val s = _state.value
            AppOutput.exportAsPng(context, s.title, s.content)
        }
    }

    fun toggleLoading(value : Boolean){
        _state.value = _state.value.copy(isLoading = value)
    }
}