package com.designlife.justdo.deck

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.designlife.justdo.R
import com.designlife.justdo.common.domain.entities.FlashCard
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.DeckRepository
import com.designlife.justdo.common.domain.repositories.NoteRepository
import com.designlife.justdo.common.presentation.components.TopPaddingComponent
//import com.designlife.justdo.common.utils.AppServiceLocator
import com.designlife.justdo.common.utils.constants.Constants
import com.designlife.justdo.common.utils.enums.ScreenType
import com.designlife.justdo.deck.presentation.components.CreateCardListComponent
import com.designlife.justdo.deck.presentation.components.CustomCardButton
import com.designlife.justdo.deck.presentation.components.DeckBottomBarComponent
import com.designlife.justdo.deck.presentation.components.DeckHeaderComponent
import com.designlife.justdo.deck.presentation.components.EditCardListComponent
import com.designlife.justdo.deck.presentation.components.PreviewCardListComponent
import com.designlife.justdo.deck.presentation.events.DeckEvents
import com.designlife.justdo.deck.presentation.viewmodel.DeckViewModel
import com.designlife.justdo.deck.presentation.viewmodel.DeckViewModelFactory
import com.designlife.justdo.note.presentation.enums.DeckMode
import com.designlife.justdo.ui.theme.UIComponentBackground
import com.designlife.orchestrator.SchedulingEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class DeckFragment : Fragment() {
    private lateinit var viewModel: DeckViewModel
    private val deckRepository : DeckRepository by inject()
    private val categoryRepository : CategoryRepository by inject()
    private var deckMode = DeckMode.CREATE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val factory = DeckViewModelFactory(deckRepository, categoryRepository)
        viewModel = ViewModelProvider(this, factory)[DeckViewModel::class.java]

        viewModel.onEvent(DeckEvents.OnCategoryDemand)
        val deckId = arguments?.getLong("deckId") ?: -1L
        val index = arguments?.getInt("categoryIndex") ?: -1
        if (index != -1) {
            viewModel.onEvent(DeckEvents.OnCategoryIndexChange(index))
        }

        if (deckId != -1L) {
            deckMode = DeckMode.UPDATE
            viewModel.onEvent(DeckEvents.OnDeckDemand(deckId))
        }

        lifecycleScope.launch {
            viewModel.atomicWrite.collect {
                findNavController().navigateUp()
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
                        TopPaddingComponent()
                        AnimatedVisibility(
                            visible = !viewModeVisibility
                        ){
                            DeckHeaderComponent(
                                headerTitle = headerTitle,
                                onTitleChange = {
                                    viewModel.onEvent(DeckEvents.OnHeaderChange(it))
                                },
                                onCloseEvent = {
                                    try {
                                        saveDeck(false)
                                        if (viewModel.hasDeckModified.value) {
                                            Toast.makeText(requireActivity(), "Card's Saved", Toast.LENGTH_SHORT).show()
                                        }
                                        findNavController().navigateUp()
                                    }catch (e : Exception){
                                        e.printStackTrace()
                                    }
                                },
                                progressState = viewModel.progressState.value,
                                isEdit = editState,
                                onAutoSaveEvent = {
                                    try {
                                        saveDeck(true)
                                    }catch (e : Exception){
                                        e.printStackTrace()
                                    }
                                },
                                onDeleteButtonClickEvent = {
                                    viewModel.onEvent(DeckEvents.OnDeckDeleteEvent)
                                    findNavController().navigateUp()
                                },
                                categoryList = categoryList,
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
                                    .fillMaxHeight(.68F)
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
                                onExpandEvent = { _ ->
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
                            .padding(bottom = 36.dp)
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
                        Spacer(modifier = Modifier.height(if (!viewModeVisibility) 5.dp else 0.dp))
                        DeckBottomBarComponent(
                            listState = editListState,
                            viewModeVisible = viewModeVisibility,
                            onShowStackEvent = {
                                try{
                                    viewModel.onEvent(DeckEvents.OnDeckToggle)
                                    viewModel.onEvent(DeckEvents.OnEditStateChange(false))
                                    if (it) {
                                        viewModel.onEvent(DeckEvents.OnPersistCardChanges)
                                    }
                                }catch (e : Exception){
                                    e.printStackTrace()
                                }

                            },
                            onNextCardEvent = { index ->
                                scope.launch(Dispatchers.Main.immediate) {
                                    val nextIndex = getNextIndex(cardList, index)
                                    editListState.animateScrollToItem(nextIndex+1)
                                    previewListState.scrollToItem(nextIndex+1)
                                    listState.scrollToItem(nextIndex+1)
                                }
                            },
                            onPreviousCardEvent = {
                                scope.launch(Dispatchers.Main.immediate) {
                                    val previousIndex = getPreviousIndex(it)
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isEnabled) {
                    try {
                        saveDeck(false)
                    }catch (e : Exception) {
                        e.printStackTrace()
                    }finally {
                        if (viewModel.hasDeckModified.value) {
                            Toast.makeText(requireActivity(), "Card's Saved", Toast.LENGTH_SHORT).show()
                        }
                    }
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onResume() {
        super.onResume()
        parentFragmentManager.setFragmentResultListener(
            Constants.CONTAINER_VIEW,
            this
        ) { _, bundle ->
            lifecycleScope.launch(Dispatchers.Main.immediate) {
                val selectedIndex = bundle.getInt(Constants.CONTAINER_POP_INDEX)
                viewModel.onEvent(DeckEvents.OnCategoryIndexChange(selectedIndex))
            }
        }
    }

    private fun getPreviousIndex(index: Int): Int {
        return if (index - 1 == -1) 0
        else index - 1
    }

    private fun getNextIndex(cardList: MutableList<FlashCard>, index: Int): Int {
        return if (index == 0 ) { 1 }
        else if (index < cardList.lastIndex) { index + 1 }
        else { cardList.lastIndex }
    }

    private fun saveDeck(isAutoSave : Boolean){
        try {
            if (deckMode == DeckMode.CREATE) {
                deckMode = DeckMode.UPDATE
                viewModel.onEvent(DeckEvents.OnInsert(isAutoSave))
            } else if (deckMode == DeckMode.UPDATE) {
                viewModel.onEvent(DeckEvents.OnUpdate(isAutoSave))
            }
        }catch (e : Exception){
            e.printStackTrace()
            Log.e("FLOW", "saveDeck: saved with exception ${e.message}")
        }
    }

    override fun onStop() {
        super.onStop()

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            lifecycleScope.launch {
                saveDeck(false)
                viewModel.onEvent(DeckEvents.OnClear)
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }
}