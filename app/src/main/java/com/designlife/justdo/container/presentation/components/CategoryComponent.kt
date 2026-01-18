package com.designlife.justdo.container.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.designlife.justdo.common.domain.entities.Category
import com.designlife.justdo.common.domain.enums.CategoryState
import com.designlife.justdo.common.utils.camelCase
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.PrimaryBackgroundColor
import com.designlife.justdo.ui.theme.TaskItemLabelColor

@Composable
fun CategoryComponent(
    categoryList : List<Category>,
    categoryMode : CategoryState,
    onCategoryTitleUpdate : (index : Int,oldTitle : String,title : String) -> Unit,
    onCategoryListUpdate : () -> Unit,
    onCategoryDelete : (index : Int) -> Unit,
    selectedCategory : Int,
    colorPickerSelectedColor : Color,
    colorPickerSelectedEmoji : String,
    categoryName : String,
    onCategoryNameChange : (categoryName : String) -> Unit,
    onColorPickerEvent : () -> Unit,
    onCategorySelectedEvent : (index : Int) -> Unit,
    onNewCategoryEvent : (category : Category) -> Unit
) {

    when(categoryMode){
        CategoryState.INSERT -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ){
                item {
                    // Add Category
                    Spacer(modifier = Modifier.height(15.dp))
                    CustomRadioItem(
                        title = "Add",
                        isSelected = false,
                        isDummyCategory = true,
                        categoryName = categoryName,
                        onCategoryNameChange = {onCategoryNameChange(it)},
                        colorCode = Color.Gray,
                        colorPickerSelectedColor = colorPickerSelectedColor,
                        colorPickerEvent = {
                            onColorPickerEvent()
                        },
                        onCategoryInsertEvent = {
                            // Add New Category
                            if (categoryName.isNotBlank())
                                onNewCategoryEvent(Category(name = categoryName, totalTodo = 0, totalCompleted = 0, color = colorPickerSelectedColor, emoji = colorPickerSelectedEmoji))
                        }
                    ){}
                    Spacer(modifier = Modifier.height(20.dp))
                }
                items(categoryList.size){ index ->
                    val item = categoryList[index]

                    CustomRadioItem(
                        title = item.name.camelCase(),
                        isSelected = index == selectedCategory,
                        isDummyCategory = false,
                        colorCode = item.color,
                        colorPickerSelectedColor = colorPickerSelectedColor,
                        colorPickerEvent = {
                            // Do nothing here
                        }
                    ){
                        if (index != selectedCategory)
                            onCategorySelectedEvent(index)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
        CategoryState.UPDATE -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ){
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
                items(categoryList.size){ index ->
                    val item = categoryList[index]
                    CategoryItemComponent(
                        categoryTitle = item.name,
                        onTitleUpdate = {onCategoryTitleUpdate(index,item.name,it)},
                        onDeleteEvent = {onCategoryDelete(index)}
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(80.dp))
                        Button(
                            modifier = Modifier.fillMaxWidth(.9F),
                            onClick = {
                                onCategoryListUpdate()
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonPrimary.value)
                        ) {
                            Text(text = "Save", textAlign = TextAlign.Center, style = TextStyle(color = Color.White))
                        }
                    }
                }
            }
        }
        CategoryState.NONE -> {}
    }
}