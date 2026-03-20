package com.designlife.justdo.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.entities.Deck
import com.designlife.justdo.ui.theme.Shapes
import com.designlife.justdo.ui.theme.deckItemContentStyle
import com.designlife.justdo.ui.theme.deckItemContentStyleSize
import com.designlife.justdo.ui.theme.deckItemTitleStyle
import com.designlife.justdo.ui.theme.deckItemTitleStyleSize
import com.designlife.justdo.ui.theme.deckListHeight
import com.designlife.justdo.ui.theme.rightRoundedCorners

@Composable
fun DeckItem(
    deck: Deck,
    deckTheme : Color,
    onDeckClickEvent : () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = deckListHeight.value)
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onDeckClickEvent() },
        shape = RoundedCornerShape(20)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = deckTheme.copy(alpha = 0.3F), shape = RoundedCornerShape(20)),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier
                .height(60.dp)
                .width(6.dp)
                .background(color = deckTheme, shape = Shapes.rightRoundedCorners(20.dp)))
            Spacer(modifier = Modifier.width(2.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (deck.deckName.isNotEmpty() && deck.deckName.length > 35)  deck.deckName.substring(0,34) else deck.deckName,
                    style = deckItemTitleStyle.value,
                    fontSize = deckItemTitleStyleSize.value
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${deck.cards.size} Cards",
                        style = deckItemContentStyle.value,
                        fontSize = deckItemContentStyleSize.value
                    )
                    Text(
                        text = getDateFromDeck(deck),
                        style = deckItemContentStyle.value,
                        fontSize = deckItemContentStyleSize.value
                    )
                }
            }
        }
    }
}

fun getDateFromDeck(deck: Deck): String {
    return "${IDateGenerator.getDayInfoFrom(deck.modifiedDate).first} ${IDateGenerator.getMonthFromDate(deck.modifiedDate)}"
}
