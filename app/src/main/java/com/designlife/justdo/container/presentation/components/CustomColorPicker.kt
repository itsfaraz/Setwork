package com.designlife.justdo.container.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.R
import com.designlife.justdo.ui.theme.*

@Composable
fun CustomColorPicker(
    isVisible : Boolean,
    colorPaletteDialog : (visibility : Boolean) -> Unit,
    selectedColor : Color,
    onColorChange : (color : Color) -> Unit,
    onEmojiChange : (emoji : String) -> Unit,
) {
    val colorList = listOf<Color>(
        ColorPaletteItem1, ColorPaletteItem2, ColorPaletteItem3, ColorPaletteItem4,
        ColorPaletteItem5, ColorPaletteItem6, ColorPaletteItem7, ColorPaletteItem8,
        ColorPaletteItem9, ColorPaletteItem10, ColorPaletteItem11, ColorPaletteItem12,
        ColorPaletteItem13, ColorPaletteItem14, ColorPaletteItem15, ColorPaletteItem16,
    )

    val emojiList = listOf<String>(
        "🏋️","🏃‍","🚗","📔",
        "🎓","💎","⚠️","♾️",
        "❓","⚕️","👨‍💻","💰",
        "📈","🎯","🧠","💡"
    )

    Card(
        modifier = Modifier
            .padding(horizontal = 80.dp)
            .height(180.dp)
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        elevation = 12.dp
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .background(shape = RoundedCornerShape(20.dp), color = Color.White),
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center
        ){
            items(colorList.size){index ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 6.dp, vertical = 15.dp)
                        .height(46.dp)
                        .clickable {
                            onColorChange(colorList[index])
                            onEmojiChange(emojiList[index])
                            colorPaletteDialog(false)
                        }
                        .background(color = colorList.get(index), shape = RoundedCornerShape(100)),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (colorList[index] == selectedColor){
                        Image(painter = painterResource(id = R.drawable.ic_selected_tick), contentDescription = "Selected Color", modifier = Modifier.size(20.dp))
                    }else{
                        Text(
                            fontSize = 15.sp,
                            text = emojiList[index]
                        )
                    }
                }
            }
        }
    }
}