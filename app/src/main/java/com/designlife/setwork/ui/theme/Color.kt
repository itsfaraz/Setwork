package com.designlife.setwork.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color


val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)
val PaleYellow = Color(0xFF03DAC5)



// Light
val LightButtonPrimary = Color(0xFF7388FB)
val LightDangerButton = Color(0xFFF73458)
val LightPrimaryCardColor = Color(0xFF27F7D6)
val LightButtonHighLightPrimary = Color(0xFFB3AFAF)
val LightPrimaryColor1 = Color(0xFFEEFDF9)
val LightPrimaryBackgroundCategoryColor = Color(0xFFDBE1DF)
val LightPrimaryBackgroundColor = Color(0xFFF3F3F3)
val LightTaskItemLabelColor = Color(0xFFA0A0A0)
val LightSelectedCategoryBackground = Color(0xFF4053BE)
val LightPrimaryColorHome1 = Color(0xFF219AD2)
val LightPrimaryColorHome2 = Color(0xFFFFFFFF)
val LightPrimaryDark = Color(0xFF1A1B1B)
val LightCustomGrey = Color(0xFFD9D9D9)
val LightComponent = Color(0xFFFFFFFF)
val LightUIComponent = Color(0xFFF3F3F3)
val LightTypography = Color(0xFF000000)
val LightTypographyDim = Color(0xFF000000)
val LightIcon = Color(0xFFFFFFFF)

// Dark
val DarkPrimaryCardColor = Color(0xFF27F7D6)
val DarkButtonHighLightPrimary = Color(0xFFB3AFAF)
val DarkPrimaryColor1 = Color(0xFFEEFDF9)
val DarkPrimaryBackgroundCategoryColor = Color(0xFFDBE1DF)
val DarkPrimaryBackgroundColor = Color(0xFF080707)
val DarkTaskItemLabelColor = Color(0xFFA0A0A0)
val DarkSelectedCategoryBackground = Color(0xFF4053BE)
val DarkPrimaryColorHome1 = Color(0xFF46DCF0)
val DarkPrimaryColorHome2 = Color(0xFF4D4949)
val DarkPrimaryLight = Color(0xFF1A1B1B)
val DarkCustomGrey = Color(0xFFD9D9D9)
val DarkComponent = Color(0xFF000000)
val DarkUIComponent = Color(0xFF161616)
val DarkTypography = Color(0xFFFFFFFF)
val DarkTypographyDim = Color(0xFFFFFFFF)
val DarkIcon = Color(0xFF000000)

// Color Variables
var ButtonPrimary =  mutableStateOf(Color(0xFF7388FB))
var DangerButton =  mutableStateOf(Color(0xFFF73458))
var PrimaryCardColor = mutableStateOf(LightPrimaryCardColor)
var ButtonHighLightPrimary = mutableStateOf(LightButtonHighLightPrimary)
var PrimaryColor1 =  mutableStateOf(LightPrimaryColor1)
var PrimaryBackgroundCategoryColor = mutableStateOf(LightPrimaryBackgroundCategoryColor)
var PrimaryBackgroundColor = mutableStateOf(LightPrimaryBackgroundColor)
var TaskItemLabelColor = mutableStateOf(LightTaskItemLabelColor)
var SelectedCategoryBackground = mutableStateOf(LightSelectedCategoryBackground)
var PrimaryColorHome1 = mutableStateOf(LightPrimaryColorHome1)
var PrimaryColorHome2 = mutableStateOf(LightPrimaryColorHome2)
var PrimaryDark = mutableStateOf(LightPrimaryDark)
var CustomGrey =  mutableStateOf(LightCustomGrey)
var ComponentBackground =  mutableStateOf(LightComponent)
var UIComponentBackground =  mutableStateOf(LightUIComponent)
var TypographyColor =  mutableStateOf(LightTypography)
var TypographyColorDim =  mutableStateOf(LightTypographyDim)
var IconColor =  mutableStateOf(LightIcon)


val ColorPaletteItem1 = Color(0xFFFB9494)
val ColorPaletteItem2 = Color(0xFF5779F4)
val ColorPaletteItem3 = Color(0xFFF157F4)
val ColorPaletteItem4 = Color(0xFFFF6F32)
val ColorPaletteItem5 = Color(0xFFF82A2A)
val ColorPaletteItem6 = Color(0xFF688869)
val ColorPaletteItem7 = Color(0xFF0CDE6C)
val ColorPaletteItem8 = Color(0xFFE6F903)
val ColorPaletteItem9 = Color(0xFF001279)
val ColorPaletteItem10 = Color(0xFF7BBBC3)
val ColorPaletteItem11 = Color(0xFFB9FF68)
val ColorPaletteItem12 = Color(0xFFFFA92A)
val ColorPaletteItem13 = Color(0xFFEB73FF)
val ColorPaletteItem14 = Color(0xFFF80054)
val ColorPaletteItem15 = Color(0xFFFF6F64)
val ColorPaletteItem16 = Color(0xFF00FFE7)

fun updateSystemColor(isSystemInDarkMode : Boolean){
    ButtonPrimary.value =  Color(0xFF7388FB)
    DangerButton.value =  Color(0xFFF73458)
    PrimaryCardColor.value = if (isSystemInDarkMode) DarkPrimaryCardColor else LightPrimaryCardColor
    ButtonHighLightPrimary.value = if (isSystemInDarkMode) DarkButtonHighLightPrimary else LightButtonHighLightPrimary
    PrimaryColor1.value = if (isSystemInDarkMode) DarkPrimaryColor1 else LightPrimaryColor1
    PrimaryBackgroundCategoryColor.value = if (isSystemInDarkMode) DarkPrimaryBackgroundCategoryColor else LightPrimaryBackgroundCategoryColor
    PrimaryBackgroundColor.value = if (isSystemInDarkMode) DarkPrimaryBackgroundColor else LightPrimaryBackgroundColor
    TaskItemLabelColor.value = if (isSystemInDarkMode) DarkTaskItemLabelColor else LightTaskItemLabelColor
    SelectedCategoryBackground.value = if (isSystemInDarkMode) DarkSelectedCategoryBackground else LightSelectedCategoryBackground
    PrimaryColorHome1.value = if (isSystemInDarkMode) DarkPrimaryColorHome1 else LightPrimaryColorHome1
    PrimaryColorHome2.value = if (isSystemInDarkMode) DarkPrimaryColorHome2 else LightPrimaryColorHome2
    PrimaryDark.value = if (isSystemInDarkMode) DarkPrimaryLight else LightPrimaryDark
    CustomGrey.value = if (isSystemInDarkMode) DarkCustomGrey else LightCustomGrey
    CustomGrey.value = if (isSystemInDarkMode) DarkCustomGrey else LightCustomGrey
    ComponentBackground.value = if (isSystemInDarkMode) DarkComponent else LightComponent
    UIComponentBackground.value = if (isSystemInDarkMode) DarkUIComponent else LightUIComponent
    TypographyColor.value = if (isSystemInDarkMode) DarkTypography else LightTypography
    TypographyColorDim.value = if (isSystemInDarkMode) DarkTypographyDim else LightTypographyDim
    IconColor.value = if (isSystemInDarkMode) LightIcon else DarkIcon
}