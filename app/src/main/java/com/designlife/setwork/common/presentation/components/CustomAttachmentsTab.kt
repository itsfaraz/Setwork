package com.designlife.setwork.common.presentation.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.designlife.setwork.R
import com.designlife.setwork.common.domain.entities.Category
import com.designlife.setwork.common.utils.Logs
import com.designlife.setwork.common.utils.camelCase
import com.designlife.setwork.ui.theme.AttachmentTabItemTextStyle
import com.designlife.setwork.ui.theme.ButtonPrimary
import com.designlife.setwork.ui.theme.IconColor
import com.designlife.setwork.ui.theme.TypographyColor
import com.designlife.setwork.ui.theme.attachmentTabItemTextStyleSize

@Composable
fun CustomAttachmentsTab(
    hasCover: Boolean,
    onGalleryEvent: (imageBitmap: Bitmap) -> Unit,
    categoryList: List<Category>,
    selectedCategoryIndex: Int,
    onCategoryEvent: (index: Int) -> Unit,
    addCategoryEvent: () -> Unit
) {
    val context = LocalContext.current
    var dropDownState by remember {
        mutableStateOf(false)
    }

    var imageUri by remember {
        mutableStateOf<Bitmap?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract =
            ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap: Bitmap? = getBitmapFromURI(context, uri)
            bitmap?.let {
                imageUri = bitmap
                onGalleryEvent(bitmap)
            }
        }
    }

    val categoryName = if (selectedCategoryIndex == -1) "Category" else {
        if (categoryList[selectedCategoryIndex].name.length >= 9) "${
            (categoryList[selectedCategoryIndex].name).substring(
                0,
                5
            )
        }..." else (categoryList[selectedCategoryIndex].name)
    }.camelCase()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AttachementTabItem(
                layoutWeight = .5F,
                icon = R.drawable.ic_folder,
                itemTitle = categoryName,
                isCategory = true
            ) {
                dropDownState = true
            }
            Spacer(modifier = Modifier.width(20.dp))
            if (hasCover) {
                AttachementTabItem(
                    layoutWeight = 1F,
                    icon = R.drawable.ic_gallery,
                    itemTitle = "Add Cover"
                ) {
                    launcher.launch(arrayOf("image/*"))
                }
            }
        }
        Row(
            modifier = Modifier
            .fillMaxWidth(.5F).padding(end = 40.dp),
            horizontalArrangement = Arrangement.End
        ) {
            AnimatedVisibility(dropDownState) {
                DropdownMenu(
                    modifier = Modifier.background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    ),
                    expanded = dropDownState,
                    onDismissRequest = { dropDownState = false }
                ) {
                    categoryList.forEachIndexed { index, category ->
                        DropdownMenuItem(
                            onClick = {
                                onCategoryEvent(index)
                                dropDownState = false
                            }
                        ) {
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
                    ) {
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
}

fun getBitmapFromURI(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e(Logs.TAG_ERROR, "Image Bitmap Error :: ${e.message}")
        null
    }
}

fun getCategoryName(name: String): String {
    return (if (name.length >= 14) "${name.substring(0, 10)}..." else name).camelCase()
}

@Composable
fun AttachementTabItem(
    layoutWeight: Float,
    @DrawableRes icon: Int,
    itemTitle: String,
    isCategory: Boolean = false,
    isDeckCategory: Boolean = false,
    onEvent: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(layoutWeight)
            .rippleClickable {
                onEvent()
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isDeckCategory) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(ButtonPrimary.value, shape = RoundedCornerShape(100)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = icon),
                    contentDescription = "Tab Icon",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = itemTitle,
            style = AttachmentTabItemTextStyle.value.copy(color = TypographyColor.value),
            fontSize = attachmentTabItemTextStyleSize.value
        )
        if (isCategory) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Drop Down",
                tint = IconColor.value
            )
        }
    }
}




@Composable
fun CustomCategoryIcon(
    categoryList: List<Category>,
    onCategoryEvent: (index: Int) -> Unit,
    addCategoryEvent: () -> Unit
) {
    var dropDownState by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = { dropDownState = true}) {
            Column(
                modifier = Modifier
                    .size(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(R.drawable.ic_folder),
                    contentDescription = "Folder Icon",
                    tint = ButtonPrimary.value
                )
            }
        }
        AnimatedVisibility(dropDownState) {
            DropdownMenu(
                modifier = Modifier.background(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp)
                ),
                expanded = dropDownState,
                onDismissRequest = { dropDownState = false }
            ) {
                categoryList.forEachIndexed { index, category ->
                    DropdownMenuItem(
                        onClick = {
                            onCategoryEvent(index)
                            dropDownState = false
                        }
                    ) {
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
                ) {
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

