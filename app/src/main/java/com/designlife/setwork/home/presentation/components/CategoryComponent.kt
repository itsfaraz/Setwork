package com.designlife.setwork.home.presentation.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.setwork.R
import com.designlife.setwork.common.domain.entities.Category
import com.designlife.setwork.common.presentation.components.FolderItem
import com.designlife.setwork.common.presentation.components.rippleClickable
import com.designlife.setwork.common.utils.enums.ViewType
import com.designlife.setwork.ui.theme.PrimaryBackgroundCategoryColor
import com.designlife.setwork.ui.theme.PrimaryColorHome2
import com.designlife.setwork.ui.theme.TypographyColor
import com.designlife.setwork.ui.theme.UIComponentBackground
import com.designlife.setwork.ui.theme.contentStyle_One
import com.designlife.setwork.ui.theme.contentStyle_OneSize
import com.designlife.setwork.ui.theme.fontFamily
import com.designlife.setwork.ui.theme.headerStyle

@Composable
fun CategoryComponent(
    viewType: ViewType,
    selectedCategoryIndex: Int,
    categoryList: List<Category>,
    onEventClick: (categoryIndex: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Text(text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = TypographyColor.value,
                    fontSize = 10.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Light
                )
            ) {
                append(if (viewType == ViewType.TASK) "CATEGORIES " else "FOLDERS ")
            }
        })
        Spacer(modifier = Modifier.height(20.dp))
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(
                count = categoryList.size,
                key = {
                    categoryList[it].hashCode()
                }
            ) { index ->
                val item = categoryList[index]
                if (viewType == ViewType.TASK) {
                    CategoryItem(
                        totalTasks = item.totalTodo,
                        categoryName = item.name,
                        totalCompleted = item.totalCompleted,
                        categoryTheme = item.color,
                        isSelected = index == selectedCategoryIndex
                    ) {
                        onEventClick(index)
                    }
                } else {
                    FolderItem(
                        folderName = item.name,
                        colorTheme = item.color,
                        emoji = item.emoji,
                        isSelected = index == selectedCategoryIndex
                    ) {
                        onEventClick(index)
                    }
                }
            }
//            item {
//                DummyCategoryItem{
//                    newCategoryEvent()
//                }
//            }
        }
    }
}

@Composable
fun CategoryItem(
    totalTasks: Int,
    categoryName: String,
    totalCompleted: Int,
    categoryTheme: Color,
    isSelected: Boolean,
    onCategoryEvent: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .width(200.dp)
            .height(100.dp)
            .alpha(if (isSelected) .8F else 1F)
            .clip(RoundedCornerShape(20))
            .rippleClickable { onCategoryEvent() }
            .background(
                color = if (isSelected) categoryTheme else UIComponentBackground.value,
                shape = RoundedCornerShape(20)
            )
            .padding(horizontal = 8.dp),
    ) {

        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            Image(
                modifier = Modifier.padding(bottom = 6.dp).fillMaxSize().alpha(0.2F),
                painter = painterResource(R.drawable.bc_one_op),
                contentDescription = "Dot Background",
                contentScale = ContentScale.FillBounds,
                colorFilter = ColorFilter.tint(color = categoryTheme)
            )
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${totalTasks} Task's",
                    style = contentStyle_One.value.copy(
                        color = if (isSelected) Color.White else TypographyColor.value,
                        fontSize = contentStyle_OneSize.value
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = (if (categoryName.length >= 10) "${
                        categoryName.substring(
                            0,
                            8
                        )
                    }..." else categoryName).uppercase(),
                    style = headerStyle.value.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isSelected) Color.White else TypographyColor.value
                    )
                )
                Spacer(modifier = Modifier.height(30.dp))
                AnimatedCategoryBar(
                    totalTasks = totalTasks.toFloat(),
                    totalCompleted = totalCompleted.toFloat(),
                    color = if (isSelected) Color.White else categoryTheme
                )
            }
        }

    }
}

@SuppressLint("UnnecessaryComposable")
@Composable
fun DummyCategoryItem(
    newCategoryEvent: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .width(200.dp)
            .height(100.dp)
            .background(color = PrimaryColorHome2.value, shape = RoundedCornerShape(20))
            .rippleClickable { newCategoryEvent() }
            .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(20))
            .padding(horizontal = 8.dp),
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Add Category",
            style = headerStyle.value.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryBackgroundCategoryColor.value
            )
        )
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(220.dp),
                imageVector = Icons.Default.Add,
                contentDescription = "Add Icon",
                tint = PrimaryBackgroundCategoryColor.value,
            )
        }
    }
}

@Composable
fun AnimatedCategoryBar(
    totalTasks: Float,
    totalCompleted: Float,
    color: Color
) {
    var progress by remember {
        mutableStateOf(0f)
    }
    val progressBarAnimation by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing,
            delayMillis = 100
        )
    )
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(100)),
        color = color,
        backgroundColor = PrimaryBackgroundCategoryColor.value,
        progress = progressBarAnimation,
    )
    LaunchedEffect(Unit) {
        if (totalTasks != 0F && totalCompleted != 0F)
            progress = (((totalCompleted / totalTasks)))
    }
}