package com.designlife.setwork.home.domain.usecase

import com.designlife.setwork.common.domain.calendar.DateGenerator

class LoadIntialDatesUseCase(
    private val dateGenerator: DateGenerator
) {

    suspend operator fun invoke(){
        dateGenerator.setupDates()
    }

}