package com.designlife.justdo.ui.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.designlife.justdo.R
import com.designlife.justdo.settings.presentation.enums.AppFontSize


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

val headerStyleFontSize = mutableStateOf<TextUnit>(16.sp)
val contentStyle_OneSize = mutableStateOf<TextUnit>(10.sp)
val buttonStyleSize = mutableStateOf<TextUnit>(12.sp)
val commonStyleSize = mutableStateOf<TextUnit>(12.sp)
val taskItemLabelStyleSize = mutableStateOf<TextUnit>(10.sp)
val taskItemStyleSize = mutableStateOf<TextUnit>(13.sp)
val noteTitleStyleSize = mutableStateOf<TextUnit>(22.sp)
val noteContentStyleSize = mutableStateOf<TextUnit>(20.sp)
val noteItemTitleStyleSize = mutableStateOf<TextUnit>(15.sp)
val noteItemContentStyleSize = mutableStateOf<TextUnit>(12.sp)
val folderTextStyleSize = mutableStateOf<TextUnit>(12.sp)
val cardTextStyleSize = mutableStateOf<TextUnit>(18.sp)
val highlightTextStyleSize = mutableStateOf<TextUnit>(8.sp)
val deckItemTitleStyleSize = mutableStateOf<TextUnit>(16.sp)
val deckItemContentStyleSize = mutableStateOf<TextUnit>(12.sp)
val attachmentTabItemTextStyleSize = mutableStateOf<TextUnit>(13.sp)
val settingPageHeaderStyleSize = mutableStateOf<TextUnit>(26.sp)
val settingHeaderStyleSize = mutableStateOf<TextUnit>(12.sp)
val settingItemStyleSize = mutableStateOf<TextUnit>(16.sp)
val pickerItemStyleSize = mutableStateOf<TextUnit>(14.sp)

val headerStyle = mutableStateOf(TextStyle(
    color = Color.Black,
    fontFamily = fontFamily,
    fontSize = headerStyleFontSize.value,
    fontWeight = FontWeight.Medium
))

val contentStyle_One = mutableStateOf(
    TextStyle(
        color = Color.Black,
        fontFamily = fontFamily,
        fontSize = contentStyle_OneSize.value,
        fontWeight = FontWeight.Medium
    )
)

val buttonStyle = mutableStateOf(TextStyle(
    color = Color.White,
    fontFamily = fontFamily,
    fontSize = buttonStyleSize.value,
    fontWeight = FontWeight.Medium
))

val taskItemLabelStyle = mutableStateOf(TextStyle(
    color = TaskItemLabelColor.value,
    fontFamily = fontFamily,
    fontSize = taskItemLabelStyleSize.value,
    fontWeight = FontWeight.Medium
))

val taskItemStyle = mutableStateOf(TextStyle(
    color = Color.Black,
    fontFamily = fontFamily,
    fontSize = taskItemStyleSize.value,
    fontWeight = FontWeight.Medium
))


val noteTitleStyle = mutableStateOf(TextStyle(
    color = Color.Black,
    fontFamily = fontFamily,
    fontSize = noteTitleStyleSize.value,
    fontWeight = FontWeight.SemiBold
))

val noteContentStyle = mutableStateOf(TextStyle(
    color = Color.Black,
    fontFamily = fontFamily,
    fontSize = noteContentStyleSize.value,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Start
))

val noteItemTitleStyle = mutableStateOf(TextStyle(
    color = Color.Black,
    fontFamily = fontFamily,
    fontSize = noteItemTitleStyleSize.value,
    fontWeight = FontWeight.SemiBold
))

val noteItemContentStyle = mutableStateOf(TextStyle(
    color = Color.Gray,
    fontFamily = fontFamily,
    fontSize = noteItemContentStyleSize.value,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Start
))

val folderTextStyle = mutableStateOf(TextStyle(
    color = Color.White,
    fontFamily = fontFamily,
    fontSize = folderTextStyleSize.value,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Start
))

val cardTextStyle = mutableStateOf(TextStyle(
    color = Color.Black,
    fontFamily = fontFamily,
    fontSize = cardTextStyleSize.value,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Center
))

val highlightTextStyle = mutableStateOf(TextStyle(
    color = Color.Gray,
    fontFamily = fontFamily,
    fontSize = highlightTextStyleSize.value,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Center
))

val deckItemTitleStyle = mutableStateOf(TextStyle(
    color = Color.Black,
    fontFamily = fontFamily,
    fontSize = deckItemTitleStyleSize.value,
    fontWeight = FontWeight.SemiBold
))

val deckItemContentStyle = mutableStateOf(TextStyle(
    color = Color.White,
    fontFamily = fontFamily,
    fontSize = deckItemContentStyleSize.value,
    fontWeight = FontWeight.Light
))

val AttachmentTabItemTextStyle = mutableStateOf(TextStyle(
    color = Color.Gray,
    fontFamily = fontFamily,
    fontSize = attachmentTabItemTextStyleSize.value,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Center
))

val SettingPageHeaderStyle = mutableStateOf(TextStyle(
    color = Color.Black,
    fontFamily = fontFamily,
    fontSize = settingPageHeaderStyleSize.value,
    fontWeight = FontWeight.Bold
))

val SettingHeaderStyle = mutableStateOf(TextStyle(
    color = ButtonHighLightPrimary.value,
    fontFamily = fontFamily,
    fontSize = settingHeaderStyleSize.value,
    fontWeight = FontWeight.SemiBold
))

val SettingItemStyle = mutableStateOf(TextStyle(
    color = ButtonHighLightPrimary.value,
    fontFamily = fontFamily,
    fontSize = settingItemStyleSize.value,
    fontWeight = FontWeight.Light
))

val PickerItemStyle = mutableStateOf(TextStyle(
    color = ButtonPrimary.value,
    fontFamily = fontFamily,
    fontSize = pickerItemStyleSize.value,
    fontWeight = FontWeight.Light
))

fun updateSystemFont(value: AppFontSize) {
    when(value){
        AppFontSize.MEDIUM -> {
            headerStyleFontSize.value = 16.sp
            contentStyle_OneSize.value = 10.sp
            buttonStyleSize.value = 12.sp
            commonStyleSize.value = 12.sp
            taskItemLabelStyleSize.value = 10.sp
            taskItemStyleSize.value = 13.sp
            noteTitleStyleSize.value = 22.sp
            noteContentStyleSize.value = 20.sp
            noteItemTitleStyleSize.value = 15.sp
            noteItemContentStyleSize.value = 12.sp
            folderTextStyleSize.value = 12.sp
            cardTextStyleSize.value = 18.sp
            highlightTextStyleSize.value = 8.sp
            deckItemTitleStyleSize.value = 16.sp
            deckItemContentStyleSize.value = 12.sp
            attachmentTabItemTextStyleSize.value = 13.sp
            settingPageHeaderStyleSize.value = 13.sp
            settingHeaderStyleSize.value = 11.sp
            settingItemStyleSize.value = 16.sp
            pickerItemStyleSize.value = 14.sp
        }
        AppFontSize.SMALL -> {
            headerStyleFontSize.value = 14.sp
            contentStyle_OneSize.value = 8.sp
            buttonStyleSize.value = 10.sp
            commonStyleSize.value = 10.sp
            taskItemLabelStyleSize.value = 8.sp
            taskItemStyleSize.value = 11.sp
            noteTitleStyleSize.value = 20.sp
            noteContentStyleSize.value = 18.sp
            noteItemTitleStyleSize.value = 13.sp
            noteItemContentStyleSize.value = 10.sp
            folderTextStyleSize.value = 10.sp
            cardTextStyleSize.value = 16.sp
            highlightTextStyleSize.value = 6.sp
            deckItemTitleStyleSize.value = 14.sp
            deckItemContentStyleSize.value = 10.sp
            attachmentTabItemTextStyleSize.value = 11.sp
            settingPageHeaderStyleSize.value = 11.sp
            settingHeaderStyleSize.value = 10.sp
            settingItemStyleSize.value = 14.sp
            pickerItemStyleSize.value = 12.sp
        }
        AppFontSize.LARGE -> {
            headerStyleFontSize.value = 18.sp
            contentStyle_OneSize.value = 12.sp
            buttonStyleSize.value = 14.sp
            commonStyleSize.value = 14.sp
            taskItemLabelStyleSize.value = 12.sp
            taskItemStyleSize.value = 15.sp
            noteTitleStyleSize.value = 24.sp
            noteContentStyleSize.value = 22.sp
            noteItemTitleStyleSize.value = 17.sp
            noteItemContentStyleSize.value = 14.sp
            folderTextStyleSize.value = 14.sp
            cardTextStyleSize.value = 20.sp
            highlightTextStyleSize.value = 10.sp
            deckItemTitleStyleSize.value = 18.sp
            deckItemContentStyleSize.value = 14.sp
            attachmentTabItemTextStyleSize.value = 15.sp
            settingPageHeaderStyleSize.value = 15.sp
            settingHeaderStyleSize.value = 12.sp
            settingItemStyleSize.value = 17.sp
            pickerItemStyleSize.value = 16.sp
        }
    }
}