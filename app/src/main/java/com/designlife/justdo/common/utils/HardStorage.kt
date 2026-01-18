package com.designlife.justdo.common.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import com.designlife.justdo.common.data.entities.Category
import com.designlife.justdo.common.data.entities.Deck
import com.designlife.justdo.common.data.entities.Note
import com.designlife.justdo.common.data.entities.Todo
import com.designlife.justdo.common.data.room.dao.AppDatabase
import com.designlife.justdo.common.utils.security.EncryptionUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException


object HardStorage {
    val scope = CoroutineScope(Dispatchers.IO + Job())

    suspend fun backupImport(context : Context) : Boolean {
        val backupFolder = File(context.getExternalFilesDir(null),"Setwork/Backup/")
//        val envExternalFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

        if (!backupFolder.exists()){
            Log.i("BACKUP", "backupImport: Document folder Not exists")
            return false
        }
//        val appFolder = File(envExternalFileDir, "Setwork")
//        if (!appFolder.exists()) {
//            Log.i("BACKUP", "backupImport: Setwork folder Not exists")
//            return false
//        }
//        val backupFolder = File(appFolder, "Backup")
//        if (!backupFolder.exists()) {
//            Log.i("BACKUP", "backupImport: Backup folder Not exists")
//            return false
//        }

        try {
            val backupTodoFile = File(backupFolder,AppDatabase.BACKUP_EN_TODO)
            val backupDeckFile = File(backupFolder,AppDatabase.BACKUP_EN_DECK)
            val backupCategoryFile = File(backupFolder,AppDatabase.BACKUP_EN_CATEGORY)
            val backupNoteFile = File(backupFolder, AppDatabase.BACKUP_EN_NOTE)

            Log.i("BACKUP", "backupImport: Backup Todo Path :${backupTodoFile.absolutePath}")
            Log.i("BACKUP", "backupImport: Backup Todo File Exists :${backupTodoFile.exists()}")
            Log.i("BACKUP", "backupImport: Backup Todo File Reading :${backupTodoFile.canRead()}")


            setFilePermission(backupTodoFile)
            setFilePermission(backupDeckFile)
            setFilePermission(backupCategoryFile)
            setFilePermission(backupNoteFile)

            val todoRepository = AppServiceLocator.provideTodoRepository(context)
            val deckRepository = AppServiceLocator.provideDeckRepository(context)
            val noteRepository = AppServiceLocator.provideNoteRepository(context)
            val categoryRepository = AppServiceLocator.provideCategoryRepository(context)

            Log.i("BACKUP", "backupImport: Backup Todo File Path :${backupTodoFile.path.toString()}")
            val todoJson = backupTodoFile.readText()
            val deckJson = backupDeckFile.readText()
            val noteJson = backupNoteFile.readText()
            val categoryJson = backupCategoryFile.readText()

            Log.i("BACKUP", "backupImport: Todo Json : ${todoJson}")
            val todoListType = object : TypeToken<List<Todo>>() {}.type
            val todoList : List<Todo> = Gson().fromJson(scope.async {decrypt(todoJson)}.await(),todoListType)

            val deckListType = object : TypeToken<List<Deck>>() {}.type
            val deckList : List<Deck> = Gson().fromJson(scope.async {decrypt(deckJson)}.await(),deckListType)

            val noteListType = object : TypeToken<List<Note>>() {}.type
            val noteList : List<Note> = Gson().fromJson(scope.async {decrypt(noteJson)}.await(),noteListType)

            val categoryListType = object : TypeToken<List<Category>>() {}.type
            val categoryList : List<Category> = Gson().fromJson(scope.async {decrypt(categoryJson)}.await(),categoryListType)


            todoRepository.insertAllImportedTodo(todoList)
            deckRepository.insertAllImportedDeck(deckList)
            noteRepository.insertAllImportedNote(noteList)
            categoryRepository.insertAllImportedCategories(categoryList)

            return true
        } catch (e: IOException) {
            e.printStackTrace()
            Log.i("BACKUP", "backupImport: Exception : ${e.message}")
            return false
        }
    }

    suspend fun backupExport(context : Context){
        val backupFolder = File(context.getExternalFilesDir(null),"Setwork/Backup/")
//        val envExternalFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

        if (!backupFolder.exists()){
            backupFolder.mkdirs()
        }
//        val appFolder = File(envExternalFileDir, "Setwork")
//        if (!appFolder.exists()) {
//            appFolder.mkdirs();
//        }
//        val backupFolder = File(appFolder, "Backup")
//        if (!backupFolder.exists()) {
//            backupFolder.mkdirs();
//        }
        Log.i("BACKUP", "backupExport: backupFolder : ${backupFolder.absoluteFile}")
//        val backupFileTodo = File(backupFolder, AppDatabase.BACKUP_EN_TODO)
        val backupFileTodo = File(backupFolder,AppDatabase.BACKUP_EN_TODO)
        val backupFileNote = File(backupFolder,AppDatabase.BACKUP_EN_NOTE)
        val backupFileDeck = File(backupFolder, AppDatabase.BACKUP_EN_DECK)
        val backupFileCategory = File(backupFolder,  AppDatabase.BACKUP_EN_CATEGORY)

        checkFileExists(backupFileTodo)
        checkFileExists(backupFileNote)
        checkFileExists(backupFileDeck)
        checkFileExists(backupFileCategory)

        setFilePermission(backupFileTodo)
        setFilePermission(backupFileNote)
        setFilePermission(backupFileDeck)
        setFilePermission(backupFileCategory)

        Log.i("BACKUP", "backupExport: backupFolder : App Database Check")
        try {
            val todoRepository = AppServiceLocator.provideTodoRepository(context)
            val deckRepository = AppServiceLocator.provideDeckRepository(context)
            val noteRepository = AppServiceLocator.provideNoteRepository(context)
            val categoryRepository = AppServiceLocator.provideCategoryRepository(context)
            Log.i("BACKUP", "backupExport: backupFolder : App Database Repositories Fetch")
            val todoList =  todoRepository.getAllRawTodo().firstOrNull()
            val deckList = deckRepository.getAllRawDecks().firstOrNull()
            val noteList = noteRepository.getAllRawNotes().firstOrNull()
            val categoryList = categoryRepository.getAllRawCategory().firstOrNull()

            val todoJson = Gson().toJson(todoList)
            val deckJson = Gson().toJson(deckList)
            val noteJson = Gson().toJson(noteList)
            val categoryJson = Gson().toJson(categoryList)

            Log.i("BACKUP", "backupExport: backupFolder : App Database Repositories Data --> Json File --> ${backupFileTodo.exists()}")
            val todoFileWriter = FileWriter(backupFileTodo, false)
            Log.i("BACKUP", "backupExport: backupFolder : App Database Repositories Data --> Json --> Write")
            todoFileWriter.use {
                Log.i("BACKUP", "backupExport: todo json content : ${todoJson}")
                it.write(scope.async { encrypt(todoJson) }.await())
                Log.i("BACKUP", "backupExport: backupFolder : App Database Repositories Data --> Json --> Write --> Encrypt")
            }


            val noteFileWriter = FileWriter(backupFileNote, false)
            noteFileWriter.use {
                it.write(scope.async {encrypt(noteJson)}.await())
            }

            val deckFileWriter = FileWriter(backupFileDeck, false)
            deckFileWriter.use {
                it.write(scope.async {encrypt(deckJson)}.await())
            }

            val categoryFileWriter = FileWriter(backupFileCategory, false)
            categoryFileWriter.use {
                it.write(scope.async {encrypt(categoryJson)}.await())
            }

            todoFileWriter.close()
            noteFileWriter.close()
            deckFileWriter.close()
            categoryFileWriter.close()
//
//            setReadOnly(backupFileTodo)
//            setReadOnly(backupFileNote)
//            setReadOnly(backupFileDeck)
//            setReadOnly(backupFileCategory)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun checkFileExists(backupFile: File) {
        if (!backupFile.exists()){
            backupFile.createNewFile()
        }
    }

    private fun setFilePermission(backupFile: File) {
        backupFile.setExecutable(true)
        backupFile.setWritable(true)
        backupFile.setReadable(true)
    }

    private suspend fun encrypt(json : String) : String{
//    val secretKey = EncryptionUtils.generateKey()
//    return EncryptionUtils.encrypt(json,secretKey)
    return json
}

    private suspend fun decrypt(json : String) : String{
    //    val secretKey = EncryptionUtils.generateKey()
    //    return EncryptionUtils.decrypt(json,secretKey)
        return json
    }

    private fun setReadOnly(file : File) = file.setReadOnly()

}