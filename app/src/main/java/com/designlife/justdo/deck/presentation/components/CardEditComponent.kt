package com.designlife.justdo.deck.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.common.domain.entities.FlashCard
import com.designlife.justdo.ui.theme.PrimaryColorHome2
import com.designlife.justdo.ui.theme.TaskItemLabelColor
import com.designlife.justdo.ui.theme.UIComponentBackground
import com.designlife.justdo.ui.theme.cardTextStyle
import com.designlife.justdo.ui.theme.headerStyle
import com.designlife.justdo.ui.theme.highlightTextStyle
import com.designlife.justdo.ui.theme.highlightTextStyleSize

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
        mutableStateOf(1F) // // Adjust this fraction as needed
    }
    val itemWidthFraction = 0.33f
    val frontTextScrollState = rememberScrollState()
    val backTextScrollState = rememberScrollState()
//    val rotationState = animateFloatAsState(
//        targetValue = if (cardExpandState.value) 180F else 0F
//    )


    var rotated by remember { mutableStateOf(false) }

    val rotationState by animateFloatAsState(
        targetValue = if (rotated) 180f else 0f,
        animationSpec = tween(500)
    )

    val animateFront by animateFloatAsState(
        targetValue = if (!rotated) 1f else 0f,
        animationSpec = tween(600)
    )

    val animateBack by animateFloatAsState(
        targetValue = if (rotated) 1f else 0f,
        animationSpec = tween(600)
    )

    val animateColor by animateColorAsState(
        targetValue = if (rotated) deckTheme else Color.White,
        animationSpec = tween(500)
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                rotationY = rotationState
                cameraDistance = 8 * density
            }
            .width((screenWidth * itemWidthFraction).dp)
            .fillMaxHeight(itemHeight.value)
            .clickable {
                if (editState) {
                    rotated = false
                } else {
                    rotated = !rotated
                }
            }
            .padding(horizontal = 3.dp),
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
            backgroundColor = animateColor,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                if (!rotated){
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
                }
                Column(
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(horizontal = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                    ,
                    verticalArrangement = if (editState) Arrangement.SpaceEvenly else Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    if (editState){
                        BasicTextField(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .weight(1F)
                                .background(color = Color.Transparent),
                            value = frontContent,
                            onValueChange = {
                                if (((frontContent.length)+(it.length)) <= 300){
                                    onFrontContentChange(it)
                                }
                            },
                            textStyle = headerStyle.value.copy(
                                fontWeight = FontWeight.Normal
                            )
                        ) { innerField ->
                            if (frontContent.isEmpty()){
                                Text(text = "Front Content Text ...", color = TaskItemLabelColor.value)
                            }
                            innerField()
                        }
                        Row(modifier = Modifier
                            .padding(vertical = 5.dp)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Text(
                                text = "${frontContent.length}/300",
                                style = highlightTextStyle.value,
                                fontSize = highlightTextStyleSize.value
                            )
                        }
                        BasicTextField(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .weight(1F)
                                .background(color = Color.Transparent),
                            value = backContent,
                            onValueChange = {
                                if (backContent.length <= 1000){
                                    onBackContentChange(it)
                                }
                            },
                            textStyle = headerStyle.value.copy(
                                fontWeight = FontWeight.Normal
                            )
                        ) { innerField ->
                            if (backContent.isEmpty()){
                                Text(text = "Back Content Text ...", color = TaskItemLabelColor.value)
                            }
                            innerField()
                        }
                    }
                    if (!editState){
                        Text(
                            modifier = Modifier
                                .verticalScroll(frontTextScrollState)
                                .graphicsLayer {
                                    alpha = if (rotated) animateBack else animateFront
                                    rotationY = rotationState
                                },
                            text = if (!rotated){if(frontContent.isEmpty()) "Short Text ..." else frontContent} else { if(backContent.isEmpty()) "Full Content Text ..." else backContent},
                            style = cardTextStyle.value.copy(
                                textAlign = TextAlign.Justify,
                                fontWeight = FontWeight.SemiBold,
                                color = if (!rotated) Color.Black else Color.White,
                                fontSize = if (!rotated) 22.sp else 18.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}


@Preview
@Composable
fun CardEditPrev() {
    val edit = remember {
        mutableStateOf(true)
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