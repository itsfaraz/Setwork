package com.designlife.justdo.archive.presentation.events

import com.designlife.justdo.common.utils.enums.ViewType

sealed class ArchiveEvents{
    data class OnProgressBarToggle(val toggleValue : Boolean) : ArchiveEvents()
    object OnRefreshInitialDates : ArchiveEvents()
    data class DeleteTaskById(val id : Int) : ArchiveEvents()
    data class DeleteNoteById(val id : Long) : ArchiveEvents()
    data class DeleteDeckById(val id : Long) : ArchiveEvents()
}
