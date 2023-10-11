package com.designlife.justdo.home.presentation.components

import android.graphics.Paint.Align
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.designlife.justdo.common.domain.calendar.IDateGenerator.Companion.getFormattedDate
import com.designlife.justdo.common.domain.calendar.IDateGenerator.Companion.getGracefullyTimeFromEpoch
import com.designlife.justdo.common.domain.entities.Note
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.noteItemContentStyle
import com.designlife.justdo.ui.theme.noteItemTitleStyle
import java.util.Date

@Composable
fun NoteItem(
    noteTheme : Color,
    note : Note,
    onClick : () -> Unit
) {
    val hasCover = note.coverImage.isNotEmpty() && note.coverImage.isNotBlank()
    val modifier = if (hasCover) Modifier
        .fillMaxWidth(1F)
        .wrapContentHeight() else Modifier
        .padding(horizontal = 10.dp, vertical = 10.dp)
        .fillMaxWidth(1F)
        .height(180.dp)
    Card(
        backgroundColor = Color.White,
        modifier = modifier
            .padding(horizontal = if (!hasCover) 5.dp else 12.dp, vertical = if (!hasCover) 0.dp else 4.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
        ) {
            if (hasCover){
                Box(modifier = Modifier.wrapContentSize(), contentAlignment = Alignment.TopStart) {
                    val painter = rememberAsyncImagePainter(note.coverImage)
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        painter = painter,
                        contentScale = ContentScale.FillBounds,
                        contentDescription = "Cover Image"
                    )
                    Column(
                        modifier = Modifier
                            .padding(top = 85.dp)
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
                    style = noteItemTitleStyle.copy(
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    modifier = Modifier.fillMaxWidth().height(35.dp),
                    text = if (note.content.length > 90) "${note.content.substring(0,80)} ..." else note.content,
                    style = noteItemContentStyle.copy(
                        textAlign = TextAlign.Center
                    ),
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = getGracefullyTimeFromEpoch(note.lastModified.time),
                    style = noteItemContentStyle.copy(
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(if (hasCover) 6.dp else 2.dp))
            }
        }
    }
}