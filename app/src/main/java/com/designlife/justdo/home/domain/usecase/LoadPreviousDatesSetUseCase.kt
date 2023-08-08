package com.designlife.justdo.home.domain.usecase

import com.designlife.justdo.common.domain.calendar.DateGenerator
import java.util.Date

class LoadPreviousDatesSetUseCase(
    private val dateGenerator: DateGenerator
) {

    operator fun invoke() : List<Date>{
        return dateGenerator.loadPreviousMonth()
    }

}