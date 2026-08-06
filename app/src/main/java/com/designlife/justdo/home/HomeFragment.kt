package com.designlife.justdo.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.designlife.justdo.R
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.entities.Category
import com.designlife.justdo.common.domain.entities.Deck
import com.designlife.justdo.common.domain.entities.Note
import com.designlife.justdo.common.domain.entities.Todo
import com.designlife.justdo.common.domain.repositories.appstore.AppStoreRepository
import com.designlife.justdo.common.presentation.components.BottomSheet
import com.designlife.justdo.common.presentation.components.ProgressBar
import com.designlife.justdo.common.utils.AppServiceLocator
import com.designlife.justdo.common.utils.NavOptions
import com.designlife.justdo.common.utils.constants.Constants
import com.designlife.justdo.common.utils.entity.BottomNavItem
import com.designlife.justdo.common.utils.entity.SettingItem
import com.designlife.justdo.common.utils.enums.ViewType
import com.designlife.justdo.common.utils.getFormattedTimestamp
import com.designlife.justdo.common.utils.update.SoftwareUpdateManager
import com.designlife.justdo.home.domain.usecase.LoadIntialDatesUseCase
import com.designlife.justdo.home.domain.usecase.LoadNextDatesSetUseCase
import com.designlife.justdo.home.domain.usecase.LoadPreviousDatesSetUseCase
import com.designlife.justdo.home.presentation.components.BottomNavigationBar
import com.designlife.justdo.home.presentation.components.CategoryComponent
import com.designlife.justdo.home.presentation.components.DateComponent
import com.designlife.justdo.home.presentation.components.DeckItemList
import com.designlife.justdo.home.presentation.components.HeaderComponent
import com.designlife.justdo.home.presentation.components.NoteItemList
import com.designlife.justdo.home.presentation.components.SearchBarComponent
import com.designlife.justdo.home.presentation.components.Settings
import com.designlife.justdo.home.presentation.components.TodoItemList
import com.designlife.justdo.home.presentation.events.HomeEvents
import com.designlife.justdo.home.presentation.viewmodel.HomeViewModel
import com.designlife.justdo.home.presentation.viewmodel.HomeViewModelFactory
import com.designlife.justdo.settings.presentation.components.CustomLoaderComponent
import com.designlife.justdo.settings.presentation.components.CustomPickerComponent
import com.designlife.justdo.settings.presentation.enums.GeneralSettingView
import com.designlife.justdo.settings.presentation.events.SettingEvents
import com.designlife.justdo.settings.presentation.viewmodel.SettingViewModel
import com.designlife.justdo.settings.presentation.viewmodel.SettingViewModelFactory
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.PrimaryBackgroundColor
import com.designlife.justdo.ui.theme.PrimaryColorHome1
import com.designlife.justdo.ui.theme.PrimaryColorHome2
import com.designlife.orchestrator.data.NotificationType
import com.designlife.orchestrator.data.NotificationTypeI
import com.designlife.orchestrator.notification.clickmanager.TaskListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import androidx.core.net.toUri
import com.designlife.justdo.common.presentation.components.TopPaddingComponent
import com.designlife.justdo.common.presentation.components.appBackground
import kotlin.time.Duration.Companion.milliseconds

class HomeFragment : Fragment(), TaskListener {

    private lateinit var navigationItems: List<BottomNavItem>
    private lateinit var viewModel: HomeViewModel
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var dateListState: LazyListState
    private lateinit var todoListState: LazyListState
    private lateinit var scope: CoroutineScope
    private lateinit var appStoreRepository: AppStoreRepository
    private lateinit var softwareUpdateManager: SoftwareUpdateManager

    private lateinit var openFileLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var createFileLauncher: ActivityResultLauncher<String>


    private val settingIcons: List<SettingItem> = initSettingIcons()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBottomNavItems()
        importExport()
        val dateGenerator = IDateGenerator()
        val loadDatesUseCase = LoadIntialDatesUseCase(dateGenerator)
        val loadNextDatesUseCase = LoadNextDatesSetUseCase(dateGenerator)
        val loadPreviousDatesUseCase = LoadPreviousDatesSetUseCase(dateGenerator)
        val todoRepository =
            AppServiceLocator.provideTodoRepository(requireActivity().applicationContext)
        val categoryRepository =
            AppServiceLocator.provideCategoryRepository(requireActivity().applicationContext)
        val noteRepository =
            AppServiceLocator.provideNoteRepository(requireActivity().applicationContext)
        val deckRepository =
            AppServiceLocator.provideDeckRepository(requireActivity().applicationContext)
        val setworkProvider = AppServiceLocator.provideSetworkProvider(requireContext())
        val factory = HomeViewModelFactory(
            setworkProvider,
            dateGenerator,
            todoRepository,
            categoryRepository,
            noteRepository,
            deckRepository,
            loadDatesUseCase,
            loadNextDatesUseCase,
            loadPreviousDatesUseCase
        )
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        appStoreRepository = AppServiceLocator.provideAppStoreRepository(requireContext())
        softwareUpdateManager = AppServiceLocator.provideSoftwareUpdateManager(requireContext())
        onNotificationAvailable()
        onSetworkProviderInitialise()
        val settingFactory = SettingViewModelFactory(appStoreRepository)
        settingViewModel = ViewModelProvider(this, settingFactory)[SettingViewModel::class.java]
        lifecycleScope.launch {
            viewModel.onEvent(HomeEvents.OnProgressBarToggle(true))
            coroutineScope {
                launch { settingViewModel.initSettingPreferences() }
                launch { viewModel.fetchAllDecks() }
                launch { viewModel.loadInitialDates() }
                launch { viewModel.fetchAllTodo() }
                launch { viewModel.fetchAllCategory() }
                launch { viewModel.fetchAllNotes() }
            }
            viewModel.onEvent(HomeEvents.OnProgressBarToggle(false))
        }
    }

    private fun onSetworkProviderInitialise() {
        if (requireActivity().intent?.getBooleanExtra("fromProvider", false) == true) {
            val actionId = requireActivity().intent.getIntExtra("actionId", 0)
            val taskId = requireActivity().intent.getIntExtra("taskId", 0)
            onUserProviderEvent(actionId,taskId)
        }
    }

    private fun importExport() {
        //        Import
        openFileLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                uri?.let {
                    // Persist permission (important!)
                    requireContext().contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )

                    readFromUri(it)
                }
            }

        //        Export
        createFileLauncher =
            registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
                uri?.let {
                    requireContext().contentResolver.openOutputStream(it)?.use { stream ->
                        lifecycleScope.launch {
                            EXPORT_DATA.collect{ data ->
                                Log.d("FLOW","HomeFragment :: registerForActivityResult : Data : $data")
                                stream.write(data.toByteArray())
                            }
                        }
                    }
                }
            }
    }

    private fun readFromUri(uri: Uri) {
        val data = requireContext().contentResolver
            .openInputStream(uri)
            ?.bufferedReader()
            ?.use { it.readText() }
        data?.let {
            settingViewModel.onEvent(SettingEvents.OnImportEvent(requireContext(),it))
        }
    }

    private fun initBottomNavItems() {
        navigationItems = listOf(
            BottomNavItem("Tasks", R.drawable.ic_task_view),
            BottomNavItem("Notes", R.drawable.ic_notes_view),
            BottomNavItem("Decks", R.drawable.ic_deck_view),
            BottomNavItem("Settings", R.drawable.ic_settings)
        )
    }

    private fun initSettingIcons(): List<SettingItem> {
        return listOf(
            SettingItem("Default Screen", R.drawable.ic_default_screen),
            SettingItem("App Theme", R.drawable.ic_color_theme),
            SettingItem("Font Size", R.drawable.ic_font_size),
            SettingItem("List Item Height", R.drawable.ic_list_item_height),
            SettingItem("Import", R.drawable.ic_import),
            SettingItem("Export", R.drawable.ic_export),
            SettingItem("Help", R.drawable.ic_help),
            SettingItem("Feedback", R.drawable.ic_feedback),
            SettingItem("Software update", R.drawable.ic_autorenew),
        )
    }

    private fun onNotificationAvailable() {
        if (requireActivity().intent?.getBooleanExtra("fromNotification", false) == true) {
            val id = requireActivity().intent.getIntExtra("notificationId", 0)
            val title = requireActivity().intent.getStringExtra("title") ?: ""
            val type = requireActivity().intent.getStringExtra("type") ?: ""
            onUserNotificationEvent(id, title, type)
        }
    }

    private fun initialSlide() {
        val dateList = viewModel.dateList.value
        val currentDate = viewModel.currentDate.value
        val computeIndex = dateList.indexOf(currentDate)
        scope.launch(Dispatchers.Main.immediate) {
            if (dateList.isNotEmpty()){
                computeIndex.let { index ->
                    if (index != -1){
                        viewModel.onEvent(HomeEvents.OnIndexSelected(index))
                        dateListState.scrollToItem(index)
                        viewModel.onEvent(HomeEvents.HighlightTodoByDate(index))
                    }
                }
            }
            if (viewModel.todoList.value.isNotEmpty()){
                scrollToRollItem(viewModel.todoIndex.value, todoListState)
            }
        }
    }

    fun onUserProviderEvent(actionId: Int,taskId : Int) {
        val bundle = bundleOf()
        if (actionId == -1) {
            bundle.putBoolean(Constants.TASK_VIEW, false)
            findNavController().navigate(
                R.id.taskFragment,
                bundle,
                NavOptions.navOptionStack
            )
        }

        if (actionId == -2){
            findNavController().navigate(
                R.id.OChatFragment,
                bundle,
                NavOptions.navOptionStack
            )
        }
        if (actionId == -3){
            navigateToTaskViewById(taskId)
        }

        if (actionId == -4){
            Log.i("PROVIDER_FLOW", "onUserProviderEvent: actionId : $actionId taskId : $taskId")
        }
    }

    override fun onUserNotificationEvent(id: Int, title: String, type: String) {
        if (id != -1) {
            val notificationType = NotificationTypeI.getType(type)
            when (notificationType) {
                NotificationType.TASK_NOTIFY -> {
                    navigateToTaskViewById(id)
                }

                NotificationType.NOTE_NOTIFY -> {
                    navigateToNoteViewById(id)
                }

                NotificationType.DECK_NOTIFY -> {
                    navigateToDeckViewById(id)
                }

                NotificationType.APP_UPDATE -> {
                    if (title == "Software Update" && id == 10001) {
                        softwareUpdateManager.installUpdate()
                    }
                }

                NotificationType.COMMON_NOTIFY -> {
                }
            }
        }
    }

    suspend fun highlightToday() {
        val date = viewModel.currentDate.value
        val index = viewModel.dateList.value.indexOf(date)
        if (index!=-1){
            viewModel.onEvent(HomeEvents.OnIndexSelected(index))
            dateListState.scrollToItem(index)
            viewModel.onEvent(HomeEvents.HighlightTodoByDate(index))
        }
        if (viewModel.todoIndex.value != -1){
            todoListState.scrollToItem(viewModel.todoIndex.value)
        }
    }

    @OptIn(
        ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
        ExperimentalAnimationApi::class, ExperimentalAnimationApi::class
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val legacyScroll = remember {
                    mutableStateOf(true)
                }
                dateListState = rememberLazyListState()
                scope = rememberCoroutineScope()
                val selectedIndex = viewModel.selectedIndex.value
                val dateList = viewModel.dateList.value
                val categoryList = viewModel.categoryList.value
                val todoList = viewModel.todoList.value
                todoListState = rememberLazyListState()
                val noteListState = rememberLazyStaggeredGridState()
                val deckListState = rememberLazyListState()
                val searchToggle = viewModel.searchToggle.value
                val searchText = viewModel.searchText.value
                val noteList =
                    if (searchText.isNotEmpty()) viewModel.searchList.value else viewModel.noteList.sortedByDescending { it.lastModified.time }
                val deckList =
                    if (searchText.isNotEmpty()) viewModel.searchList.value else viewModel.deckList.sortedByDescending { it.modifiedDate.time }
                val colorMap = viewModel.colorMap.value
                val currentMonth = viewModel.currentMonth.value
                val currentYear = viewModel.currentYear.value
                val currentDate = viewModel.currentDate.value
                val selectedCategoryIndex = viewModel.selectedCategoryIndex.value
                val sheetVisibility = viewModel.sheetVisibility.value
                val isBottomSheetToggled = remember {
                    mutableStateOf(false)
                }
                val viewType = viewModel.viewType.value
                // Setting Config
                val pickerState = settingViewModel.pickerVisibility.value
                val pickerListState = settingViewModel.pickerItemList.value
                val loaderState = settingViewModel.loaderVisibility.value
                val loaderStatus = settingViewModel.loaderStatus.value
                val isDarkMode = SettingViewModel.darkModeStatus.value
                todoListIE = viewModel.todoList.value
                noteListIE = viewModel.noteList
                deckListIE = viewModel.deckList
                categoryListIE = viewModel.categoryList.value
                LaunchedEffect(viewModel.isLoaded.value) {
                    try {
                        initialSlide()
                    }catch (e : Exception){
                        e.printStackTrace()
                        delay(200.milliseconds)
                        initialSlide()
                    }
                }

                LaunchedEffect(legacyScroll.value) {
                    try {
                        scope.launch {
                            delay(300.milliseconds)
                            legacyScroll.value = false
                            highlightToday()
                        }
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(if (viewModel.progressBarVisibility.value) 0.7F else 1F)
                            .blur(radius = if (viewModel.progressBarVisibility.value) 7.dp else 0.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = if (viewType == ViewType.SETTING) listOf(
                                                PrimaryBackgroundColor.value,
                                                PrimaryBackgroundColor.value
                                            )
                                            else listOf(
                                                PrimaryColorHome2.value,
                                                PrimaryColorHome1.value
                                            )
                                        )
                                    )
                                    .appBackground(enable = viewType != ViewType.SETTING)

                            ) {
                                TopPaddingComponent()
                                AnimatedVisibility(visible = viewType != ViewType.SETTING) {
                                    HeaderComponent(
                                        onEventClick = {
                                            scope.launch {
                                                highlightToday()
                                            }
                                        },
                                        currentDate = Date(System.currentTimeMillis()),
                                        viewType = viewType,
                                        searchIconVisibility = viewType != ViewType.TASK && !searchToggle,
                                        onSearchIconClick = {
                                            viewModel.onEvent(HomeEvents.OnSearchToggle(true))
                                        },
                                        onChatIconEvent = {
                                            findNavController().navigate(
                                                R.id.OChatFragment,
                                                null,
                                                NavOptions.navOptionStack
                                            )
                                        }
                                    )
                                }
                                AnimatedVisibility(visible = viewType != ViewType.SETTING) {
                                    Spacer(modifier = Modifier.height(if (searchToggle) 20.dp else 0.dp))
                                }
                                AnimatedVisibility(visible = searchToggle) {
                                    SearchBarComponent(
                                        searchText = searchText,
                                        onSearchUpdates = {
                                            viewModel.onEvent(HomeEvents.OnSearchUpdate(it))
                                        },
                                        onClearSearch = {
                                            viewModel.onEvent(HomeEvents.OnClearSearch)
                                            viewModel.onEvent(HomeEvents.OnSearchToggle(false))
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.height(if (viewType == ViewType.TASK) 20.dp else 0.dp))
                                AnimatedVisibility(visible = viewType == ViewType.TASK) {
                                    DateComponent(
                                        listState = dateListState,
                                        currentDate = currentDate,
                                        currentMonth = currentMonth,
                                        currentYear = currentYear,
                                        dateList = dateList,
                                        onEventClick = {
                                            viewModel.onEvent(HomeEvents.HighlightTodoByDate(it))
                                            scope.launch(Dispatchers.Main) {
                                                scrollToRollItem(
                                                    viewModel.todoIndex.value,
                                                    todoListState
                                                )
                                                viewModel.onEvent(HomeEvents.OnIndexSelected(it))
                                            }
                                        },
                                        onChangeVisibleDate = {
                                            viewModel.onYearChange(it)
                                            viewModel.onMonthChange(it)
                                        },
                                        loadPreviousTrigger = {
                                            scope.launch(Dispatchers.Main) {
                                                viewModel.loadPreviousMonth()
                                                scrollToRollItem(
                                                    viewModel.currentDateIndex.value,
                                                    dateListState
                                                )
                                            }
                                        },
                                        loadNextTrigger = {
                                            scope.launch(Dispatchers.Main) {
                                                viewModel.loadNextMonth()
                                                scrollToRollItem(
                                                    viewModel.currentDateIndex.value,
                                                    dateListState
                                                )
                                            }
                                        },
                                        selectedIndex = selectedIndex
                                    )
                                }
                                AnimatedVisibility(visible = viewType != ViewType.SETTING) {
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                                AnimatedVisibility(visible = viewType != ViewType.SETTING) {
                                    CategoryComponent(
                                        viewType = viewType,
                                        categoryList = categoryList,
                                        selectedCategoryIndex = selectedCategoryIndex
                                    ) { categoryIndex ->
                                        viewModel.onEvent(
                                            HomeEvents.OnCategorySortSelected(
                                                categoryIndex
                                            )
                                        )
                                    }
                                }
                                AnimatedVisibility(visible = viewType != ViewType.SETTING) {
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                                AnimatedVisibility(
                                    visible = viewType == ViewType.TASK,
                                ) {
                                    TodoItemList(
                                        listState = todoListState,
                                        todoList = todoList,
                                        colorMap = colorMap,
                                        onSwipeLeftEvent = {
                                        },
                                        onSwipeRightEvent = {
                                            swipeActionEvent(ViewType.NOTE)
                                        },
                                        onFirstIndexChangeEvent = { date ->
                                            viewModel.onEvent(HomeEvents.HighlightDateByTodo(date))
                                            scope.launch(Dispatchers.Main) {
                                                scrollToRollItem(
                                                    viewModel.currentDateIndex.value,
                                                    dateListState
                                                )
                                            }
                                        },
                                    ) { todoId ->
                                        navigateToTaskViewById(todoId)
                                    }
                                }
                                AnimatedVisibility(visible = viewType == ViewType.NOTE) {
                                    @Suppress("UNCHECKED_CAST")
                                    NoteItemList(
                                        listState = noteListState,
                                        noteList = noteList as List<Note>,
                                        colorMap = colorMap,
                                        onSwipeLeftEvent = {
                                            swipeActionEvent(ViewType.TASK)
                                        },
                                        onSwipeRightEvent = {
                                            swipeActionEvent(ViewType.DECK)
                                        },
                                        onNoteClickEvent = {
                                            val bundle = bundleOf()
                                            bundle.putLong("noteId", noteList[it].noteId)
                                            bundle.putInt(
                                                "categoryIndex",
                                                getCategoryIndexFromNote(noteList[it])
                                            )
                                            findNavController().navigate(
                                                R.id.noteFragment,
                                                bundle,
                                                NavOptions.navOptionStack
                                            )
                                        }
                                    )
                                }
                                AnimatedVisibility(visible = viewType == ViewType.DECK) {
                                    @Suppress("UNCHECKED_CAST")
                                    DeckItemList(
                                        listState = deckListState,
                                        deckList = deckList as List<Deck>,
                                        colorMap = colorMap,
                                        onSwipeLeftEvent = {
                                            swipeActionEvent(ViewType.NOTE)
                                        },
                                        onSwipeRightEvent = {
                                            swipeActionEvent(ViewType.SETTING)
                                        },
                                        onDeckClickEvent = { index ->
                                            val bundle = bundleOf()
                                            bundle.putInt(
                                                "categoryIndex",
                                                getCategoryIndexFromDeck(deckList[index])
                                            )
                                            bundle.putLong("deckId", deckList[index].deckId)
                                            findNavController().navigate(
                                                R.id.deckFragment,
                                                bundle,
                                                NavOptions.navOptionStack
                                            )
                                        }
                                    )
                                }
                                AnimatedVisibility(
                                    visible = viewType == ViewType.SETTING,
                                    enter = slideInVertically() + expandVertically(expandFrom = Alignment.Bottom),
                                    exit = slideOutVertically() + shrinkVertically(shrinkTowards = Alignment.Bottom)
                                ) {
                                    Settings(
                                        iconList = settingIcons,
                                        pickerState = pickerState,
                                        loaderState = loaderState,
                                        onSwipeLeftEvent = {
                                            swipeActionEvent(ViewType.DECK)
                                        },
                                        onSwipeRightEvent = {

                                        },
                                        onDefaultScreenEvent = {
                                            settingViewModel.onEvent(
                                                SettingEvents.OnGeneralSettingViewChange(
                                                    GeneralSettingView.DEFAULT_SCREEN
                                                )
                                            )
                                        },
                                        onAppThemeEvent = {
                                            settingViewModel.onEvent(
                                                SettingEvents.OnGeneralSettingViewChange(
                                                    GeneralSettingView.APP_THEME
                                                )
                                            )
                                        },
                                        onFontSizeEvent = {
                                            settingViewModel.onEvent(
                                                SettingEvents.OnGeneralSettingViewChange(
                                                    GeneralSettingView.FONT_SIZE
                                                )
                                            )
                                        },
                                        onListHeightEvent = {
                                            settingViewModel.onEvent(
                                                SettingEvents.OnGeneralSettingViewChange(
                                                    GeneralSettingView.LIST_HEIGHT
                                                )
                                            )
                                        },
                                        onImportEvent = {
                                            openFileLauncher.launch(arrayOf("*/*"))
                                        },
                                        onExportEvent = {
                                            lifecycleScope.launch {
                                                val fileName = "Setwork_${getFormattedTimestamp(System.currentTimeMillis())}.json"
                                                settingViewModel.onEvent(SettingEvents.OnExportEvent)
                                                delay(600.milliseconds)
                                                if (settingViewModel.isExportReady.value){
                                                    settingViewModel.onEvent(SettingEvents.OnImportExportCompute(false))
                                                    createFileLauncher.launch(fileName)
                                                }
                                            }
                                        },
                                        onHelpEvent = {
                                            helpMailToOrangeBytes()
                                        },
                                        onFeedbackEvent = {
                                            feedbackMailToOrangeBytes()
                                        },
                                        onSoftwareUpdateEvent = {
                                            softwareUpdateManager.checkForUpdate()
                                            Toast.makeText(
                                                requireContext(),
                                                "Checking Software Updates",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        onGeneralSettingItemClick = {
                                            settingViewModel.onEvent(
                                                SettingEvents.OnPickerToggle(
                                                    true
                                                )
                                            )
                                        },
                                        onBackupSettingItemClick = {
                                            settingViewModel.onEvent(SettingEvents.OnLoaderToggle(true))
                                        }
                                    )
                                }
                            }
                            if (sheetVisibility) {
                                val dialog = BottomSheet.dialog(
                                    context = requireActivity(),
                                    isDarkMode = isDarkMode,
                                    onCloseEvent = {
                                        isBottomSheetToggled.value = false
                                        viewModel.updateSheetVisibility(false)
                                    },
                                    onTaskEvent = {

                                    },
                                    onNoteEvent = {

                                    },
                                    onDeckEvent = {

                                    }
                                )
                                if (!isBottomSheetToggled.value) {
                                    dialog.show()
                                    isBottomSheetToggled.value = true
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(WindowInsets.navigationBars.asPaddingValues()),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            BottomNavigationBar(
                                items = navigationItems,
                                selectedScreen = viewType
                            ) {
                                swipeActionEvent(it)
                            }
                        }
                        if (viewModel.progressBarVisibility.value) {
                            ProgressBar()
                        }
                        if (viewType == ViewType.SETTING) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                AnimatedVisibility(
                                    visible = pickerState,
                                    enter = scaleIn(),
                                    exit = scaleOut()
                                ) {
                                    CustomPickerComponent(
                                        itemList = pickerListState,
                                        onCloseClick = {
                                            if (pickerState) {
                                                settingViewModel.onEvent(
                                                    SettingEvents.OnPickerToggle(
                                                        false
                                                    )
                                                )
                                            }
                                        },
                                        onItemClick = { index ->
                                            settingViewModel.onEvent(
                                                SettingEvents.OnPickerItemClick(
                                                    index
                                                )
                                            )
                                        })
                                }
                                AnimatedVisibility(
                                    visible = loaderState,
                                    enter = scaleIn(),
                                    exit = scaleOut()
                                ) {
                                    CustomLoaderComponent(loaderData = loaderStatus)
                                }
                            }
                        }
                        AnimatedVisibility(
                            visible = viewType != ViewType.SETTING,
                            enter = scaleIn(),
                            exit = scaleOut()
                        ) {
                            androidx.compose.material.FloatingActionButton(
                                modifier = Modifier
                                    .padding(bottom = 115.dp, end = 20.dp)
                                    .wrapContentSize(),
                                onClick = {
                                    navigateByView(viewType)
                                },
                                backgroundColor = ButtonPrimary.value
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "FAB",
                                    tint = PrimaryColorHome2.value
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    private fun swipeActionEvent(viewType: ViewType){
        viewModel.onEvent(HomeEvents.OnViewChange(viewType))
        viewModel.onEvent(HomeEvents.OnClearSearch)
        viewModel.onEvent(HomeEvents.OnSearchToggle(false))
        // impossible index
        viewModel.onEvent(HomeEvents.OnCategorySortSelected(-1))
    }

    private fun getCategoryIndexFromNote(note: Note): Int {
        return getIndexFromId(note.categoryId)
    }

    private fun getCategoryIndexFromDeck(deck: Deck): Int {
        return getIndexFromId(deck.categoryId)
    }

    private fun getIndexFromId(categoryId: Long): Int {
        val categoryList = viewModel.categoryList.value
        val categoryInstance = categoryList.find { it.id == categoryId }
        return categoryList.indexOf(categoryInstance)
    }

    override fun onResume() {
        super.onResume()
        settingViewModel.onEvent(SettingEvents.OnLoaderToggle(false))
        viewModel.archiveTodos(viewModel.todoList.value)
        if (::scope.isInitialized){
            scope.launch {
                highlightToday()
            }
        }
    }

    private fun navigateToTaskViewById(todoId: Int) {
        val bundle = bundleOf()
        bundle.putBoolean(Constants.TASK_VIEW, true)
        bundle.putInt(Constants.TASK_VIEW_ID, todoId)
        findNavController().navigate(
            R.id.taskFragment,
            bundle,
            NavOptions.navOptionStack
        )
    }

    private fun navigateToNoteViewById(noteId: Int) {
        val bundle = bundleOf()
        bundle.putBoolean(Constants.NOTE_VIEW, true)
        bundle.putInt(Constants.NOTE_VIEW_ID, noteId)
        findNavController().navigate(
            R.id.noteFragment,
            bundle,
            NavOptions.navOptionStack
        )
    }

    private fun navigateToDeckViewById(deckId: Int) {
        val bundle = bundleOf()
        bundle.putBoolean(Constants.DECK_VIEW, true)
        bundle.putInt(Constants.DECK_VIEW_ID, deckId)
        Log.i("NOTIFICATION_FLOW", "HomeFragment :: navigateToNoteViewById: navigated to todoId : $deckId")
        findNavController().navigate(
            R.id.deckFragment,
            bundle,
            NavOptions.navOptionStack
        )
    }

    private suspend fun scrollToRollItem(currentDateIndex: Int, listState: LazyListState) {
        if (currentDateIndex != -1) {
            listState.animateScrollToItem(currentDateIndex)
        }
    }

    private fun navigateByView(viewType: ViewType) {
        val bundle = bundleOf()
        when (viewType) {
            ViewType.TASK -> {
                bundle.putBoolean(Constants.TASK_VIEW, false)
                findNavController().navigate(
                    R.id.taskFragment,
                    bundle,
                    NavOptions.navOptionStackSlide
                )
            }

            ViewType.DECK -> {
                bundle.putLong("deckId", -1L)
                bundle.putInt("categoryIndex", 0)
                findNavController().navigate(
                    R.id.deckFragment,
                    bundle,
                    NavOptions.navOptionStackSlide
                )
            }

            ViewType.NOTE -> {
                bundle.putLong("noteId", -1L)
                bundle.putInt("categoryIndex", 0)
                findNavController().navigate(
                    R.id.noteFragment,
                    bundle,
                    NavOptions.navOptionStackSlide
                )
            }

            else -> {}
        }
    }

    private fun feedbackMailToOrangeBytes() {
        val email = arrayOf("feedback.orangebytes@protonmail.com")
        val subject = "Setwork - Feedback"

        val gmailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, email)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            setPackage("com.google.android.gm")
        }

        try {
            startActivity(gmailIntent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()

            // 2. Fallback → show chooser with all email apps
            val fallbackIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, email)
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }

            try {
                startActivity(Intent.createChooser(fallbackIntent, "Write us on a mail"))
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "No email apps installed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    private fun helpMailToOrangeBytes() {
        val email = arrayOf("help.orangebytes@proton.me")
        val subject = "Setwork - Help"

        val gmailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, email)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            setPackage("com.google.android.gm")
        }

        try {
            startActivity(gmailIntent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()

            // 2. Fallback → show chooser with all email apps
            val fallbackIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, email)
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }

            try {
                startActivity(Intent.createChooser(fallbackIntent, "Write us on a mail"))
            } catch (exception: ActivityNotFoundException) {
                exception.printStackTrace()
                Toast.makeText(requireContext(), "No email apps installed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }

    companion object{
        internal var todoListIE : List<Todo> = mutableListOf()
        internal var deckListIE : List<Deck> = mutableListOf()
        internal var noteListIE : List<Note> = mutableListOf()
        internal var categoryListIE : List<Category> = mutableListOf()
        internal var EXPORT_DATA : MutableStateFlow<String> = MutableStateFlow("")
    }
}