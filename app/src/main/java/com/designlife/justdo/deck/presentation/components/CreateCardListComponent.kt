package com.designlife.justdo.deck.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.R
import com.designlife.justdo.common.domain.entities.FlashCard
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.cardTextStyle
import com.designlife.justdo.ui.theme.fontFamily
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun CreateCardListComponent(
    scope: CoroutineScope,
    listState: LazyListState,
    deckTheme: Color,
    cards: List<FlashCard>,
    onDeleteEvent: (index: Int) -> Unit,
    onExpandEvent: (index: Int) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxHeight(.8F)
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
                                    color = ButtonPrimary,
                                    fontSize = 17.sp,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append("\n\n\nCreate new card")
                            }
                        },
                        style = cardTextStyle
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
            var isSwiped by remember { mutableStateOf(false) }
            val threshold = -100f // Adjust the threshold as needed
            var offsetY = animateFloatAsState(
                targetValue = if (isSwiped) -2500F else 0F,
                animationSpec = tween(durationMillis = 300)
            )
            CardCreateComponent(
                modifier = Modifier
                    .offset { IntOffset(0, offsetY.value.roundToInt()) }
                    .pointerInput(cards[index]) {
                        detectVerticalDragGestures { _, dragAmount ->
                            when{
                                !isSwiped && dragAmount < threshold -> {
                                    isSwiped = true
                                }
                            }
                        }
                    },
                deckTheme = deckTheme,
                card = cards[index],
                onDeleteEvent = { onDeleteEvent(index) },
                onExpandEvent = { onExpandEvent(index) }
            )
            if (isSwiped) {
                LaunchedEffect(cards[index]) {
                    delay(150) // Wait for the animation to complete
                    onDeleteEvent(index)
                }
            }
        }
    }
}