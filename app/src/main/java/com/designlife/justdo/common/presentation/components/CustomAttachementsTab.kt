package com.designlife.justdo.common.presentation.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.designlife.justdo.R
import com.designlife.justdo.common.domain.entities.Category
import com.designlife.justdo.common.utils.Logs
import com.designlife.justdo.common.utils.camelCase
import com.designlife.justdo.ui.theme.AttachmentTabItemTextStyle
import com.designlife.justdo.ui.theme.IconColor
import com.designlife.justdo.ui.theme.TypographyColor
import com.designlife.justdo.ui.theme.attachmentTabItemTextStyleSize

@Composable
fun CustomAttachementsTab(
    hasCover : Boolean,
    onGalleryEvent : (imageBitmap : Bitmap) -> Unit,
    categoryList : List<Category>,
    selectedCategoryIndex : Int,
    onCategoryEvent : (index : Int) -> Unit,
    addCategoryEvent : () -> Unit
) {
    val context = LocalContext.current
    var dropDownState by remember {
        mutableStateOf(false)
    }

    var imageUri by remember {
        mutableStateOf<Bitmap?>(null)
    }

    val launcher = rememberLauncherForActivityResult(contract =
        ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                val bitmap : Bitmap? = getBitmapFromURI(context,uri)
                bitmap?.let{
                    imageUri = bitmap
                    onGalleryEvent(bitmap)
                }
            }
    }

    val categoryName = if (selectedCategoryIndex == -1) "Category" else {
        if(categoryList[selectedCategoryIndex].name.length >= 9) "${(categoryList[selectedCategoryIndex].name).substring(0,5)}..." else (categoryList[selectedCategoryIndex].name)
    }.camelCase()

    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.wrapContentSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (hasCover){
                AttachementTabItem(icon = R.drawable.ic_gallery, itemTitle = "Gallery") {
                    launcher.launch(arrayOf("image/*"))
                }
                Spacer(modifier = Modifier.width(25.dp))
            }
            AttachementTabItem(icon = R.drawable.ic_folder, itemTitle = categoryName,isCategory = true) {
                dropDownState = true
            }
        }
        if (dropDownState){
            DropdownMenu(
                modifier = Modifier.background(color = Color.White, shape = RoundedCornerShape(10.dp)),
                expanded = dropDownState,
                onDismissRequest = { dropDownState = false }
            ) {
                categoryList.forEachIndexed {index,category ->
                    DropdownMenuItem(
                        onClick = {
                            onCategoryEvent(index)
                            dropDownState = false
                        }
                    ){
                        Row(
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            Text("${category.emoji} ${getCategoryName(category.name)}")
                        }
                    }
                }
                DropdownMenuItem(
                    onClick = {
                        addCategoryEvent()
                    }
                ){
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🗃 New category")
                    }
                }
            }
        }
    }
}

fun getBitmapFromURI(context : Context,uri: Uri): Bitmap? {
    try {
        val bitmap : Bitmap? = MediaStore.Images.Media.getBitmap(context.contentResolver,uri)
        bitmap?.let {
            return it
        } ?: throw Exception("Getting Null Image Bitmap")
    }catch (e : Exception){
        e.printStackTrace()
        Log.e(Logs.TAG_ERROR, "CustomAttachementsTab: Image Bitmap Error :: ${e.message}")
    }
    return null
}

fun getCategoryName(name: String): String {
    return (if (name.length >= 14) "${name.substring(0,10)}..." else name).camelCase()
}

@Composable
fun AttachementTabItem(
    @DrawableRes icon : Int,
    itemTitle : String,
    isCategory : Boolean = false,
    isDeckCategory : Boolean = false,
    onEvent : () -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .clickable {
                onEvent()
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isDeckCategory){
            Icon(modifier = Modifier.size(width = 10.dp, height = 20.dp), painter = painterResource(id = icon), contentDescription = "Tab Icon", tint = IconColor.value)
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = itemTitle,
            style = AttachmentTabItemTextStyle.value.copy(color = TypographyColor.value),
            fontSize = attachmentTabItemTextStyleSize.value
        )
        if (isCategory){
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Drop Down", tint = IconColor.value)
        }
    }
}