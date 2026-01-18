package com.designlife.justdo.home.presentation.events

import com.designlife.justdo.common.utils.enums.ViewType
import java.util.Date

sealed class HomeEvents{
    data class OnIndexSelected(val index : Int) : HomeEvents()
    data class OnTodoEvent(val index : Int) : HomeEvents()
    data class HighlightTodoByDate(val visibleIndex : Int) : HomeEvents()
    data class HighlightDateByTodo(val visibleIndex : Int) : HomeEvents()
    data class OnCategorySortSelected(val categoryIndex : Int) : HomeEvents()
    data class OnProgressBarToggle(val toggleValue : Boolean) : HomeEvents()
    data class OnViewChange(val view : ViewType) : HomeEvents()
    data class OnSearchToggle(val toggleValue : Boolean) : HomeEvents()
    data class OnSearchUpdate(val searchText : String) : HomeEvents()
    object OnRefreshInitialDates : HomeEvents()
    object OnClearSearch : HomeEvents()
}
