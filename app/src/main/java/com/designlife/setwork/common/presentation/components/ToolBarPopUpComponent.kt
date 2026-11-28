package com.designlife.setwork.common.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.designlife.setwork.R
import com.designlife.setwork.ui.theme.ComponentBackground
import com.designlife.setwork.ui.theme.LightButtonPrimary
import com.designlife.setwork.ui.theme.NotePopUpItemStyle
import com.designlife.setwork.ui.theme.TypographyColor

@Composable
fun ToolBarPopUpComponent(
    onCloseEvent :  () -> Unit,
    onCopyEvent :  () -> Unit,
    onDuplicateEvent :  () -> Unit,
    onExportPdfEvent : () -> Unit,
    onExportPngEvent : () -> Unit,
    onDeleteEvent : () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(color = ComponentBackground.value)
            .padding(top = 28.dp)
            .height(340.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = ComponentBackground.value, shape = RoundedCornerShape(16.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(end = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {onCloseEvent()}) {
                    Icon(modifier = Modifier.size(18.dp), imageVector = Icons.Default.Clear, contentDescription = "Cancel", tint = Color.DarkGray)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            PopItem("Copy", R.drawable.ic_copy_all) { onCopyEvent() }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(modifier = Modifier.fillMaxWidth(.96F), thickness = 2.dp, color = LightButtonPrimary)
            Spacer(modifier = Modifier.height(10.dp))
            PopItem("Export pdf", R.drawable.ic_export_pdf) { onExportPdfEvent() }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(modifier = Modifier.fillMaxWidth(.96F), thickness = 2.dp, color = LightButtonPrimary)
            Spacer(modifier = Modifier.height(10.dp))
            PopItem("Export png", R.drawable.ic_export_png) { onExportPngEvent() }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(modifier = Modifier.fillMaxWidth(.96F), thickness = 2.dp, color = LightButtonPrimary)
            Spacer(modifier = Modifier.height(10.dp))
            PopItem("Duplicate", R.drawable.ic_duplicate) { onDuplicateEvent() }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(modifier = Modifier.fillMaxWidth(.96F), thickness = 2.dp, color = LightButtonPrimary)
            Spacer(modifier = Modifier.height(10.dp))
            PopItem("Delete", R.drawable.ic_delete) { onDeleteEvent() }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun PopItem(
    itemTitle: String,
    @DrawableRes iconRes: Int,
    onItemEvent: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(30.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(color = ComponentBackground.value)
            .rippleClickable {
                onItemEvent()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = itemTitle, style = NotePopUpItemStyle.value.copy(color = TypographyColor.value), textAlign = TextAlign.Start)
        Column(
            modifier = Modifier
                .size(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.size(18.dp),
                painter = painterResource(iconRes),
                contentDescription = "Tooltip Icon"
            )
        }
    }
}