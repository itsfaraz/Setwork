package com.designlife.justdo.home.domain.usecase

import com.designlife.justdo.common.domain.calendar.DateGenerator
import java.util.Date

class LoadNextDatesSetUseCase(
    private val dateGenerator: DateGenerator
) {

    operator fun invoke() : List<Date>{
        return dateGenerator.loadNextMonth()
    }

}