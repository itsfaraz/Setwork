package com.designlife.justdo.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)

fun Shapes.cutTopRoundedCorners(radius : Dp) : RoundedCornerShape{
    return RoundedCornerShape(topStart = radius,topEnd = radius, bottomStart = 0.dp, bottomEnd = 0.dp)
}
fun Shapes.cutBottomRoundedCorners(radius : Dp) : RoundedCornerShape{
    return RoundedCornerShape(topStart = 0.dp,topEnd = 0.dp, bottomStart = radius, bottomEnd = radius)
}

fun Shapes.cutRoundedCorners(radius : Dp) : RoundedCornerShape{
    return RoundedCornerShape(topStart = 0.dp,topEnd = radius, bottomStart = radius, bottomEnd = 0.dp)
}

fun Shapes.leftRoundedCorners(radius: Dp) : RoundedCornerShape{
    return RoundedCornerShape(topStart = radius,topEnd = 0.dp, bottomStart = radius, bottomEnd = 0.dp)
}

fun Shapes.rightRoundedCorners(radius: Dp) : RoundedCornerShape{
    return RoundedCornerShape(topStart = 0.dp,topEnd = radius, bottomStart = 0.dp, bottomEnd = radius)
}

fun Shapes.zeroRoundedCorners() : RoundedCornerShape{
    return RoundedCornerShape(topStart = 0.dp,topEnd = 0.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
}