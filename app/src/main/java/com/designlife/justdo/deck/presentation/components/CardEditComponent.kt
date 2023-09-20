package com.designlife.justdo.deck.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.common.domain.entities.FlashCard
import com.designlife.justdo.ui.theme.TaskItemLabelColor
import com.designlife.justdo.ui.theme.cardTextStyle
import com.designlife.justdo.ui.theme.headerStyle

@Composable
fun CardEditComponent(
    modifier: Modifier = Modifier,
    deckTheme: Color,
    frontContent : String,
    backContent : String,
    editState : Boolean,
    onEditStateChange: (value: Boolean) -> Unit,
    onFrontContentChange : (value : String) -> Unit,
    onBackContentChange : (value : String) -> Unit,
) {
    val screenWidth = LocalDensity.current.run {
        (LocalConfiguration.current.screenWidthDp * density).toInt()
    }
    val itemHeight = remember {
        mutableStateOf(0.8F) // // Adjust this fraction as needed
    }
    val itemWidthFraction = 0.4f
    val cardExpandState = remember {
        mutableStateOf(false)
    }
    val frontTextScrollState = rememberScrollState()
    val backTextScrollState = rememberScrollState()
    val rotationState = animateFloatAsState(
        targetValue = if (cardExpandState.value) 180F else 0F
    )
    Box(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .width((screenWidth * itemWidthFraction).dp)
            .fillMaxHeight(itemHeight.value)
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = modifier
                .padding(horizontal = 5.dp)
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                )
                .width((screenWidth * itemWidthFraction).dp)
                .fillMaxHeight(itemHeight.value),
            backgroundColor = Color.White,
            elevation = 15.dp,
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
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        modifier = Modifier.size(22.dp),
                        onClick = {
                            onEditStateChange(true)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Close Icon",
                            tint = Color.White
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(horizontal = 6.dp)
                ){
                    if (editState){
                        BasicTextField(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(color = Color.Transparent),
                            value = frontContent,
                            onValueChange = {
                                if (frontContent.length <= 300){
                                    onFrontContentChange(it)
                                }
                            },
                            singleLine = true,
                            textStyle = headerStyle.copy(
                                fontWeight = FontWeight.Normal
                            )
                        ) { innerField ->
                            innerField()
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        BasicTextField(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(color = Color.Transparent),
                            value = backContent,
                            onValueChange = {
                                if (backContent.length <= 1000){
                                    onBackContentChange(it)
                                }
                            },
                            singleLine = true,
                            textStyle = headerStyle.copy(
                                fontWeight = FontWeight.Normal
                            )
                        ) { innerField ->
                            innerField()
                        }
                    }
                    if (!editState){
                        Text(
                            modifier = Modifier.verticalScroll(frontTextScrollState),
                            text = frontContent,
                            style = cardTextStyle.copy(
                                textAlign = TextAlign.Justify,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                    if (!editState && cardExpandState.value){
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            modifier = Modifier.verticalScroll(backTextScrollState),
                            text = backContent,
                            style = cardTextStyle.copy(
                                textAlign = TextAlign.Justify,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
        if (!editState){
            IconButton(
                modifier = Modifier
                    .size(22.dp)
                    .rotate(rotationState.value),
                onClick = {
                    if (!cardExpandState.value){
                        cardExpandState.value = true
                        itemHeight.value = 0.9F
                    }else{
                        cardExpandState.value = false
                        itemHeight.value = 0.8F
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Arrow Up Down Icon",
                    tint = Color.Gray
                )
            }
        }
    }
}


@Preview
@Composable
fun CardEditPrev() {
    val edit = remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CardEditComponent(
            deckTheme = Color.Red,
            frontContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec.",
            backContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. N",
            editState = edit.value,
            onEditStateChange = {
                 edit.value = it
            },
            onFrontContentChange = {},
            onBackContentChange ={}
        )
    }
}