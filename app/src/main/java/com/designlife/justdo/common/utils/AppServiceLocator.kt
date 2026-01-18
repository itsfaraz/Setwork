package com.designlife.justdo.common.utils

import android.content.Context
import com.designlife.justdo.common.data.datastore.appStore
import com.designlife.justdo.common.data.room.dao.AppDatabase
import com.designlife.justdo.common.domain.repeat.RepeatRepository
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.DeckRepository
import com.designlife.justdo.common.domain.repositories.NoteRepository
import com.designlife.justdo.common.domain.repositories.TodoCategoryRepository
import com.designlife.justdo.common.domain.repositories.TodoRepository
import com.designlife.justdo.common.domain.repositories.WidgetRepository
import com.designlife.justdo.common.domain.repositories.appstore.IAppStoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.Date

object AppServiceLocator {
    private var categoryRepository : CategoryRepository? = null
    private var todoRepository : TodoRepository? = null
    private var noteRepository : NoteRepository? = null
    private var deckRepository : DeckRepository? = null
    private var todoCategoryRepository : TodoCategoryRepository? = null
    private var repeatRepository : RepeatRepository? = null
    private var appStoreRepository : IAppStoreRepository? = null
    private var widgetRepository : WidgetRepository? = null


    public fun provideCategoryRepository(context : Context) : CategoryRepository{
       return categoryRepository ?: createCategoryRepository(context);
    }

    private fun createCategoryRepository(context: Context): CategoryRepository {
        val categoryDao = AppDatabase.getDatabase(context).categoryDao()
        categoryRepository = CategoryRepository(categoryDao)
        return categoryRepository!!
    }

    public fun provideTodoRepository(context : Context) : TodoRepository{
        return todoRepository ?: createTodoRepository(context)
    }

    private fun createTodoRepository(context: Context): TodoRepository {
        val todoDao = AppDatabase.getDatabase(context).todoDao()
        todoRepository = TodoRepository(todoDao)
        return todoRepository!!
    }

    public fun provideRepeatRepository() : RepeatRepository{
        return repeatRepository ?: createRepeatRepository()
    }

    private fun createRepeatRepository(): RepeatRepository {
        repeatRepository = RepeatRepository()
        return repeatRepository!!
    }

    public fun provideAppStoreRepository(context: Context) : IAppStoreRepository{
        return appStoreRepository ?: createAppStoreRepository(context)
    }

    private fun createAppStoreRepository(context: Context): IAppStoreRepository {
        appStoreRepository = IAppStoreRepository(context.appStore)
        return appStoreRepository!!
    }

    public fun provideNoteRepository(context : Context) : NoteRepository{
        return noteRepository ?: createNoteRepository(context);
    }

    private fun createNoteRepository(context: Context): NoteRepository {
        val noteDao = AppDatabase.getDatabase(context).noteDao()
        noteRepository = NoteRepository(noteDao)
        return noteRepository!!
    }

    public fun provideDeckRepository(context : Context) : DeckRepository{
        return deckRepository ?: createDeckRepository(context);
    }

    private fun createDeckRepository(context: Context): DeckRepository {
        val deckDao = AppDatabase.getDatabase(context).deckDao()
        deckRepository = DeckRepository(deckDao)
        return deckRepository!!
    }

    public fun provideTodoCategoryRepository(context : Context) : TodoCategoryRepository{
        return todoCategoryRepository ?: createTodoCategoryRepository(context);
    }

    private fun createTodoCategoryRepository(context: Context): TodoCategoryRepository {
        val todoCategoryDao = AppDatabase.getDatabase(context).todoCategoryJunctionDao()
        val todoDao = AppDatabase.getDatabase(context).todoDao()
        val categoryDao = AppDatabase.getDatabase(context).categoryDao()
        todoCategoryRepository = TodoCategoryRepository(todoCategoryDao,todoDao,categoryDao)
        return todoCategoryRepository!!
    }

    public fun provideWidgetRepository(context : Context) : WidgetRepository{
        return widgetRepository ?: createWidgetRepository(context);
    }

    private fun createWidgetRepository(context: Context): WidgetRepository {
        val widgetDao = AppDatabase.getDatabase(context).widgetDao()
        widgetRepository = WidgetRepository(widgetDao)
        return widgetRepository!!
    }
}