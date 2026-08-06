package com.designlife.justdo.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.common.presentation.components.appBackground
import com.designlife.justdo.common.utils.entity.SettingItem
import com.designlife.justdo.ui.theme.ButtonHighLightPrimary
import com.designlife.justdo.ui.theme.SettingHeaderStyle
import com.designlife.justdo.ui.theme.SettingPageHeaderStyle
import com.designlife.justdo.ui.theme.TypographyColor
import com.designlife.justdo.ui.theme.UIComponentBackground
import com.designlife.justdo.ui.theme.settingHeaderStyleSize

@Composable
fun Settings(
    iconList: List<SettingItem>,
    pickerState: Boolean,
    loaderState: Boolean,
    onSwipeLeftEvent: () -> Unit,
    onSwipeRightEvent: () -> Unit,
    onDefaultScreenEvent: () -> Unit,
    onAppThemeEvent: () -> Unit,
    onFontSizeEvent: () -> Unit,
    onListHeightEvent: () -> Unit,
    onImportEvent: () -> Unit,
    onExportEvent: () -> Unit,
    onHelpEvent: () -> Unit,
    onFeedbackEvent: () -> Unit,
    onSoftwareUpdateEvent: () -> Unit,
    onGeneralSettingItemClick: () -> Unit,
    onBackupSettingItemClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount > 0) {
                        onSwipeLeftEvent()
                    } else {
                        onSwipeRightEvent()
                    }
                }
            }
            .fillMaxSize()
            .background(UIComponentBackground.value)
            .alpha(if (pickerState || loaderState) 0.7F else 1F)
            .blur(radius = if (pickerState || loaderState) 7.dp else 0.dp)
            .appBackground(enable = true)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                modifier = Modifier.padding(start = 6.dp, top = 6.dp),
                text = "Settings",
                style = SettingPageHeaderStyle.value.copy(TypographyColor.value),
                fontSize = 26.sp
            )
            LazyColumn(
                modifier = Modifier
                    .padding(start = 6.dp, end = 6.dp)
                    .fillMaxSize()
            ) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    DividerLiner()
                    Spacer(modifier = Modifier.height(15.dp))
                    SettingHeader(headerTitle = "General")
                    Spacer(modifier = Modifier.height(15.dp))
                }

                iconList.getOrNull(0)?.let { item ->
                    item {
                        SettingItemComponent(drawableIcon = item.icon, title = item.title) {
                            onDefaultScreenEvent()
                            onGeneralSettingItemClick()
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
                iconList.getOrNull(1)?.let { item ->
                    item {
                        SettingItemComponent(drawableIcon = item.icon, title = item.title) {
                            onAppThemeEvent()
                            onGeneralSettingItemClick()
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
                iconList.getOrNull(2)?.let { item ->
                    item {
                        SettingItemComponent(drawableIcon = item.icon, title = item.title) {
                            onFontSizeEvent()
                            onGeneralSettingItemClick()
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
                iconList.getOrNull(3)?.let { item ->
                    item {
                        SettingItemComponent(drawableIcon = item.icon, title = item.title) {
                            onListHeightEvent()
                            onGeneralSettingItemClick()
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    DividerLiner()
                    Spacer(modifier = Modifier.height(15.dp))
                    SettingHeader(headerTitle = "Backup")
                    Spacer(modifier = Modifier.height(15.dp))
                }

                iconList.getOrNull(4)?.let { item ->
                    item {
                        SettingItemComponent(drawableIcon = item.icon, title = item.title) {
                            onImportEvent()
                            onBackupSettingItemClick()
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
                iconList.getOrNull(5)?.let { item ->
                    item {
                        SettingItemComponent(drawableIcon = item.icon, title = item.title) {
                            onExportEvent()
                            onBackupSettingItemClick()
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    DividerLiner()
                    Spacer(modifier = Modifier.height(15.dp))
                    SettingHeader(headerTitle = "More")
                    Spacer(modifier = Modifier.height(15.dp))
                }

                iconList.getOrNull(6)?.let { item ->
                    item {
                        SettingItemComponent(drawableIcon = item.icon, title = item.title) {
                            onHelpEvent()
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
                iconList.getOrNull(7)?.let { item ->
                    item {
                        SettingItemComponent(drawableIcon = item.icon, title = item.title) {
                            onFeedbackEvent()
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
                iconList.getOrNull(8)?.let { item ->
                    item {
                        SettingItemComponent(drawableIcon = item.icon, title = item.title) {
                            onSoftwareUpdateEvent()
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    SettingHeader(headerTitle = "VERSION 1.0.1")
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun SettingHeader(
    headerTitle: String
) {
    Text(
        text = headerTitle.uppercase(),
        style = SettingHeaderStyle.value,
        fontSize = settingHeaderStyleSize.value
    )
}

@Composable
fun DividerLiner() {
    Spacer(
        modifier = Modifier
            .height(0.2.dp)
            .fillMaxWidth()
            .background(color = ButtonHighLightPrimary.value)
    )
}