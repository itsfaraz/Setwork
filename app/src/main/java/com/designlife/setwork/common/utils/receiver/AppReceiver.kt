package com.designlife.setwork.common.utils.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.designlife.setwork.common.domain.repositories.TodoRepository
//import com.designlife.setwork.common.utils.AppServiceLocator
import com.designlife.justdo_provider.SetworkProvider
import com.designlife.justdo_provider.data.ProviderTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppReceiver() : BroadcastReceiver(), KoinComponent {
    private val provider : SetworkProvider by inject()
    private val todoRepository : TodoRepository by inject()
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            scope.launch {
                try {
                    todoRepository.getAllTodo().collectLatest { todos ->
                        val task = todos
                            .filter { todo -> !todo.isCompleted }
                            .map {
                                todo ->
                                ProviderTask(
                                    id = todo.todoId.toLong(),
                                    title = todo.title,
                                    color = Color.Blue,
                                    description = todo.note,
                                    completed = todo.isCompleted,
                                    time = todo.date.time)
                            }
                        provider.addTaskList(task)
                    }
                } catch (e : Exception){
                    Log.e("FLOW", "onReceive: ${e.message}")
                }finally {
                    pendingResult.finish()
                }
            }
        }
    }
}