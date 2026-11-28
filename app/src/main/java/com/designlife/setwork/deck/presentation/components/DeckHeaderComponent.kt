package com.designlife.setwork.deck.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.designlife.setwork.R
import com.designlife.setwork.common.domain.entities.Category
import com.designlife.setwork.common.presentation.components.CustomCategoryIcon
import com.designlife.setwork.common.presentation.components.CustomIconButton
import com.designlife.setwork.ui.theme.ButtonPrimary
import com.designlife.setwork.ui.theme.ComponentBackground
import com.designlife.setwork.ui.theme.IconColor
import com.designlife.setwork.ui.theme.Shapes
import com.designlife.setwork.ui.theme.TaskItemLabelColor
import com.designlife.setwork.ui.theme.TypographyColor
import com.designlife.setwork.ui.theme.cutBottomRoundedCorners
import com.designlife.setwork.ui.theme.headerStyle
import kotlinx.coroutines.delay

@Composable
fun DeckHeaderComponent(
    headerTitle: String,
    onTitleChange: (String) -> Unit,
    onCloseEvent: () -> Unit,
    isEdit: Boolean,
    progressState: Boolean,
    onAutoSaveEvent: () -> Unit,
    onDeleteButtonClickEvent: () -> Unit,
    categoryList: List<Category>,
    onCategoryIndexChange: (Int) -> Unit,
    addNewCategory: () -> Unit
) {
    val onClose by rememberUpdatedState(onCloseEvent)
    val onAutoSave by rememberUpdatedState(onAutoSaveEvent)
    val onDelete by rememberUpdatedState(onDeleteButtonClickEvent)
    LaunchedEffect(isEdit, headerTitle) {
        if (!isEdit) {
            delay(5000)
            onAutoSave()
        }
    }
    val displayTitle = remember(headerTitle) {
        if (headerTitle.length > 20) headerTitle.take(19) else headerTitle
    }
    val rotation by rememberRotationAnimation(enabled = !isEdit)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 66.dp, max = 74.dp)
            .clip(Shapes.cutBottomRoundedCorners(16.dp))
            .background(ComponentBackground.value),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onClose() }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Icon",
                    tint = IconColor.value,
                    modifier = Modifier.size(22.dp)
                )
            }
            TitleInput(
                title = displayTitle,
                originalTitle = headerTitle,
                onTitleChange = onTitleChange
            )
            if (!isEdit) {
                HeaderActions(
                    rotation = rotation,
                    categoryList = categoryList,
                    onCategoryIndexChange = onCategoryIndexChange,
                    onDelete = onDelete,
                    addNewCategory = addNewCategory
                )
            }
        }
        if (progressState) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                color = Color.Green
            )
        }
    }
}

@Composable
private fun rememberRotationAnimation(enabled: Boolean): State<Float> {
    return if (enabled) {
        val transition = rememberInfiniteTransition(label = "rotation")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(5000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )
    } else {
        remember { mutableStateOf(0f) }
    }
}

@Composable
private fun TitleInput(
    title: String,
    originalTitle: String,
    onTitleChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.4f)
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            modifier = Modifier
                .padding(end = 8.dp)
                .fillMaxWidth(),
            value = title,
            onValueChange = onTitleChange,
            singleLine = true,
            textStyle = headerStyle.value.copy(color = TypographyColor.value),
            cursorBrush = SolidColor(ButtonPrimary.value)
        ) { innerField ->
            if (originalTitle.isEmpty()) {
                Text("Deck Name ...", color = TaskItemLabelColor.value)
            }
            innerField()
        }
    }
}

@Composable
private fun HeaderActions(
    rotation: Float,
    categoryList: List<Category>,
    onCategoryIndexChange: (Int) -> Unit,
    onDelete: () -> Unit,
    addNewCategory: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(2.dp))
        Box(
            modifier = Modifier
                .wrapContentSize()
                .rotate(rotation)
        ) {
            CustomIconButton(
                onIconEvent = {},
                iconRes = R.drawable.ic_auto_save
            )
        }
        Spacer(modifier = Modifier.width(2.dp))
        CustomCategoryIcon(
            categoryList = categoryList,
            onCategoryEvent = onCategoryIndexChange
        ) {
            addNewCategory()
        }
        Spacer(modifier = Modifier.width(2.dp))
        IconButton(onClick = onDelete) {
            Icon(imageVector = Icons.Default.Delete, tint = Color.Red, contentDescription = "Delete Icon")
        }
    }
}



//@Composable
//fun DeckHeaderComponent(
//    headerTitle: String,
//    onTitleChange: (newTitle: String) -> Unit,
//    onCloseEvent: () -> Unit,
//    isEdit : Boolean,
//    isNew : Boolean,
//    progressState : Boolean,
//    onAutoSaveEvent : () -> Unit,
//    onDeleteButtonClickEvent : () -> Unit,
//    categoryList : List<Category>,
//    selectedCategoryIndex : Int,
//    onCategoryIndexChange : (index : Int) -> Unit,
//    onReminderEvent : () -> Unit,
//    addNewCategory : () -> Unit
//) {
//
//    LaunchedEffect(Unit) {
//        delay(5000)
//        onAutoSaveEvent()
//    }
//
//    val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
//
//    val rotation by infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = 360f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 5000, easing = LinearEasing),
//            repeatMode = RepeatMode.Restart
//        ),
//        label = "rotation"
//    )
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .heightIn(min = 66.dp, max = 74.dp)
//            .clip(Shapes.cutBottomRoundedCorners(16.dp))
//            .background(ComponentBackground.value),
//        horizontalAlignment = Alignment.Start,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Spacer(modifier = Modifier.height(20.dp))
//        Row(
//            modifier = Modifier
//            .fillMaxWidth(),
//            horizontalArrangement = Arrangement.Start,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            IconButton(onClick = {onCloseEvent() }) {
//                Column(
//                    modifier = Modifier
//                        .size(22.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon", tint = IconColor.value)
//                }
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(.40F)
//                    .fillMaxHeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Start
//            ) {
//                Spacer(modifier = Modifier.width(8.dp))
//                BasicTextField(
//                    modifier = Modifier
//                        .padding(end = 8.dp)
//                        .fillMaxWidth()
//                        .background(color = Color.Transparent),
//                    value = if (headerTitle.length > 20) headerTitle.substring(0,19) else headerTitle,
//                    onValueChange = {
//                        onTitleChange(it)
//                    },
//                    singleLine = true,
//                    textStyle = headerStyle.value.copy(color = TypographyColor.value),
//                    cursorBrush = SolidColor(ButtonPrimary.value)
//                ) { innerField ->
//                    if (headerTitle.isEmpty()) {
//                        Text(text = "Deck Name ...", color = TaskItemLabelColor.value)
//                    }
//                    innerField()
//                }
//            }
//
//            if (!isEdit){
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .fillMaxHeight(),
//                    horizontalArrangement = Arrangement.End,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
////                    CustomIconButton(
////                        onIconEvent = { onReminderEvent() },
////                        iconRes = R.drawable.ic_reminder
////                    )
//                    Spacer(modifier = Modifier.width(1.dp))
//                    Column (
//                        modifier = Modifier
//                            .wrapContentSize()
//                            .rotate(rotation)
//                    ) {
//                        CustomIconButton(
//                            onIconEvent = { /* auto launching from launched effects */ },
//                            iconRes = R.drawable.ic_auto_save
//                        )
//                    }
//                    Spacer(modifier = Modifier.width(1.dp))
//                    CustomCategoryIcon(
//                        categoryList = categoryList,
//                        onCategoryEvent = onCategoryIndexChange
//                    ) {
//                        addNewCategory()
//                    }
//
//                    Spacer(modifier = Modifier.width(1.dp))
//                    IconButton(
//                        onClick = {
//                            onDeleteButtonClickEvent()
//                        }
//                    ) {
//                        Icon(imageVector = Icons.Default.Delete, tint = Color.Red, contentDescription = "Delete Icon")
//                    }
//
//                }
//            }
//        }
//        AnimatedVisibility(progressState) {
//            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(5.dp), color = Color.Green)
//        }
//    }
//}