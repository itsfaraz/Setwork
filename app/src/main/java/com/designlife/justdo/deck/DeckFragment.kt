package com.designlife.justdo.deck

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import com.designlife.justdo.common.domain.entities.FlashCard
import com.designlife.justdo.common.utils.AppServiceLocator
import com.designlife.justdo.deck.presentation.components.CardEditComponent
import com.designlife.justdo.deck.presentation.components.CreateCardListComponent
import com.designlife.justdo.deck.presentation.components.CustomCardButton
import com.designlife.justdo.deck.presentation.components.DeckBottomBarComponent
import com.designlife.justdo.deck.presentation.components.DeckHeader
import com.designlife.justdo.deck.presentation.components.EditCardListComponent
import com.designlife.justdo.deck.presentation.events.DeckEvents
import com.designlife.justdo.deck.presentation.viewmodel.DeckViewModel
import com.designlife.justdo.deck.presentation.viewmodel.DeckViewModelFactory
import com.designlife.justdo.ui.theme.PrimaryBackgroundColor
import kotlinx.coroutines.launch

class DeckFragment : Fragment() {
    private lateinit var viewModel: DeckViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deckRepository = AppServiceLocator.provideDeckRepository(requireActivity().applicationContext)
        val factory = DeckViewModelFactory(deckRepository)
        viewModel = ViewModelProvider(requireActivity(),factory)[DeckViewModel::class.java]
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
                val headerTitle = viewModel.headerTitle.value
                val cardList = viewModel.cardList.value
                val updateCardsQueue = viewModel.updateCardsQueue.value
                val viewModeVisibility = viewModel.deckToggle.value
                val editState = viewModel.editState.value
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = PrimaryBackgroundColor),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DeckHeader(
                            headerTitle = headerTitle,
                            onTitleChange = {
                                viewModel.onEvent(DeckEvents.OnHeaderChange(it))
                            },
                            onCloseEvent = {},
                            isEdit = editState,
                            onButtonClickEvent = {
                                viewModel.onEvent(DeckEvents.OnEditStateChange(false))
                                viewModel.onEvent(DeckEvents.OnPersistCardChanges)
                            }
                        )
                        AnimatedVisibility(
                            visible = !viewModeVisibility,
                            enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically)
                        ) {
                            CreateCardListComponent(
                                scope = scope,
                                listState = listState,
                                deckTheme = Color.Blue,
                                cards = cardList,
                                onDeleteEvent = { index ->
                                    viewModel.onEvent(DeckEvents.OnCardRemove(index))
                                    scope.launch {
                                        listState.animateScrollToItem(if(index+1 == cardList.size) 0 else index+1)
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
                        AnimatedVisibility(
                            visible = viewModeVisibility,
                            enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                            exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
                        ) {
                            EditCardListComponent(
                                scope = scope,
                                listState = editListState,
                                deckTheme = Color.Blue,
                                cards = cardList,
                                editState = editState,
                                onUpdateEvent = { index: Int, card: FlashCard ->
                                    viewModel.onEvent(DeckEvents.OnUpdateCardChange(index,card))
                                },
                                onEditStateChange = {
                                    viewModel.onEvent(DeckEvents.OnEditStateChange(it))
                                }
                            )
                        }
                        AnimatedVisibility(visible = !viewModeVisibility) {
                            CustomCardButton {
                                viewModel.onEvent(DeckEvents.OnCreateCard)
                                scope.launch {
                                    listState.animateScrollToItem(viewModel.cardList.value.size)
                                    editListState.animateScrollToItem(viewModel.cardList.value.size)
                                }
                            }
                        }
                    }
                    DeckBottomBarComponent(
                        viewModeVisible = viewModeVisibility,
                        onShowStackEvent = {
                            viewModel.onEvent(DeckEvents.OnDeckToggle)
                            if (it){
                                viewModel.onEvent(DeckEvents.OnEditStateChange(false))
                                viewModel.onEvent(DeckEvents.OnPersistCardChanges)
                            }
                        },
                        onNextCardEvent = {},
                        onPreviousCardEvent = {}
                    )
                }
            }
        }
    }
}