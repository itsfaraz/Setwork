package com.designlife.setwork.home.domain.usecase

import com.designlife.setwork.common.domain.calendar.DateGenerator
import java.util.Date

class LoadNextDatesSetUseCase(
    private val dateGenerator: DateGenerator
) {

    operator fun invoke() : List<Date>{
        return dateGenerator.loadNextMonth()
    }

}