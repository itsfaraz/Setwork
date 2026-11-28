package com.designlife.setwork.settings.presentation.entity

import com.designlife.setwork.settings.presentation.enums.LoaderState

data class LoaderStatus(
    val title : String,
    val loaderState : LoaderState,
    val message : String
)
