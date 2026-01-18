package com.designlife.justdo.container.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.enums.CategoryState
import com.designlife.justdo.common.presentation.components.CategoryHeader
import com.designlife.justdo.common.presentation.components.CommonCustomHeader
import com.designlife.justdo.common.utils.AppServiceLocator
import com.designlife.justdo.common.utils.constants.Constants
import com.designlife.justdo.common.utils.enums.ScreenType
import com.designlife.justdo.container.presentation.components.CategoryComponent
import com.designlife.justdo.container.presentation.components.CustomColorPicker
import com.designlife.justdo.container.presentation.components.RepeatComponent
import com.designlife.justdo.container.presentation.events.ContainerEvents
import com.designlife.justdo.container.presentation.viewmodel.ContainerViewModel
import com.designlife.justdo.container.presentation.viewmodel.ContainerViewModelFactory
import com.designlife.justdo.ui.theme.PrimaryBackgroundColor
import com.designlife.justdo.ui.theme.TaskItemLabelColor


class ContainerFragment : Fragment() {

    private lateinit var viewmodel : ContainerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("FRAGMENT_CHECK", "onCreate: ContainerFragment")
        val categoryRepository = AppServiceLocator.provideCategoryRepository(requireActivity().applicationContext)
        val repeatRepository = AppServiceLocator.provideRepeatRepository()
        val factory = ContainerViewModelFactory(categoryRepository,repeatRepository)
        viewmodel = ViewModelProvider(requireActivity(),factory)[ContainerViewModel::class.java]

        

        arguments?.let { bundle ->
            val ordinal = bundle.getInt(Constants.SCREEN_TYPE)
            val editMode = bundle.getBoolean(Constants.EDIT_MODE)
            ordinal?.let {
                val screenType = ScreenType.values()[it]
                viewmodel.initialSetup(screenType, editMode)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val screenType = viewmodel.screenType.value
                val categoryList = viewmodel.categoryList
                val selectedCategoryIndex = viewmodel.selectedCategory.value
                val colorPickerState = viewmodel.colorPickerToggle.value
                val selectedPaletteColor = viewmodel.selectedPaletteColor.value
                val selectedEmoji = viewmodel.selectedEmoji.value
                val newCategoryName = viewmodel.categoryName.value
                val editCategoryName = viewmodel.categoryTitle.value
                val isEditMode = viewmodel.editMode.value
                val categoryMode = viewmodel.categoryMode.value
                val repeatList = viewmodel.repeatList.value
                val selectedRepeatIndex = viewmodel.selectedRepeatIndex.value

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PrimaryBackgroundColor.value)
                            .alpha(if (colorPickerState) .8F else 1F)
                    ) {
                        CategoryHeader(
                            headerTitle = if (screenType == ScreenType.CATEGORY) "New Category" else "Repeat",
                            screenType = screenType,
                            onCloseEvent = {
                                viewmodel.onEvent(ContainerEvents.OnCategoryStateChange(CategoryState.INSERT))
                                findNavController().navigateUp()
                            },
                            onEditEvent = {
                                viewmodel.onEvent(ContainerEvents.OnCategoryStateChange(CategoryState.UPDATE))
                            }
                        )
                        when(screenType){
                            ScreenType.CATEGORY -> {
                                CategoryComponent(
                                    categoryList = categoryList,
                                    selectedCategory = selectedCategoryIndex,
                                    colorPickerSelectedColor = selectedPaletteColor,
                                    colorPickerSelectedEmoji = selectedEmoji,
                                    categoryName = newCategoryName,
                                    categoryMode = categoryMode,
                                    onCategoryListUpdate = {
                                        viewmodel.onEvent(ContainerEvents.OnCategoryListUpdate(CategoryState.INSERT))
                                    },
                                    onCategoryDelete = {index -> viewmodel.onEvent(ContainerEvents.OnCategoryDelete(index)) },
                                    onCategoryTitleUpdate = { index,oldTitle, title ->
                                        viewmodel.onEvent(ContainerEvents.OnCategoryTitleUpdate(index = index, oldName = oldTitle, name = title))
                                    },
                                    onCategoryNameChange = {
                                        viewmodel.onEvent(ContainerEvents.OnCategoryNameUpdate(it))
                                    },
                                    onColorPickerEvent = {
                                        viewmodel.onEvent(ContainerEvents.ColorPickerToggle(true))
                                    },
                                    onCategorySelectedEvent = {
                                        if(!isEditMode){
                                            viewmodel.onEvent(ContainerEvents.OnCategorySelected(it))
                                            findNavController().navigateUp()
                                        }
                                    },
                                    onNewCategoryEvent = {
                                        viewmodel.onEvent(ContainerEvents.NewCategoryInsert(it))
                                    }
                                )
                            }
                            ScreenType.REPEAT -> {
                                RepeatComponent(
                                    repeatList = repeatList,
                                    selectedIndex = selectedRepeatIndex,
                                    onRepeatModeChange = {
                                        viewmodel.onEvent(ContainerEvents.OnRepeatTypeSelected(it))
                                        findNavController().navigateUp()
                                    }
                                )
                            }
                        }
                    }
                    if (colorPickerState){
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CustomColorPicker(
                                isVisible = colorPickerState,
                                colorPaletteDialog = {
                                      viewmodel.onEvent(ContainerEvents.ColorPickerToggle(it))
                                },
                                selectedColor = selectedPaletteColor,
                                onColorChange = {
                                    viewmodel.onEvent(ContainerEvents.OnPaletteColorSelection(it))
                                },
                                onEmojiChange = {
                                    viewmodel.onEvent(ContainerEvents.OnPaletteEmojiSelection(it))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}