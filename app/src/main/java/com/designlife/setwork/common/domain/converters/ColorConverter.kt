package com.designlife.setwork.common.domain.converters

import androidx.compose.ui.graphics.Color

object ColorConverter {

    fun serializeColor(color : Color) : String{
        val red = (color.red * 255).toInt()
        val green = (color.green * 255).toInt()
        val blue = (color.blue * 255).toInt()
        val alpha = (color.alpha * 255).toInt()

        return String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
    }


    fun deserializeColor(colorString: String): Color {
        val alpha = Integer.parseInt(colorString.substring(1, 3), 16) / 255f
        val red = Integer.parseInt(colorString.substring(3, 5), 16) / 255f
        val green = Integer.parseInt(colorString.substring(5, 7), 16) / 255f
        val blue = Integer.parseInt(colorString.substring(7, 9), 16) / 255f
        return Color(red, green, blue, alpha)
    }


}