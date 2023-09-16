package com.designlife.justdo.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.designlife.justdo.common.utils.enums.ViewType

@Composable
fun HeaderDropDownComponent(
    viewType : ViewType,
    onViewChange : (viewType : ViewType) -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    val typeList = listOf<String>("All Tasks","All Notes","All Decks")

    Box(
        modifier = Modifier.wrapContentWidth(),
        contentAlignment = Alignment.TopStart
    ) {

        Column{
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .clickable {
                        expanded = !expanded
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(text = getViewTypeText(viewType,typeList))
                Spacer(modifier = Modifier.width(10.dp))
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Arrow Down")
            }
            DropdownMenu(
                modifier = Modifier.background(color = Color.White, shape = RoundedCornerShape(10.dp)),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                typeList.forEachIndexed {index,viewTypeText ->
                    DropdownMenuItem(
                        onClick = {
                            when(index){
                                0 -> {onViewChange(ViewType.TASK)}
                                1 -> {onViewChange(ViewType.NOTE)}
                                2 -> {onViewChange(ViewType.DECK)}
                            }
                            expanded = false
                        }
                    ){
                        Text(viewTypeText)
                    }
                }
            }
        }



//        IconButton(onClick = { expanded = !expanded }) {
//            Icon(
//                imageVector = Icons.Default.MoreVert,
//                contentDescription = "More"
//            )
//        }



    }
}

fun getViewTypeText(viewType: ViewType,typeList : List<String>): String {
    return when(viewType){
        ViewType.TASK -> typeList[0]
        ViewType.NOTE -> typeList[1]
        ViewType.DECK -> typeList[2]
    }
}
