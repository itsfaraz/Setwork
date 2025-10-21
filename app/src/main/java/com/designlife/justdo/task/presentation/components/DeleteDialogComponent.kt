package com.designlife.justdo.task.presentation.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.UIComponentBackground
import com.designlife.justdo.ui.theme.noteTitleStyle
import com.designlife.justdo.ui.theme.pickerItemStyleSize

@Composable
fun DeleteDialogComponent(
    eventSuccuss : () -> Unit,
    eventFail : () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.7F)
                .height(220.dp)
                .background(UIComponentBackground.value, shape = RoundedCornerShape(12.dp)),
            elevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Text(text = "Do you want to delete all the occurrences of these task, if any ?",style = noteTitleStyle.value.copy(fontWeight = FontWeight.Medium), fontSize = pickerItemStyleSize.value)
                Spacer(modifier = Modifier.padding(top = 12.dp))
                Column (
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = ButtonPrimary.value),
                        onClick = {eventSuccuss()}
                    ) {
                        Text(modifier = Modifier.padding(0.dp), text = "Yes, Delete all",style = noteTitleStyle.value.copy(color = Color.White, fontWeight = FontWeight.SemiBold), fontSize = pickerItemStyleSize.value)
                    }
                    Spacer(modifier = Modifier.padding(vertical = 10.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = ButtonPrimary.value),
                        onClick = {eventFail()}
                    ) {
                        Text(modifier = Modifier.padding(0.dp), text = "Deleted Only Selected",style = noteTitleStyle.value.copy(color = Color.White, fontWeight = FontWeight.SemiBold), fontSize = pickerItemStyleSize.value)
                    }
                }
            }
        }
    }
}