package com.designlife.justdo.common.data.room.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.designlife.justdo.common.data.entities.Category
import com.designlife.justdo.common.data.entities.CategoryWithTodos
import com.designlife.justdo.common.data.entities.Note
import com.designlife.justdo.common.data.entities.Todo

@Database(entities = [Category::class,Todo::class, Note::class], version = 1)
abstract class AppDatabase : RoomDatabase() {



    abstract fun todoDao() : TodoDao
    abstract fun categoryDao() : CategoryDao
    abstract fun noteDao() : NoteDao

    companion object{
        private val DB_NAME = "JUSTDO"
        @Volatile
        private var INSTANCE : AppDatabase? = null

        fun getDatabase(context : Context) : AppDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    DB_NAME
                ).createFromAsset("database/Category.db").build()
                INSTANCE = instance
                return instance
            }
        }

    }


}