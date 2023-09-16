package com.designlife.justdo.note.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.designlife.justdo.ui.theme.ButtonHighLightPrimary
import com.designlife.justdo.ui.theme.noteContentStyle
import com.designlife.justdo.ui.theme.noteTitleStyle

@Composable
fun NoteComponent(
    title : String,
    onTitleUpdate : (newTitle : String) -> Unit,
    noteText : String,
    onNoteUpdate : (noteData : String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()){
        Divider(modifier = Modifier, color = ButtonHighLightPrimary, thickness = .7.dp)
        BasicTextField(
            modifier = Modifier
                .padding(start = 5.dp, top = 2.dp, bottom = 2.dp)
                .fillMaxWidth()
                .background(color = Color.Transparent),
            value = title,
            singleLine = true,
            textStyle = noteTitleStyle,
            onValueChange = {onTitleUpdate(it)},
        ){ innerTextField ->
            if (title.isEmpty()){
                Text(text = "Untitled", style = noteTitleStyle.copy(color = Color.Gray))
            }
            innerTextField()
        }
        Divider(modifier = Modifier, color = ButtonHighLightPrimary, thickness = .7.dp)
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            for(i in 1..40){
                Divider(modifier = Modifier.padding(top = (i*26).dp), color = ButtonHighLightPrimary, thickness = .5.dp)
            }
            Row(
                modifier = Modifier
                .padding(horizontal = 5.dp)
            ) {
                BasicTextField(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Transparent),
                    value = noteText,
                    singleLine = false,
                    textStyle = noteContentStyle,
                    onValueChange = {onNoteUpdate(it)},
                ){ innerTextField ->
                    innerTextField()
                }
            }
        }
    }
}