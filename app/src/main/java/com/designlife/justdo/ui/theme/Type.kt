package com.designlife.justdo.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.designlife.justdo.R
import com.designlife.justdo.home.presentation.events.HomeEvents


// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)

val fontFamily = FontFamily(
    Font(R.font.inter_regular),
    Font(R.font.inter_medium, weight = FontWeight.Medium),
    Font(R.font.inter_black, weight = FontWeight.Normal),
    Font(R.font.inter_bold, weight = FontWeight.Bold),
    Font(R.font.inter_extrabold, weight = FontWeight.ExtraBold),
    Font(R.font.inter_extralight, weight = FontWeight.ExtraLight),
    Font(R.font.inter_light, weight = FontWeight.Light),
    Font(R.font.inter_semibold, weight = FontWeight.SemiBold),
    Font(R.font.inter_thin, weight = FontWeight.Thin)
)

val headerStyle = TextStyle(
    color = Color.Black,
    fontFamily = fontFamily,
    fontSize = 16.sp,
    fontWeight = FontWeight.Medium
)

val contentStyle_One = TextStyle(
    color = Color.Black,
    fontFamily = fontFamily,
    fontSize = 10.sp,
    fontWeight = FontWeight.Medium
)

val buttonStyle = TextStyle(
    color = Color.White,
    fontFamily = fontFamily,
    fontSize = 12.sp,
    fontWeight = FontWeight.Medium
)

val taskItemLabelStyle =  TextStyle(
    color = TaskItemLabelColor,
    fontFamily = fontFamily,
    fontSize = 10.sp,
    fontWeight = FontWeight.Medium
)

val taskItemStyle =  TextStyle(
    color = Color.Black,
    fontFamily = fontFamily,
    fontSize = 13.sp,
    fontWeight = FontWeight.Medium
)


val noteTitleStyle =  TextStyle(
    color = Color.Black,
    fontFamily = fontFamily,
    fontSize = 22.sp,
    fontWeight = FontWeight.SemiBold
)

val noteContentStyle =  TextStyle(
    color = Color.Black,
    fontFamily = fontFamily,
    fontSize = 20.sp,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Start
)

val noteItemTitleStyle =  TextStyle(
    color = Color.Black,
    fontFamily = fontFamily,
    fontSize = 15.sp,
    fontWeight = FontWeight.SemiBold
)

val noteItemContentStyle =  TextStyle(
    color = Color.Gray,
    fontFamily = fontFamily,
    fontSize = 12.sp,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Start
)

val folderTextStyle = TextStyle(
    color = Color.White,
    fontFamily = fontFamily,
    fontSize = 12.sp,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Start
)

val cardTextStyle = TextStyle(
    color = Color.Black,
    fontFamily = fontFamily,
    fontSize = 18.sp,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Center
)