package com.designlife.justdo.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.R
import com.designlife.justdo.ui.theme.IconColor
import com.designlife.justdo.ui.theme.LightButtonPrimary
import com.designlife.justdo.ui.theme.LightPrimaryColorHome1
import com.designlife.justdo.ui.theme.Shapes
import com.designlife.justdo.ui.theme.TypographyColor
import com.designlife.justdo.ui.theme.UIComponentBackground
import com.designlife.justdo.ui.theme.cutBottomRoundedCorners
import com.designlife.justdo.ui.theme.noteContentStyle
import com.designlife.justdo.ui.theme.noteContentStyleSize

@Composable
fun SearchBarComponent(
    isDarkMode : Boolean,
    searchText : String,
    onSearchUpdates : (searchText : String) -> Unit,
    onClearSearch : () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(UIComponentBackground.value),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        BasicTextField(
            value = searchText,
            singleLine = true,
            textStyle = noteContentStyle.value.copy(
                fontSize = noteContentStyleSize.value,
                color = TypographyColor.value,
            ),
            cursorBrush = SolidColor(LightButtonPrimary),
            onValueChange = {onSearchUpdates(it)}
        ){ innerTextField ->
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (searchText.isEmpty()){
                    Icon(modifier = Modifier.size(15.dp),painter = painterResource(id = R.drawable.ic_search), contentDescription = "Search Icon", tint = LightButtonPrimary)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Row( modifier = Modifier.fillMaxWidth(.8F)) {
                    innerTextField()
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (searchText.isNotEmpty()){
                        IconButton(onClick = {
                            onClearSearch()
                        }) {
                            Icon(modifier = Modifier.size(20.dp),imageVector = Icons.Default.Close, contentDescription = "Cross Icon", tint = IconColor.value)
                        }
                    }
                }

            }
        }
    }
}