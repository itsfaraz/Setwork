package com.designlife.justdo.container.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.designlife.justdo.common.domain.entities.Category
import com.designlife.justdo.common.utils.camelCase
import com.designlife.justdo.ui.theme.TaskItemLabelColor

@Composable
fun CategoryComponent(
    categoryList : List<Category>,
    selectedCategory : Int,
    colorPickerSelectedColor : Color,
    colorPickerSelectedEmoji : String,
    categoryName : String,
    onCategoryNameChange : (categoryName : String) -> Unit,
    onColorPickerEvent : () -> Unit,
    onCategorySelectedEvent : (index : Int) -> Unit,
    onNewCategoryEvent : (category : Category) -> Unit
) {

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
                    if (categoryName.isNotBlank() && colorPickerSelectedColor != TaskItemLabelColor)
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