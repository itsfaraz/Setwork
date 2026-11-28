package com.designlife.setwork.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.designlife.setwork.common.domain.calendar.IDateGenerator.Companion.getGracefullyTimeFromEpoch
import com.designlife.setwork.common.domain.entities.Note
import com.designlife.setwork.common.presentation.components.rippleClickable
import com.designlife.setwork.ui.theme.TypographyColor
import com.designlife.setwork.ui.theme.UIComponentBackground
import com.designlife.setwork.ui.theme.noteItemContentStyle
import com.designlife.setwork.ui.theme.noteItemContentStyleSize
import com.designlife.setwork.ui.theme.noteItemTitleStyle
import com.designlife.setwork.ui.theme.noteItemTitleStyleSize
import com.designlife.setwork.ui.theme.noteListHeight

@Composable
fun NoteItem(
    noteTheme: Color,
    note: Note,
    onClick: () -> Unit,
) {
    val hasCover = note.coverImage != null
    val context = LocalContext.current

    val shape = RoundedCornerShape(12.dp)

    val baseModifier = Modifier
        .fillMaxWidth()
        .then(
            if (!hasCover) {
                Modifier
                    .padding(horizontal = 10.dp, vertical = noteListHeight.value)
                    .height(198.dp)
                    .shadow(
                        elevation = 7.dp,
                        shape = shape,
                        ambientColor = DefaultShadowColor.copy(
                            alpha = 0.3f,
                            red = 0.5f,
                            green = 0.5f,
                            blue = 0.7f
                        )
                    )
            } else Modifier
        )

    Card(
        modifier = baseModifier
            .padding(
                horizontal = if (hasCover) 12.dp else 5.dp,
                vertical = if (hasCover) 8.dp else 4.dp
            )
            .clip(shape)
            .rippleClickable { onClick() },
        backgroundColor = UIComponentBackground.value,
        shape = shape,
        elevation = 6.dp
    ) {
        Column {
            if (hasCover) {
                Box {
                    val imageData = note.coverImage
                    val cacheKey = imageData?.toString()
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageData)
                            .memoryCacheKey(cacheKey)
                            .diskCacheKey(cacheKey)
                            .crossfade(true)
                            .size(360, 180)
                            .build(),
                        contentDescription = "Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    EmojiBadge(
                        emoji = note.emoji,
                        color = noteTheme,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 15.dp)
                    )
                }
            } else {
                EmojiBadge(
                    emoji = note.emoji,
                    color = noteTheme,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(horizontal = if (hasCover) 10.dp else 0.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val title = remember(note.title) {
                    if (note.title.length > 28)
                        note.title.take(25) + "..."
                    else note.title
                }

                val content = remember(note.content) {
                    if (note.content.length > 90)
                        note.content.take(80) + "..."
                    else note.content
                }

                Text(
                    text = title,
                    style = noteItemTitleStyle.value.copy(
                        textAlign = TextAlign.Center,
                        color = TypographyColor.value,
                        fontSize = noteItemTitleStyleSize.value
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = content,
                    style = noteItemContentStyle.value.copy(
                        textAlign = TextAlign.Center,
                        fontSize = noteItemContentStyleSize.value
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(22.dp))

                val formattedTime = remember(note.lastModified) {
                    getGracefullyTimeFromEpoch(note.lastModified.time)
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = formattedTime,
                            style = noteItemContentStyle.value.copy(
                                fontSize = noteItemContentStyleSize.value,
                                textAlign = TextAlign.Start
                            )
                        )
                        Spacer(modifier = Modifier.width(1.dp))
                        Icon(modifier = Modifier.size(12.dp), imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                    }

                }
            }
        }
    }
}

@Composable
private fun EmojiBadge(
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = CircleShape,
            backgroundColor = color,
            elevation = 10.dp
        ) {
            Box(
                modifier = Modifier.size(30.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 15.sp
                )
            }
        }
    }
}