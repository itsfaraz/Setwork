package com.designlife.setwork.common.data.room.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.designlife.setwork.common.data.converter.Converter
import com.designlife.setwork.common.data.entities.Category
import com.designlife.setwork.common.data.entities.Deck
import com.designlife.setwork.common.data.entities.Note
import com.designlife.setwork.common.data.entities.Todo

@Database(entities = [Category::class,Todo::class, Note::class, Deck::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class SetworkDatabase : RoomDatabase() {

    abstract fun todoDao() : TodoDao
    abstract fun categoryDao() : CategoryDao
    abstract fun noteDao() : NoteDao
    abstract fun deckDao() : DeckDao

    abstract fun todoCategoryJunctionDao() : TodoCategoryJunctionDao

    abstract fun widgetDao() : WidgetDao
    companion object{
        public val DB_NAME = "Setwork"
        public val BACKUP_EN_TODO = "Todo.db"
        public val BACKUP_EN_CATEGORY = "Category.db"
        public val BACKUP_EN_NOTE = "Note.db"
        public val BACKUP_EN_DECK = "Deck.db"
        @Volatile
        private var INSTANCE : SetworkDatabase? = null

        fun getDatabase(context : Context) : SetworkDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    SetworkDatabase::class.java,
                    DB_NAME
                ).createFromAsset("database/Category.db").build()
                INSTANCE = instance
                return instance
            }
        }

    }


}