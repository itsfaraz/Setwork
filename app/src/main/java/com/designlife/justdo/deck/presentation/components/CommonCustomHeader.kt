package com.designlife.justdo.deck.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.designlife.justdo.common.domain.entities.Category
import com.designlife.justdo.common.presentation.components.CustomAttachementsTab
import com.designlife.justdo.common.presentation.components.CustomButton
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.ComponentBackground
import com.designlife.justdo.ui.theme.IconColor
import com.designlife.justdo.ui.theme.Shapes
import com.designlife.justdo.ui.theme.TaskItemLabelColor
import com.designlife.justdo.ui.theme.TypographyColor
import com.designlife.justdo.ui.theme.cutBottomRoundedCorners
import com.designlife.justdo.ui.theme.headerStyle

@Composable
fun DeckHeader(
    headerTitle: String,
    onTitleChange: (newTitle: String) -> Unit,
    onCloseEvent: () -> Unit,
    isEdit : Boolean,
    isNew : Boolean,
    onButtonClickEvent : () -> Unit,
    onDeleteButtonClickEvent : () -> Unit,
    categoryList : List<Category>,
    selectedCategoryIndex : Int,
    onCategoryIndexChange : (index : Int) -> Unit,
    addNewCategory : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(Shapes.cutBottomRoundedCorners(15.dp))
            .background(ComponentBackground.value),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .size(22.dp)
                .clickable { onCloseEvent() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon", tint = IconColor.value)
        }
        Row(
            modifier = Modifier.fillMaxWidth(.52F),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            BasicTextField(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .fillMaxWidth()
                    .background(color = Color.Transparent),
                value = headerTitle,
                onValueChange = {
                    onTitleChange(it)
                },
                singleLine = true,
                textStyle = headerStyle.value.copy(color = TypographyColor.value),
                cursorBrush = SolidColor(ButtonPrimary.value)
            ) { innerField ->
                if (headerTitle.isEmpty()) {
                    Text(text = "Deck Name ...", color = TaskItemLabelColor.value)
                }
                innerField()
            }
        }

        if (isEdit){
            Row(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                CustomButton(buttonText = "Save",isDangerButton = false) {
                    onButtonClickEvent()
                }
            }
        }

        if (!isEdit){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(horizontal = 8.dp)
                        .background(color = Color.LightGray, shape = RoundedCornerShape(100))
                        .padding(start = 4.dp)
                ) {
                    CustomAttachementsTab(
                        hasCover = false,
                        onGalleryEvent = { /*TODO*/ },
                        categoryList = categoryList,
                        selectedCategoryIndex = selectedCategoryIndex,
                        onCategoryEvent = onCategoryIndexChange
                    ) {
                        addNewCategory()
                    }
                }

                if (!isNew){
                    CustomButton(buttonText = "Delete", isDangerButton = true) {
                        onDeleteButtonClickEvent()
                    }
                }
            }
        }
    }
}