package com.designlife.justdo.common.domain.repeat

import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.entities.Todo
import com.designlife.justdo.common.utils.enums.RepeatType
import java.util.Calendar
import java.util.Date

class RepeatRepository(

) {

    private val monthQuadrant = listOf<String>("first","second","third","last","fifth")

    fun getRepeatList(currentDate : Date) : List<Pair<String,RepeatType>>{
        val repeatList = arrayListOf<Pair<String,RepeatType>>()
        repeatList.add(Pair("Does not repeat",RepeatType.NO_REPEAT))
        repeatList.add(Pair("Daily",RepeatType.DAILY))
        val(dayName,week) = IDateGenerator.getFullDayInfo(currentDate)
        repeatList.add(Pair("Weekly on $dayName",RepeatType.WEEKLY_ON_DAY))
        repeatList.add(Pair("Monthly on ${monthQuadrant[week-1]} $dayName",RepeatType.MONTH_ON_DAY))
        val(day,month) = IDateGenerator.getDayMonthInfo(currentDate)
        repeatList.add(Pair("Annually on $month $day",RepeatType.ANNUALLY_ON_MONTH))
        repeatList.add(Pair("Every working day",RepeatType.EVERY_WORKING_DAY))
        return repeatList
    }

    fun createRepeatedEvents(task : Todo,repeatType : RepeatType,eventDate : Date) : List<Todo>{
        return when(repeatType){
            RepeatType.NO_REPEAT -> createNoRepeatTask(task,eventDate)
            RepeatType.DAILY -> createDailyTask(task,eventDate)
            RepeatType.EVERY_WORKING_DAY -> createEveryWorkingDayTask(task,eventDate)
            RepeatType.WEEKLY_ON_DAY -> createWeeklyTask(task,eventDate)
            RepeatType.ANNUALLY_ON_MONTH -> createAnuallyTask(task,eventDate)
            RepeatType.MONTH_ON_DAY -> createOnMonthTask(task,eventDate)
        }
    }

    private var todoList = arrayListOf<Todo>()

    private fun createOnMonthTask(task: Todo, eventDate: Date): List<Todo> {
        todoList.clear()
        val monthStart = IDateGenerator.getMonthNo(eventDate)
        val monthEnd = 11
        val calendar = Calendar.getInstance()
        calendar.time = eventDate
        for (month in monthStart..monthEnd){
            calendar.set(Calendar.MONTH,month)
            todoList.add(task.copy(
                isRepeated = eventDate == calendar.time,
                date = calendar.time
            ))
        }
        return todoList
    }

    private fun createAnuallyTask(task: Todo, eventDate: Date): List<Todo> {
        todoList.clear()
        val dayStart = IDateGenerator.getDayFromDate(eventDate)
        val lastDay = IDateGenerator.getLastDayOfMonthFromDate(eventDate)
        val calendar = Calendar.getInstance()
        calendar.time = eventDate
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        calendar.set(year,month,1,eventDate.hours,eventDate.minutes,eventDate.seconds)
        for (yearIdx in 0..4){
            calendar.set(Calendar.YEAR,year+yearIdx)
            todoList.add(task.copy(
                isRepeated = eventDate.year == calendar.time.year,
                date = calendar.time
            ))
        }
        return todoList
    }

    private fun createWeeklyTask(task: Todo, eventDate: Date): List<Todo> {
        todoList.clear()
        val dayStart = IDateGenerator.getDayFromDate(eventDate)
        val lastDay = IDateGenerator.getLastDayOfMonthFromDate(eventDate)
        val calendar = Calendar.getInstance()
        calendar.time = eventDate
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        calendar.set(year,month,1,eventDate.hours,eventDate.minutes,eventDate.seconds)
        for (day in dayStart..lastDay){
            calendar.set(Calendar.DAY_OF_MONTH,day)
            val isDaysSame = IDateGenerator.checkDays(calendar.time,eventDate)
            if(isDaysSame){
                todoList.add(task.copy(
                    isRepeated = eventDate == calendar.time,
                    date = calendar.time
                ))
            }
        }
        return todoList
    }

    private fun createEveryWorkingDayTask(task: Todo, eventDate: Date): List<Todo> {
        todoList.clear()
        val dayStart = IDateGenerator.getDayFromDate(eventDate)
        val lastDay = IDateGenerator.getLastDayOfMonthFromDate(eventDate)
        val calendar = Calendar.getInstance()
        calendar.time = eventDate
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        calendar.set(year,month,1,eventDate.hours,eventDate.minutes,eventDate.seconds)
        for (day in dayStart..lastDay){
            calendar.set(Calendar.DAY_OF_MONTH,day)
            val isWorkingDay = IDateGenerator.checkIsWorkingDay(calendar.time)
            if (isWorkingDay){
                todoList.add(task.copy(
                    isRepeated = eventDate == calendar.time,
                    date = calendar.time
                ))
            }
        }
        return todoList
    }

    private fun createDailyTask(task: Todo, eventDate: Date): List<Todo> {
        todoList.clear()
        val dayStart = IDateGenerator.getDayFromDate(eventDate)
        val lastDay = IDateGenerator.getLastDayOfMonthFromDate(eventDate)
        val calendar = Calendar.getInstance()
        calendar.time = eventDate
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        calendar.set(year,month,1,eventDate.hours,eventDate.minutes,eventDate.seconds)
        for (day in dayStart..lastDay){
            calendar.set(Calendar.DAY_OF_MONTH,day)
            todoList.add(task.copy(
                isRepeated = eventDate == calendar.time,
                date = calendar.time
            ))
        }
        return todoList
    }

    private fun createNoRepeatTask(task: Todo, eventDate: Date): List<Todo> {
        todoList.clear()
        todoList.add(task.copy(
            date = eventDate
        ))
        return todoList
    }


}