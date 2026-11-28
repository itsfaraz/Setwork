package com.designlife.setwork.common.domain.repositories

import android.database.Cursor
import com.designlife.setwork.common.data.room.dao.WidgetDao

class WidgetRepository(
    private val widgetDao: WidgetDao
) {
    suspend fun getAllTodo() : Cursor {
        return widgetDao.getAllTodo()
    }
}