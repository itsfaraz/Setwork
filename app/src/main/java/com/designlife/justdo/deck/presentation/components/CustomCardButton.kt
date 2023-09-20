package com.designlife.justdo.deck.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.designlife.justdo.ui.theme.ButtonPrimary

@Composable
fun CustomCardButton(
    onClick : () -> Unit
) {
    Card(
        modifier = Modifier
            .size(50.dp)
            .clickable {
                onClick()
            },
        backgroundColor = ButtonPrimary,
        elevation = 2.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(modifier = Modifier.size(20.dp), imageVector = Icons.Default.Add, contentDescription = "Add Button", tint = Color.White)
        }
    }
}