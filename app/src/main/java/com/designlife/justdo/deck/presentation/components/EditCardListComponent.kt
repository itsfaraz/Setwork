package com.designlife.justdo.deck.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.R
import com.designlife.justdo.common.domain.entities.FlashCard
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.cardTextStyle
import com.designlife.justdo.ui.theme.cardTextStyleSize
import com.designlife.justdo.ui.theme.fontFamily
import kotlinx.coroutines.CoroutineScope

@Composable
fun EditCardListComponent(
    scope: CoroutineScope,
    listState: LazyListState,
    deckTheme: Color,
    editState: Boolean,
    cards: MutableList<FlashCard>,
    onUpdateEvent: (index: Int, card: FlashCard) -> Unit,
    onEditStateChange: (editState: Boolean) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxHeight(.9F)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        state = listState,
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        item {
            if (cards.isEmpty()) {
                Column(
                    Modifier
                        .padding(horizontal = 30.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("Looks like deck is empty")
                            withStyle(
                                style = SpanStyle(
                                    color = ButtonPrimary.value,
                                    fontSize = 17.sp,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append("\n\n\nCreate new card")
                            }
                        },
                        style = cardTextStyle.value.copy(fontSize = cardTextStyleSize.value)
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    Image(
                        modifier = Modifier.size(200.dp, 250.dp),
                        painter = painterResource(id = R.drawable.ic_down_pointing_arrow),
                        contentDescription = "Arrow"
                    )
                }
            }
        }
        items(
            cards.size
        ) { index ->
            val card = cards[index]
            val frontCard = remember {
                mutableStateOf(cards[index].frontContent)
            }
            val backCard = remember {
                mutableStateOf(cards[index].backContent)
            }
            CardEditComponent(
                deckTheme = deckTheme,
                frontContent = frontCard.value,
                backContent = backCard.value,
                editState = editState,
                onEditStateChange = {
                    onEditStateChange(it)
                },
                onFrontContentChange = {
                    frontCard.value = it
                    onUpdateEvent(index, cards[index].copy(frontContent = frontCard.value))
                },
                onBackContentChange = {
                    backCard.value = it
                    onUpdateEvent(index, cards[index].copy(backContent = backCard.value))
                }
            )
        }
    }
}