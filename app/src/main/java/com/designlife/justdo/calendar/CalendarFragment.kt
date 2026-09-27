package com.designlife.justdo.calendar

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.designlife.justdo.calendar.data.dto.LocalEvent
import com.designlife.justdo.calendar.presentation.components.CalendarComponent
import com.designlife.justdo.calendar.presentation.components.CalendarHeaderComponent
import com.designlife.justdo.common.utils.enums.ViewType
import java.util.Calendar
import java.util.Date

class CalendarFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val progressState = remember { mutableStateOf(true) }
                val dateListState = rememberLazyListState()
                val calendar = Calendar.getInstance()
                val dateList: List<Date> = (1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                    .map { day ->
                        Calendar.getInstance().apply {
                            set(Calendar.DAY_OF_MONTH, day)
                        }.time
                    }
                val localEvents = emptyList<LocalEvent>()
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        CalendarHeaderComponent(
                            today = Date(System.currentTimeMillis()),
                            viewType = ViewType.CALENDAR,
                            dateList = dateList,
                            listState = dateListState,
                            isLoading = progressState.value,
                            onMenuEvent  = {},
                            onTodayEvent  = {},
                            onDateEvent = {}
                        )
                        CalendarComponent(
                            localEvents = localEvents
                        ){ eventId ->
                            when(eventId){
                                else -> {}
                            }
                        }


                    }
                }
            }
        }
    }
}