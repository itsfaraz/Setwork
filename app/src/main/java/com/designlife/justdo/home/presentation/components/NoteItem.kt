package com.designlife.justdo.home.presentation.components

import android.graphics.BitmapFactory
import android.graphics.Paint.Align
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.designlife.justdo.common.domain.calendar.IDateGenerator.Companion.getFormattedDate
import com.designlife.justdo.common.domain.calendar.IDateGenerator.Companion.getGracefullyTimeFromEpoch
import com.designlife.justdo.common.domain.entities.Note
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.TypographyColor
import com.designlife.justdo.ui.theme.UIComponentBackground
import com.designlife.justdo.ui.theme.noteItemContentStyle
import com.designlife.justdo.ui.theme.noteItemContentStyleSize
import com.designlife.justdo.ui.theme.noteItemTitleStyle
import com.designlife.justdo.ui.theme.noteItemTitleStyleSize
import com.designlife.justdo.ui.theme.noteListHeight
import java.util.Date

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    noteTheme : Color,
    note : Note,
    onClick : () -> Unit,
    onLongClick : () -> Unit
) {
    val hasCover = note.coverImage != null
    val modifier = if (hasCover) Modifier
        .fillMaxWidth(1F)
        .wrapContentHeight() else Modifier
        .padding(horizontal = 10.dp, vertical = noteListHeight.value)
        .fillMaxWidth(1F)
        .height(180.dp)
        .shadow(elevation = 7.dp, shape = RoundedCornerShape(12.dp), ambientColor = DefaultShadowColor.copy(
            alpha = 0.3F,
            red = .5F,
            green = .5F,
            blue = .7F,
        ))
    Card(
        backgroundColor = UIComponentBackground.value,
        modifier = modifier
            .padding(horizontal = if (!hasCover) 5.dp else 12.dp, vertical = if (!hasCover) 0.dp else 4.dp)
//            .alpha(if (deleteLock) 0.4F else 0F)
            .combinedClickable(
                onClick = {
                    onClick()
                },
                onLongClick = {
                    onLongClick()
                }
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
        ) {
            if (hasCover){
                val painter = rememberAsyncImagePainter(
                    note.coverImage
                )
                Box(modifier = Modifier.wrapContentSize(), contentAlignment = Alignment.TopStart) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        painter = painter,
                        contentScale = ContentScale.Crop,
                        contentDescription = "Cover Image"
                    )
                    Column(
                        modifier = Modifier
                            .padding(top = (85).dp)
                            .fillMaxWidth()
                            .height(30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(modifier = Modifier
                            .height(35.dp)
                            .clip(RoundedCornerShape(100)),
                            backgroundColor = noteTheme,
                            elevation = 15.dp
                        ) {
                            Box(
                                modifier = Modifier.size(30.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = note.emoji,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }else{
                Column(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth()
                        .height(35.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(100)),
                        elevation = 15.dp,
                        backgroundColor = noteTheme
                    ) {
                        Box(
                            modifier = Modifier.size(30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = note.emoji,
                                fontSize = 15.sp
                            )
                        }

                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier.padding(horizontal = if (hasCover) 10.dp else 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = if (note.title.length > 28) "${note.title.substring(0,25)} ..." else note.title,
                    style = noteItemTitleStyle.value.copy(
                        textAlign = TextAlign.Center,
                        color = TypographyColor.value,
                        fontSize = noteItemTitleStyleSize.value
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    modifier = Modifier.fillMaxWidth().height(35.dp),
                    text = if (note.content.length > 90) "${note.content.substring(0,80)} ..." else note.content,
                    style = noteItemContentStyle.value.copy(
                        textAlign = TextAlign.Center,
                        fontSize = noteItemContentStyleSize.value
                    ),
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = getGracefullyTimeFromEpoch(note.lastModified.time),
                    style = noteItemContentStyle.value.copy(
                        fontSize = noteItemContentStyleSize.value,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(if (hasCover) 6.dp else 2.dp))
            }
        }
    }
}