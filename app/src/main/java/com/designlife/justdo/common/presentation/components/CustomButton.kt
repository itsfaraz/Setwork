package com.designlife.justdo.common.presentation.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.DangerButton
import com.designlife.justdo.ui.theme.Purple500
import com.designlife.justdo.ui.theme.buttonStyle

@Composable
fun CustomButton(
    buttonText : String,
    isDangerButton : Boolean,
    onButtonEvent : () -> Unit
) {
    Button(
        onClick = { onButtonEvent() },
        modifier = Modifier
            .width(if (isDangerButton) 70.dp else 62.dp)
            .height(32.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if(isDangerButton) DangerButton else ButtonPrimary,
            disabledBackgroundColor = Purple500
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Text(text = buttonText, textAlign = TextAlign.Center, style = buttonStyle)
    }
}