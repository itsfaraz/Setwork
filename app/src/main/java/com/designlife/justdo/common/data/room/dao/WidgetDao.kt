package com.designlife.justdo.common.data.room.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface WidgetDao {
    @Transaction
    @Query("SELECT * FROM Todo")
    fun getAllTodo() : Cursor
}