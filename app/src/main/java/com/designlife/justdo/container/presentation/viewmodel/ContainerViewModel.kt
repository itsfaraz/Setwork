package com.designlife.justdo.container.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.entities.Category
import com.designlife.justdo.common.domain.enums.CategoryState
import com.designlife.justdo.common.domain.repeat.RepeatRepository
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.utils.enums.RepeatType
import com.designlife.justdo.common.utils.enums.ScreenType
import com.designlife.justdo.container.presentation.events.ContainerEvents
import com.designlife.justdo.ui.theme.TaskItemLabelColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

class ContainerViewModel(
    private val categoryRepository: CategoryRepository,
    private val repeatRepository: RepeatRepository
) : ViewModel() {

    private val _screenType : MutableState<ScreenType> = mutableStateOf(ScreenType.CATEGORY)
    val screenType = _screenType

//    private val _categoryList : MutableState<List<Category>> = mutableStateOf(listOf(Category()));
    private val _categoryList : SnapshotStateList<Category> = mutableStateListOf();
    val categoryList = _categoryList

    private val _selectedCategory : MutableState<Int> = mutableStateOf(0)
    val selectedCategory = _selectedCategory


    private val _selectedRepeatIndex : MutableState<Int> = mutableStateOf(0)
    val selectedRepeatIndex = _selectedRepeatIndex

    private val _colorPickerToggle : MutableState<Boolean> = mutableStateOf(false)
    val colorPickerToggle = _colorPickerToggle

    private val _editMode : MutableState<Boolean> = mutableStateOf(false)
    val editMode = _editMode

    private val _selectedPaletteColor : MutableState<Color> = mutableStateOf(TaskItemLabelColor.value)
    val selectedPaletteColor = _selectedPaletteColor

    private val _selectedEmoji : MutableState<String> = mutableStateOf("👀")
    val selectedEmoji = _selectedEmoji

    private val _categoryName : MutableState<String> = mutableStateOf("")
    val categoryName = _categoryName

    private val _categoryTitle : MutableState<String> = mutableStateOf("")
    val categoryTitle = _categoryTitle

    private val _categoryMode : MutableState<CategoryState> = mutableStateOf(CategoryState.INSERT)
    val categoryMode = _categoryMode

    private val _repeatList : MutableState< List<Pair<String,RepeatType>>> = mutableStateOf(listOf());
    val repeatList = _repeatList

    init {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.getAllCategory().collectLatest{
                Log.i("Observer", ": ${it.size}")
                updateCategoryList(it)
            }
        }
    }

    fun onEvent(event : ContainerEvents){
        when(event){
            is ContainerEvents.OnCategorySelected -> {
                _selectedCategory.value = event.index
            }
            is ContainerEvents.ColorPickerToggle -> {
                _colorPickerToggle.value = event.toggleValue
            }
            is ContainerEvents.OnPaletteColorSelection -> {
                _selectedPaletteColor.value = event.color
            }
            is ContainerEvents.OnCategoryNameUpdate -> {
                _categoryName.value = event.name
            }
            is ContainerEvents.NewCategoryInsert -> {
                // update db
                insertNewCategory(event.category)
                // clear variables
                clearVariables()
            }
            is ContainerEvents.OnRepeatTypeSelected -> {
                _selectedRepeatIndex.value = event.index
            }
            is ContainerEvents.OnPaletteEmojiSelection -> {
                _selectedEmoji.value = event.emoji
            }
            is ContainerEvents.OnCategoryStateChange -> {
                _categoryMode.value = event.categoryState
            }
            is ContainerEvents.OnCategoryTitleUpdate -> {
                val category = _categoryList[event.index].copy(name = event.name)
                _categoryList[event.index] = category
            }
            is ContainerEvents.OnCategoryDelete-> {
                if (event.index != -1 && event.index < _categoryList.size){
                    val category = _categoryList[event.index]
                    _categoryList.removeAt(event.index)
                    viewModelScope.launch(Dispatchers.IO) {
                        categoryRepository.deleteCategoryById(category.id)
                    }
                }
            }
            is ContainerEvents.OnCategoryListUpdate -> {
                _categoryMode.value = event.state
            }
        }
    }

    private fun updateCategoryList(categoryList : List<Category>){
        _categoryList.clear()
        _categoryList.addAll(categoryList)
    }

    private fun insertNewCategory(category: Category){
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("VM", "insertNewCategory: New Category Inserted")
            categoryRepository.insertCategory(category)
        }
    }

    private fun clearVariables(){
        _categoryName.value = ""
        _selectedPaletteColor.value = TaskItemLabelColor.value
    }

    public fun initialSetup(screenType : ScreenType,editMode : Boolean){
        _screenType.value =  screenType
        _editMode.value = editMode
    }

    public fun setupRepeatList(currentDate : Date){
        try {
            _repeatList.value = repeatRepository.getRepeatList(currentDate)
        }catch (e : Exception){
            e.printStackTrace()
            Log.i("VM", "setupRepeatList: Exception ${e.message}")
        }
    }

    public fun getCatiegoryIndexById(categoryId : Long) : Int{
        val categoryData = categoryList.filterIndexed { index, category -> category.id == categoryId }
        return categoryList.indexOf(categoryData[0])
    }

}