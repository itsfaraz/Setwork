package com.designlife.setwork.home.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.designlife.setwork.common.utils.entity.BottomNavItem
import com.designlife.setwork.common.utils.enums.ViewType
import com.designlife.setwork.ui.theme.ButtonHighLightPrimary
import com.designlife.setwork.ui.theme.ButtonPrimary
import com.designlife.setwork.ui.theme.ComponentBackground

@Composable
fun BottomNavigationBar(
    items : List<BottomNavItem>,
    selectedScreen : ViewType,
    onItemSelected : (screenType : ViewType) -> Unit
) {
    BottomNavigation(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = ComponentBackground.value
    ) {
        items.forEachIndexed {index,item ->
            val isSelected =  index == getItemIndex(selectedScreen)
            BottomNavigationItem(
                icon = {
                       Icon(modifier = Modifier.size(20.dp),painter = painterResource(id = item.icon), contentDescription = "icon", tint = if(isSelected) ButtonPrimary.value else ButtonHighLightPrimary.value)
                },
                label = {
                        Text(
                            text = item.label,
                            color = if(isSelected) ButtonPrimary.value else ButtonHighLightPrimary.value
                        )
                },
                selected = isSelected,
                onClick = { onItemSelected(getScreenTypeFromIndex(index)) }
            )
        }
    }
}

private fun getScreenTypeFromIndex(index : Int) : ViewType{
    return when(index){
        0 -> ViewType.TASK
        1 -> ViewType.NOTE
        2 -> ViewType.DECK
        3 -> ViewType.SETTING
        else -> ViewType.TASK
    }
}

private fun getItemIndex(navBarItem : ViewType) : Int{
    return when(navBarItem){
        ViewType.TASK -> 0
        ViewType.NOTE -> 1
        ViewType.DECK -> 2
        ViewType.SETTING -> 3
        else -> 0
    }
}