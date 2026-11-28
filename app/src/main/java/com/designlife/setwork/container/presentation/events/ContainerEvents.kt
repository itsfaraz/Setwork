package com.designlife.setwork.container.presentation.events

import androidx.compose.ui.graphics.Color
import com.designlife.setwork.common.domain.entities.Category
import com.designlife.setwork.common.domain.enums.CategoryState
import com.designlife.setwork.common.utils.enums.ScreenType

sealed class ContainerEvents{
    data class OnCategorySelected(val index: Int) : ContainerEvents()
    data class OnRepeatTypeSelected(val index: Int) : ContainerEvents()
    data class NewCategoryInsert(val category: Category) : ContainerEvents()
    data class ColorPickerToggle(val toggleValue : Boolean) : ContainerEvents()
    data class OnPaletteColorSelection(val color : Color) : ContainerEvents()
    data class OnPaletteEmojiSelection(val emoji : String) : ContainerEvents()
    data class OnCategoryNameUpdate(val name : String) : ContainerEvents()
    data class OnCategoryTitleUpdate(val index : Int,val oldName : String, val name : String) : ContainerEvents()
    data class OnCategoryStateChange(val categoryState: CategoryState) : ContainerEvents()
    data class OnCategoryDelete(val index : Int) : ContainerEvents()
    data class OnCategoryListUpdate(val state : CategoryState) : ContainerEvents()
}
