package com.designlife.justdo.common.domain.repositories

import android.database.Cursor
import com.designlife.justdo.common.data.room.dao.WidgetDao

class WidgetRepository(
    private val widgetDao: WidgetDao
) {
    suspend fun getAllTodo() : Cursor {
        return widgetDao.getAllTodo()
    }
}