package com.designlife.setwork.settings.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.designlife.setwork.settings.presentation.entity.LoaderStatus
import com.designlife.setwork.settings.presentation.enums.LoaderState
import com.designlife.setwork.ui.theme.ButtonPrimary
import com.designlife.setwork.ui.theme.PickerItemStyle
import com.designlife.setwork.ui.theme.UIComponentBackground
import com.designlife.setwork.ui.theme.pickerItemStyleSize

@Composable
fun CustomLoaderComponent(
    loaderData: LoaderStatus
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.7F)
                .height(120.dp)
                .background(UIComponentBackground.value, shape = RoundedCornerShape(12.dp)),
            elevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(UIComponentBackground.value),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(top = 2.dp))
                Text(
                    text = loaderData.title,
                    style = PickerItemStyle.value.copy(fontWeight = FontWeight.SemiBold),
                    fontSize = pickerItemStyleSize.value
                )
                Spacer(modifier = Modifier.padding(top = 8.dp))
                when (loaderData.loaderState) {
                    LoaderState.PENDING -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(30.dp),
                            color = ButtonPrimary.value,
                            strokeWidth = 4.dp
                        )
                    }

                    LoaderState.FAILED, LoaderState.SUCCESS -> {
                        Text(
                            text = loaderData.message,
                            style = PickerItemStyle.value,
                            fontSize = pickerItemStyleSize.value
                        )
                    }

                    else -> {}
                }
                Spacer(modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}