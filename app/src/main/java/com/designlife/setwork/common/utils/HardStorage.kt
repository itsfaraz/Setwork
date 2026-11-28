package com.designlife.setwork.common.utils

import android.content.Context
import android.util.Log
import com.designlife.setwork.common.data.entities.AppData
import com.designlife.setwork.common.data.entities.Todo
import com.designlife.setwork.common.domain.repeat.RepeatRepository
import com.designlife.setwork.common.domain.repositories.CategoryRepository
import com.designlife.setwork.common.domain.repositories.DeckRepository
import com.designlife.setwork.common.domain.repositories.NoteRepository
import com.designlife.setwork.common.domain.repositories.TodoCategoryRepository
import com.designlife.setwork.common.domain.repositories.TodoRepository
import com.designlife.setwork.home.HomeFragment
import com.designlife.justdo_provider.SetworkProvider
import com.designlife.orchestrator.SchedulingEngine
import com.designlife.orchestrator.data.NotificationInfo
import com.designlife.orchestrator.data.NotificationStatus
import com.designlife.orchestrator.data.NotificationType
import com.google.gson.Gson
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.IOException


object HardStorage : KoinComponent {

    private val todoRepository : TodoRepository by inject()
    private val categoryRepository : CategoryRepository by inject()
    private val deckRepository : DeckRepository by inject()
    private val noteRepository : NoteRepository by inject()
    suspend fun backupImport(context : Context,data : String) : Boolean {
        if (data.isEmpty()) return false
        try {

            val gsonData = Gson().fromJson(data, AppData::class.java)

            gsonData?.let {
                if (it.todos.isNotEmpty()){
                    val todos = it.todos.map { it.toTodo() }
                    todoRepository.insertAllImportedTodo(todos)
                    setNotifications(context,todos)
                }

                if (it.decks.isNotEmpty()){
                    deckRepository.insertAllImportedDeck(it.decks.map { it.toDeck() })
                }

                if (it.notes.isNotEmpty()){
                    noteRepository.insertAllImportedNote(it.notes.map { it.toNote() })
                }

                if (it.categories.isNotEmpty()){
                    categoryRepository.insertAllImportedCategories(it.categories.map { it.toCategory() })
                }
            } ?: return false

        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun setNotifications(context : Context,taskList : List<Todo>) {
        val notificationInfoData = taskList.filter { it.date >= System.currentTimeMillis() }.map { todo: Todo ->
            NotificationInfo(
                taskTitle = todo.title,
                taskSubTitle = (if (todo.note.isEmpty()) "" else if (todo.note.length > 25) "${
                    todo.note.substring(
                        0,
                        25
                    )
                } ..." else todo.note),
                scheduledTime = todo.date,
                createdTime = System.currentTimeMillis(),
                deliveredTime = 0L,
                notificationType = NotificationType.TASK_NOTIFY,
                notificationStatus = NotificationStatus.ACTIVE,
                taskId = todo.todoId.toInt()
            )
        }
        SchedulingEngine(context)
            .notificationScheduler()
            .scheduleBulkNotification(notificationInfoData)
    }

    fun backupExport(){
        try {
            val data = AppData(
                todos = HomeFragment.todoListIE,
                notes = HomeFragment.noteListIE,
                decks = HomeFragment.deckListIE,
                categories = HomeFragment.categoryListIE,
                createdAt = System.currentTimeMillis()
            )
            val dataJson = Gson().toJson(data)
            HomeFragment.EXPORT_DATA.value = dataJson
            Log.i("IE_FLOW", "backupExport: ${HomeFragment.EXPORT_DATA} ")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}