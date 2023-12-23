package com.designlife.justdo.common.utils

import androidx.annotation.IdRes
import com.designlife.justdo.R

object NavOptions{
    val navOptionStack = androidx.navigation.NavOptions
        .Builder()
        .setEnterAnim(R.anim.screen_enter)
        .setPopEnterAnim(R.anim.pop_screen_enter)
        .setExitAnim(R.anim.screen_exit)
        .build()

    val navOptionStackSlide = androidx.navigation.NavOptions
        .Builder()
        .setEnterAnim(R.anim.screen_slide_enter)
        .setPopEnterAnim(R.anim.screen_slide_pop_enter)
        .setExitAnim(R.anim.screen_slide_exit)
        .setPopExitAnim(R.anim.screen_slide_pop_exit)
        .build()

    fun navigatePop(@IdRes resId : Int) : androidx.navigation.NavOptions{
        return androidx.navigation.NavOptions
            .Builder()
            .setEnterAnim(R.anim.screen_enter)
            .setPopEnterAnim(R.anim.pop_screen_enter)
            .setPopUpTo(destinationId = resId,inclusive = true)
            .build()
    }
}