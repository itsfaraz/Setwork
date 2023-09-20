package com.designlife.justdo.deck.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.common.domain.entities.FlashCard
import com.designlife.justdo.ui.theme.cardTextStyle
import com.designlife.justdo.ui.theme.headerStyle

@Composable
fun CardCreateComponent(
    modifier: Modifier = Modifier,
    deckTheme: Color,
    card: FlashCard,
    onDeleteEvent: () -> Unit,
    onExpandEvent: () -> Unit,
) {
    val screenWidth = LocalDensity.current.run {
        (LocalConfiguration.current.screenWidthDp * density).toInt()
    }
    val itemWidthFraction = 0.3f // Adjust this fraction as needed
    Card(
        modifier = modifier
            .padding(horizontal = 15.dp)
            .width((screenWidth * itemWidthFraction).dp)
            .fillMaxHeight(.8F)
            .clickable {
                onExpandEvent()
            },
        backgroundColor = Color.White,
        elevation = 12.dp,
        shape = RoundedCornerShape(8.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(color = deckTheme)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.wrapContentWidth(),
                    text = "Start Card",
                    style = headerStyle.copy(fontSize = 14.sp, color = Color.White),
                    textAlign = TextAlign.Start
                )
                IconButton(
                    modifier = Modifier.size(22.dp),
                    onClick = { onDeleteEvent() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon",
                        tint = Color.White
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (card.frontContent.isEmpty()) "Sample text flash card. Click to add content." else card.frontContent,
                    style = cardTextStyle,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}