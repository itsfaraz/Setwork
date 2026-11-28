package com.designlife.setwork.common.presentation.components

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
import com.designlife.setwork.ui.theme.ButtonPrimary
import com.designlife.setwork.ui.theme.DangerButton
import com.designlife.setwork.ui.theme.Purple500
import com.designlife.setwork.ui.theme.buttonStyle
import com.designlife.setwork.ui.theme.buttonStyleSize

@Composable
fun CustomButton(
    buttonText: String,
    isDangerButton: Boolean,
    onButtonEvent: () -> Unit
) {
    Button(
        onClick = { onButtonEvent() },
        modifier = Modifier
            .width(if (isDangerButton) 80.dp else 68.dp)
            .height(if (isDangerButton) 30.dp else 30.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isDangerButton) DangerButton.value else ButtonPrimary.value,
            disabledBackgroundColor = Purple500
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Text(
            text = buttonText,
            textAlign = TextAlign.Start,
            style = buttonStyle.value,
            fontSize = buttonStyleSize.value
        )
    }
}