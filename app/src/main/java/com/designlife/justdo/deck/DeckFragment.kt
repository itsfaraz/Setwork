package com.designlife.justdo.deck

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.navigation.findNavController
import com.designlife.justdo.R
import com.designlife.justdo.common.domain.entities.FlashCard
import com.designlife.justdo.common.utils.AppServiceLocator
import com.designlife.justdo.common.utils.constants.Constants
import com.designlife.justdo.common.utils.enums.ScreenType
import com.designlife.justdo.deck.presentation.components.CreateCardListComponent
import com.designlife.justdo.deck.presentation.components.CustomCardButton
import com.designlife.justdo.deck.presentation.components.DeckBottomBarComponent
import com.designlife.justdo.deck.presentation.components.DeckHeader
import com.designlife.justdo.deck.presentation.components.EditCardListComponent
import com.designlife.justdo.deck.presentation.components.PreviewCardListComponent
import com.designlife.justdo.deck.presentation.events.DeckEvents
import com.designlife.justdo.deck.presentation.viewmodel.DeckViewModel
import com.designlife.justdo.deck.presentation.viewmodel.DeckViewModelFactory
import com.designlife.justdo.note.presentation.enums.DeckMode
import com.designlife.justdo.note.presentation.enums.NoteMode
import com.designlife.justdo.note.presentation.events.NoteEvents
import com.designlife.justdo.ui.theme.PrimaryBackgroundColor
import com.designlife.justdo.ui.theme.UIComponentBackground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DeckFragment : Fragment() {
    private lateinit var viewModel: DeckViewModel
    private var deckMode = DeckMode.CREATE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deckRepository =
            AppServiceLocator.provideDeckRepository(requireActivity().applicationContext)
        val categoryRepository =
            AppServiceLocator.provideCategoryRepository(requireActivity().applicationContext)
        val factory = DeckViewModelFactory(deckRepository, categoryRepository)
        viewModel = ViewModelProvider(this, factory)[DeckViewModel::class.java]
        CoroutineScope(Dispatchers.IO).launch {
            async {
                viewModel.fetchCategories()
            }.await()
            val deckId = arguments?.getLong("deckId") ?: -1L
            val index = arguments?.getInt("categoryIndex") ?: -1
            if (index != -1) {
                viewModel.onEvent(DeckEvents.OnCategoryIndexChange(index))
            }
            if (deckId != -1L) {
                deckMode = DeckMode.UPDATE
                viewModel.fetchDeckById(deckId)
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val scope = rememberCoroutineScope()
                val listState = rememberLazyListState()
                val editListState = rememberLazyListState()
                val previewListState = rememberLazyListState()
                val headerTitle = viewModel.headerTitle.value
                val cardList = viewModel.cardList.value
                val viewModeVisibility = viewModel.deckToggle.value
                val editState = viewModel.editState.value
                val categoryList = viewModel.categoryList.value
                val selectedCategoryIndex = viewModel.selectedCategoryIndex.value
                val themeColor = viewModel.themeColor.value
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = UIComponentBackground.value),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedVisibility(
                            visible = !viewModeVisibility
                        ){
                            DeckHeader(
                                headerTitle = headerTitle,
                                onTitleChange = {
                                    viewModel.onEvent(DeckEvents.OnHeaderChange(it))
                                },
                                onCloseEvent = {
                                    findNavController().navigateUp()
                                },
                                isEdit = editState,
                                onButtonClickEvent = {
                                    viewModel.onEvent(DeckEvents.OnEditStateChange(false))
                                    viewModel.onEvent(DeckEvents.OnPersistCardChanges)
                                },
                                categoryList = categoryList,
                                selectedCategoryIndex = selectedCategoryIndex,
                                onCategoryIndexChange = {
                                    viewModel.onEvent(DeckEvents.OnCategoryIndexChange(it))
                                },
                                addNewCategory = {
                                    val bundle = bundleOf()
                                    bundle.putInt(Constants.SCREEN_TYPE, ScreenType.CATEGORY.ordinal)
                                    findNavController().navigate(
                                        R.id.containerFragment,
                                        bundle
                                    )
                                }
                            )
                        }
                        // Create Card Section
                        AnimatedVisibility(
                            visible = !viewModeVisibility,
                            enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically)
                        ) {
                            CreateCardListComponent(
                                modifier = Modifier
                                    .fillMaxHeight(.7F)
                                    .fillMaxWidth(),
                                listState = listState,
                                deckTheme = themeColor,
                                cards = cardList,
                                onDeleteEvent = { index ->
                                    viewModel.onEvent(DeckEvents.OnCardRemove(index))
                                    scope.launch {
                                        listState.animateScrollToItem(if (index + 1 == cardList.size) 0 else index + 1)
                                    }
                                },
                                onExpandEvent = { index ->
                                    scope.launch {
                                        editListState.animateScrollToItem(listState.firstVisibleItemIndex)
                                    }
                                    viewModel.onEvent(DeckEvents.OnDeckToggle)
                                }
                            )
                        }
                        // Preview create card
                        AnimatedVisibility(
                            visible = !viewModeVisibility,
                            enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically)
                        ) {
                            PreviewCardListComponent(
                                listState = previewListState,
                                cards = cardList,
                                visibleItemIndex = listState.firstVisibleItemIndex,
                                onPreviewCardEvent = { index ->
                                    scope.launch {
                                        listState.animateScrollToItem(index + 1)
                                    }
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(if (!viewModeVisibility) 15.dp else 0.dp))
                        AnimatedVisibility(
                            visible = viewModeVisibility,
                            enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                            exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
                        ) {
                            EditCardListComponent(
                                scope = scope,
                                listState = editListState,
                                deckTheme = themeColor,
                                cards = cardList,
                                editState = editState,
                                onUpdateEvent = { index: Int, card: FlashCard ->
                                    viewModel.onEvent(DeckEvents.OnUpdateCardChange(index, card))
                                },
                                onEditStateChange = {
                                    viewModel.onEvent(DeckEvents.OnEditStateChange(it))
                                }
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedVisibility(visible = !viewModeVisibility) {
                            CustomCardButton {
                                viewModel.onEvent(DeckEvents.OnCreateCard)
                                scope.launch {
                                    listState.animateScrollToItem(viewModel.cardList.value.size)
                                    editListState.animateScrollToItem(viewModel.cardList.value.size)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(if (!viewModeVisibility) 15.dp else 0.dp))
                        DeckBottomBarComponent(
                            listState = editListState,
                            viewModeVisible = viewModeVisibility,
                            onShowStackEvent = {
                                viewModel.onEvent(DeckEvents.OnDeckToggle)
                                if (it) {
                                    viewModel.onEvent(DeckEvents.OnEditStateChange(false))
                                    viewModel.onEvent(DeckEvents.OnPersistCardChanges)
                                }
                            },
                            onNextCardEvent = {
                                val nextIndex = getNextIndex(cardList, it)
                                scope.launch(Dispatchers.Main) {
                                    editListState.animateScrollToItem(nextIndex)
                                    previewListState.scrollToItem(nextIndex)
                                    listState.scrollToItem(nextIndex)
                                }
                            },
                            onPreviousCardEvent = {
                                val previousIndex = getPreviousIndex(cardList, it)
                                scope.launch(Dispatchers.Main) {
                                    editListState.animateScrollToItem(previousIndex)
                                    previewListState.scrollToItem(previousIndex)
                                    listState.scrollToItem(previousIndex)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun getPreviousIndex(cardList: MutableList<FlashCard>, index: Int): Int {
        return if (index - 1 == -1) 0
        else index - 1
    }

    private fun getNextIndex(cardList: MutableList<FlashCard>, index: Int): Int {
        return if (index + 1 == cardList.size+1) cardList.lastIndex
        else index + 1
    }

    override fun onStop() {
        super.onStop()
        if (deckMode == DeckMode.CREATE) {
            viewModel.insertDeck()
        } else if (deckMode == DeckMode.UPDATE) {
            viewModel.updateDeck()
        }
        if (viewModel.hasDeckModified.value) {
            Toast.makeText(requireActivity(), "Card's Saved", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}