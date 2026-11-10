package com.designlife.justdo.calendar.presentation.events

import com.designlife.justdo.common.utils.enums.ViewType

sealed class CalendarEvents{
    data class OnIndexSelected(val index : Int) : CalendarEvents()
    data object OnTodayEvent : CalendarEvents()
    data class HighlightTodoByDate(val visibleIndex : Int) : CalendarEvents()
    data class HighlightDateByEvent(val epoch : Long) : CalendarEvents()
    data class OnCategorySortSelected(val categoryIndex : Int) : CalendarEvents()
    data class OnProgressBarToggle(val toggleValue : Boolean) : CalendarEvents()
    data class OnViewChange(val view : ViewType) : CalendarEvents()
    data class OnSearchToggle(val toggleValue : Boolean) : CalendarEvents()
    data class OnSearchUpdate(val searchText : String) : CalendarEvents()
    object OnRefreshInitialDates : CalendarEvents()
    object OnClearSearch : CalendarEvents()
    data class OnImportEvent(val data : String) : CalendarEvents()
}
