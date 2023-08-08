package com.designlife.justdo.common.domain.calendar

import kotlinx.coroutines.flow.StateFlow
import java.util.Date

interface DateGenerator {
    fun setupDates()
    fun getDateList() : StateFlow<List<Date>>
    fun loadPreviousMonth() : List<Date>
    fun loadNextMonth() : List<Date>
}