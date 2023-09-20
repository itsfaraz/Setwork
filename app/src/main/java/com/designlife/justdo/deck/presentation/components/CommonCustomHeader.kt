package com.designlife.justdo.deck.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.designlife.justdo.ui.theme.Shapes
import com.designlife.justdo.ui.theme.TaskItemLabelColor
import com.designlife.justdo.ui.theme.cutBottomRoundedCorners
import com.designlife.justdo.ui.theme.headerStyle

@Composable
fun DeckHeader(
    headerTitle: String,
    onTitleChange: (newTitle: String) -> Unit,
    onCloseEvent: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(Shapes.cutBottomRoundedCorners(15.dp))
            .background(Color.White),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onCloseEvent() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon")
            }
            Spacer(modifier = Modifier.width(10.dp))
            BasicTextField(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .fillMaxWidth()
                    .background(color = Color.Transparent),
                value = headerTitle,
                onValueChange = {
                    onTitleChange(it)
                },
                singleLine = true,
                textStyle = headerStyle
            ) { innerField ->
                if (headerTitle.isEmpty()) {
                    Text(text = "Deck Name ...", color = TaskItemLabelColor)
                }
                innerField()
            }
        }
    }

}