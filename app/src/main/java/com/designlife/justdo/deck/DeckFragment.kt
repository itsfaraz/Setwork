package com.designlife.justdo.deck

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
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
import com.designlife.justdo.common.utils.AppServiceLocator
import com.designlife.justdo.deck.presentation.components.CreateCardListComponent
import com.designlife.justdo.deck.presentation.components.CustomCardButton
import com.designlife.justdo.deck.presentation.components.DeckBottomBarComponent
import com.designlife.justdo.deck.presentation.components.DeckHeader
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val scope = rememberCoroutineScope()
                val listState = rememberLazyListState()
                val headerTitle = viewModel.headerTitle.value
                val cardList = viewModel.cardList.value
                val viewModeVisibility = viewModel.deckToggle.value
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
                            onCloseEvent = {}
                        )
                        AnimatedVisibility(visible = !viewModeVisibility) {
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
                                onExpandEvent = {}
                            )

                        }
                        AnimatedVisibility(visible = viewModeVisibility) {
                            Text(text = "Hello World")
                        }

                        AnimatedVisibility(visible = !viewModeVisibility) {
                            CustomCardButton {
                                viewModel.onEvent(DeckEvents.OnCreateCard)
                                scope.launch {
                                    listState.animateScrollToItem(viewModel.cardList.value.size)
                                }
                            }
                        }
                    }
                    DeckBottomBarComponent(
                        viewModeVisible = viewModeVisibility,
                        onShowStackEvent = {
                            viewModel.onEvent(DeckEvents.OnDeckToggle)
                        },
                        onNextCardEvent = {},
                        onPreviousCardEvent = {}
                    )
                }
            }
        }
    }
}