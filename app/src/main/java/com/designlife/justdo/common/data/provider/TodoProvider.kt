package com.designlife.justdo.common.data.provider

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.designlife.justdo.common.domain.repositories.WidgetRepository
import com.designlife.justdo.common.utils.AppServiceLocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class TodoProvider : ContentProvider() {

    private val MATCHER : UriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    private var widgetRepository : WidgetRepository? = null
    companion object{
        private const val AUTHORITY = "com.designlife.justdo.common.data.provider"

        private const val PATH_ALL_TODO = "ALL_TODO"
        private const val PATH_UPDATE_TODO = "UPDATE_TODO"

        public val CONTENT_URI_1 : Uri = Uri.parse("content://${AUTHORITY}/${PATH_ALL_TODO}")
        public val CONTENT_URI_2 : Uri = Uri.parse("content://${AUTHORITY}/${PATH_UPDATE_TODO}/#")

        private const val ALL_TODO = 1
        private const val UPDATE_TODO = 2

        public val MIME_TYPE_1 = "${ContentResolver.CURSOR_DIR_BASE_TYPE}/vnd.com.designlife.justdo.common.data.provider.alltodo"
        public val MIME_TYPE_2 = "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/vnd.com.designlife.justdo.common.data.provider.updatetodo"
    }

    init {
        MATCHER.addURI(AUTHORITY, PATH_ALL_TODO, ALL_TODO)
        MATCHER.addURI(AUTHORITY, PATH_UPDATE_TODO, UPDATE_TODO)
    }

    override fun onCreate(): Boolean {
        context?.let {
            widgetRepository = AppServiceLocator.provideWidgetRepository(it)
        }
        return context != null
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
       return when(MATCHER.match(uri)){
         ALL_TODO -> {
             runBlocking(Dispatchers.IO) { widgetRepository?.getAllTodo() }
         }
         else -> null
       }
    }

    override fun getType(uri: Uri): String? {
       return when(MATCHER.match(uri)){
           ALL_TODO -> MIME_TYPE_1
           UPDATE_TODO -> MIME_TYPE_2
           else -> null
       }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }
}