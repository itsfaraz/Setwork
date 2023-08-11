package com.designlife.justdo.common.domain.calendar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale


class IDateGenerator : DateGenerator {

    private val _allDateList : MutableStateFlow<ArrayList<Date>> = MutableStateFlow(arrayListOf())
    private var previousMonth = getCurrentMonth()
    private var nextMonth = getCurrentMonth()
    private var prevYear = getCurrentYear()
    private var nextYear = getCurrentYear()


    override fun getDateList(): StateFlow<List<Date>>  {
        return _allDateList
    }

    override fun loadPreviousMonth() : List<Date>{
        val preservedMonth = previousMonth
        previousMonth -= 1 % 12
        if (previousMonth == 0){
            prevYear -= 1;
            previousMonth = 12
        }
        val previousMonthList =  ArrayList<Date>()
        val firstDayIndex = getFirstDayOfMonth(previousMonth,prevYear)
        for (days in 1..totalDaysInAMonth(previousMonth,prevYear)){
            val dayIndex = (firstDayIndex + (days-1)) % daysList.size
            previousMonthList.add(getDateBy(days, previousMonth, prevYear))
        }
        return previousMonthList
    }

    override fun loadNextMonth() : List<Date>{
        val preservedMonth = nextMonth
        nextMonth += 1
        if (nextMonth == 13){
            nextYear += 1
            nextMonth = 1
        }
        val nextMonthList =  arrayListOf<Date>()
        val firstDayIndex = getFirstDayOfMonth(nextMonth,nextYear)
        for (days in 1..totalDaysInAMonth(nextMonth,nextYear)){
            val dayIndex = (firstDayIndex + (days-1)) % daysList.size
            val date = getDateBy(days, nextMonth, nextYear)
            nextMonthList.add(date)
        }
//        _allDateList.value.addAll(nextMonthList)
        return nextMonthList
    }





    override fun setupDates() {
        val month = getCurrentMonth()
        val year = getCurrentYear()
        val firstDayIndex = getFirstDayOfMonth(month,year)
        val datesList =  arrayListOf<Date>()
        for (days in 1..totalDaysInAMonth(month,year)){
            val dayIndex = (firstDayIndex + (days-1)) % daysList.size
            val date = getDateBy(days,month,year)
            datesList.add(date)

        }
        _allDateList.value = datesList
    }



    companion object {
        val daysList =  listOf<String>( "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        private val fullDayNameList =  listOf<String>( "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val monthsList =  listOf<String>( "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        private val fillMonthNamesList =  listOf<String>( "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

        private fun getNoLength(value : Int) : Int{
            return (Math.log(value as Double)+1).toInt()
        }

        public fun getToday() : Date{
            return getDateBy(getCurrentDay(), getCurrentMonth(), getCurrentYear())
        }


        private fun getDateBy(day : Int, month : Int, year : Int) : Date{
            val dateString = "$day/$month/$year"
            val formate = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            return formate.parse(dateString)!!
        }

        public fun getDayInfoFrom(date : Date) : Pair<Int,String>{
            val calendar = Calendar.getInstance()
            calendar.time = date
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            return Pair(day, daysList[calendar.get(Calendar.DAY_OF_WEEK)-1])
        }

        public fun getFullDayInfo(date : Date) : Pair<String,Int>{
            val calendar = Calendar.getInstance()
            calendar.time = date
            val dayName = fullDayNameList[calendar.get(Calendar.DAY_OF_WEEK)-1]
            val week = calendar.get(Calendar.WEEK_OF_MONTH)
            return Pair(dayName,week)
        }

        public fun getDayMonthInfo(date : Date) : Pair<Int,String>{
            val calendar = Calendar.getInstance()
            calendar.time = date
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = fillMonthNamesList[calendar.get(Calendar.MONTH)]
            return Pair(day,month)
        }

        fun getMonthFromDate(date: Date): String {
            val format = SimpleDateFormat("MMM", Locale.ENGLISH)
            return format.format(date)
        }

        private fun getFirstDayOfMonth(month : Int,year : Int) : Int{
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH,month - 1)
            calendar.set(Calendar.YEAR,year)
            calendar.set(Calendar.DAY_OF_MONTH,1)
            return calendar.get(Calendar.DAY_OF_WEEK) - 1
        }

        private fun getCurrentMonth() : Int{
            val today = Date(System.currentTimeMillis());
            val calendar = Calendar.getInstance()
            calendar.time = today
            return calendar.get(Calendar.MONTH)+1
        }

        public fun getCurrentYear() : Int{
            val today = Date(System.currentTimeMillis());
            val calendar = Calendar.getInstance()
            calendar.time = today
            return calendar.get(Calendar.YEAR)
        }

        private fun totalDaysInAMonth(month : Int,year : Int) : Int{
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month - 1)
            return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        private fun getDayName(index : Int) : String{
            return daysList.get(index)
        }

        private fun getCurrentDay() : Int{
            val date = Date(System.currentTimeMillis())
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar.get(Calendar.DAY_OF_MONTH)
        }

        fun getYearFromDate(date: Date): String {
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar.get(Calendar.YEAR).toString()
        }

        fun getEpochFromDate(date : Date) : Long{
            return date.time
        }

        fun getDateFromEpoch(timeMillis : Long) : Date{
            return Date(timeMillis)
        }

        fun getGracefullyDateFrom(day : Int,month : Int,year : Int) : String{
            val calendar = Calendar.getInstance()
            calendar.set(year, month - 1, day)
            val dateFormat = SimpleDateFormat("EEE dd MMM yyyy", Locale.ENGLISH)
            return dateFormat.format(calendar.time)
        }

        fun getGracefullyDateFromDate(date : Date) : String{
            val calendar = Calendar.getInstance()
            calendar.time = date
            val dateFormat = SimpleDateFormat("EEE dd MMM yyyy", Locale.ENGLISH)
            return dateFormat.format(calendar.time)
        }

        fun getGracefullyTimeFromEpoch(epoch : Long) : String{
            val date = Date(epoch) // Convert epoch value from seconds to milliseconds
            val dateFormat = SimpleDateFormat("hh:mm a", Locale.US)
            return dateFormat.format(date)
        }

        fun getGracefullyTimeFrom(hour : Int,minute : Int) : String{
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
            val dateFormat = SimpleDateFormat("hh:mm a", Locale.US)
            return dateFormat.format(calendar.time)
        }

        fun isTodayInEnd(currentDate : Date) : Boolean{
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            val day  = calendar.get(Calendar.DAY_OF_MONTH)
            if (day in 0..6 || day in 25..31)
                return true
            return false
        }

        fun getDayFromDate(date : Date) : Int{
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar.get(Calendar.DAY_OF_MONTH)
        }

        fun getLastDayOfMonthFromDate(date : Date) : Int{
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        fun checkIsWorkingDay(date : Date) : Boolean{
            val calendar = Calendar.getInstance()
            calendar.time = date
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            return dayOfWeek != Calendar.SUNDAY && dayOfWeek != Calendar.SATURDAY
        }

        fun checkDays(orignalDate : Date,newDate : Date) : Boolean{
            val calendar = Calendar.getInstance()
            calendar.time = orignalDate
            val day1 = calendar.get(Calendar.DAY_OF_MONTH)
            calendar.time = newDate
            val day2 = calendar.get(Calendar.DAY_OF_MONTH)
            return daysList[day1%7].equals(daysList[day2%7])
        }

        fun getMonthNo(date : Date) : Int{
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar.get(Calendar.MONTH)
        }

        fun getFormattedDate(date : Date) : String{
            val calendar = Calendar.getInstance()
            calendar.time = date
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            return dateFormat.format(calendar.time)
        }
        fun getEpochForDate(date: Date): Long {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            return calendar.timeInMillis / 1000
        }

        fun getDateFromDate(date: Date): Date {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            return calendar.time
        }

    }

}

