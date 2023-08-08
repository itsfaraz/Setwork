package com.designlife.justdo.home.presentation.events

sealed class HomeEvents{
    data class OnIndexSelected(val index : Int) : HomeEvents()
}
