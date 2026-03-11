package com.designlife.justdo.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.designlife.justdo.R
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.entities.Deck
import com.designlife.justdo.common.domain.entities.Note
import com.designlife.justdo.common.domain.repositories.appstore.AppStoreRepository
import com.designlife.justdo.common.presentation.components.BottomSheet
import com.designlife.justdo.common.presentation.components.ProgressBar
import com.designlife.justdo.common.utils.AppServiceLocator
import com.designlife.justdo.common.utils.NavOptions
import com.designlife.justdo.common.utils.constants.Constants
import com.designlife.justdo.common.utils.entity.BottomNavItem
import com.designlife.justdo.common.utils.entity.SettingItem
import com.designlife.justdo.common.utils.enums.ViewType
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
import com.designlife.justdo.settings.presentation.enums.AppBackup
import com.designlife.justdo.settings.presentation.enums.GeneralSettingView
import com.designlife.justdo.settings.presentation.events.SettingEvents
import com.designlife.justdo.settings.presentation.viewmodel.SettingViewModel
import com.designlife.justdo.settings.presentation.viewmodel.SettingViewModelFactory
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.PrimaryBackgroundColor
import com.designlife.justdo.ui.theme.PrimaryColorHome1
import com.designlife.justdo.ui.theme.PrimaryColorHome2
import com.designlife.orchestrator.notification.clickmanager.TaskListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

class HomeFragment : Fragment(), TaskListener {

    private lateinit var navigationItems: List<BottomNavItem>
    private lateinit var viewModel: HomeViewModel
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var dateListState: LazyListState
    private lateinit var todoListState: LazyListState
    private lateinit var scope: CoroutineScope
    private lateinit var appStoreRepository: AppStoreRepository
    private lateinit var softwareUpdateManager: SoftwareUpdateManager
    private val settingIcons: List<SettingItem> = initSettingIcons()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBottomNavItems()
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
        val factory = HomeViewModelFactory(
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
        onNotificationAvailable()
        val settingFactory = SettingViewModelFactory(appStoreRepository)
        settingViewModel = ViewModelProvider(this, settingFactory)[SettingViewModel::class.java]
        softwareUpdateManager = AppServiceLocator.provideSoftwareUpdateManager(requireContext())
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.onEvent(HomeEvents.OnProgressBarToggle(true))
            launch {
                viewModel.loadInitialDates()
            }
            launch {
                settingViewModel.initSettingPreferences()
            }
            launch {
                viewModel.fetchAllTodo()
            }
            launch {
                viewModel.fetchAllCategory()
            }
            launch {
                viewModel.fetchAllNotes()
            }
            launch {
                viewModel.fetchAllDecks()
            }

            initialSlide()
            viewModel.onEvent(HomeEvents.OnViewChange(settingViewModel.defaultScreen.value))
        }
        checkNotificationView()
    }

    private fun initBottomNavItems() {
        navigationItems = listOf<BottomNavItem>(
            BottomNavItem("Tasks", R.drawable.ic_task_view),
            BottomNavItem("Notes", R.drawable.ic_notes_view),
            BottomNavItem("Decks", R.drawable.ic_deck_view),
            BottomNavItem("Settings", R.drawable.ic_settings)
        )
    }

    private fun initSettingIcons(): List<SettingItem> {
        return listOf<SettingItem>(
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
            onUserNotificationEvent(id, title)
        }
    }




    private fun checkNotificationView() {
        CoroutineScope(Dispatchers.IO).launch {
            val todoId = async {  appStoreRepository.getTodoId() }.await()
            Log.i("NOTIFICATION_FLOW", "HomeFragment :: checkNotificationView: fetched todoId : ${todoId}")
            if (todoId != -1) {
                Log.i("NOTIFICATION_FLOW", "HomeFragment :: checkNotificationView: navigated to todoId : ${todoId}")
                navigateToTaskViewById(todoId)
                Log.i("NOTIFICATION_FLOW", "HomeFragment :: checkNotificationView: update todoId : -1")
                appStoreRepository.updateTodoId(-1)
            }
        }
    }

    private fun byPassNotificationView(todoId : Int) {
        if (todoId != -1) {
            Log.i("NOTIFICATION_FLOW", "HomeFragment :: checkNotificationView: navigated to todoId : ${todoId}")
            navigateToTaskViewById(todoId)
            Log.i("NOTIFICATION_FLOW", "HomeFragment :: checkNotificationView: update todoId : -1")
        }
    }

    private suspend fun initialSlide() {
        if (::scope.isInitialized) {
            delay(200)
            viewModel.onEvent(HomeEvents.OnRefreshInitialDates)
            delay(200)
            val index = viewModel.dateList.value.indexOf(viewModel.currentDate.value)
            Log.i("SLIDE_DATA", "initialSlide: List ${viewModel.dateList.value}")
            Log.i("SLIDE_DATA", "initialSlide: Current Date ${viewModel.currentDate.value}")
            Log.i("SLIDE_DATA", "initialSlide: List Size ${viewModel.dateList.value.size} :: Index ${index}")
            viewModel.onEvent(HomeEvents.OnIndexSelected(index))
            val job: Job = scope.launch(Dispatchers.Default) {
                scope.launch {
                    scrollToRollItem(viewModel.todoIndex.value, todoListState)
                }
            }
            job.join()
            delay(100)
            scope.launch { scrollToRollItem(index, dateListState) }
            viewModel.onEvent(HomeEvents.OnProgressBarToggle(false))
        }
    }

    override fun onUserNotificationEvent(id: Int, title: String) {
        Log.i("NOTIFICATION_FLOW", "HomeFragment :: onUserNotificationEvent: task : ${id}")
        if (title.equals("Software Update") && id == 10001){
            softwareUpdateManager.installUpdate()
        }else{
            byPassNotificationView(id)
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
                dateListState = rememberLazyListState()
                scope = rememberCoroutineScope()
                val bottomSheetState = rememberModalBottomSheetState(
                    initialValue = ModalBottomSheetValue.Hidden
                )
                var sheetLayoutVisible = viewModel.sheetVisibility.value
                val currentDateIndex = viewModel.currentDateIndex.value
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
                    if (searchText.isNotEmpty()) viewModel.searchList.value as List<Note> else viewModel.noteList
                val deckList =
                    if (searchText.isNotEmpty()) viewModel.searchList.value as List<Deck> else viewModel.deckList.value
                val colorMap = viewModel.colorMap.value
                val currentMonth = viewModel.currentMonth.value
                val currentYear = viewModel.currentYear.value
                val currentDate = viewModel.currentDate.value
                val todayDateIndex = dateList.indexOf(currentDate)
                Log.i("DateCheck", "onCreateView: ${todayDateIndex}")
                val selectedDateTodoIndex = viewModel.todoIndex.value
                val selectedCategoryIndex = viewModel.selectedCategoryIndex.value
                val progressBar = viewModel.progressBarVisibility.value
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
                val isDarkMode = SettingViewModel.Companion.darkModeStatus.value
                Box(
                    modifier = Modifier.Companion.fillMaxSize(),
                ) {
                    Box(
                        modifier = Modifier.Companion
                            .fillMaxSize()
                            .alpha(if (viewModel.progressBarVisibility.value) 0.7F else 1F)
                            .blur(radius = if (viewModel.progressBarVisibility.value) 7.dp else 0.dp)
                    ) {
                        Box(
                            modifier = Modifier.Companion
                                .fillMaxSize(),
                            contentAlignment = Alignment.Companion.BottomEnd
                        ) {
                            Column(
                                modifier = Modifier.Companion
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.Companion.verticalGradient(
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
                            ) {
                                AnimatedVisibility(visible = viewType != ViewType.SETTING) {
                                    HeaderComponent(
                                        headerText = "All Tasks",
                                        onEventClick = {
                                            scope.launch(Dispatchers.Main) {
                                                viewModel.onEvent(
                                                    HomeEvents.OnIndexSelected(
                                                        todayDateIndex
                                                    )
                                                )
                                                dateListState.scrollToItem(todayDateIndex)
                                                viewModel.onEvent(
                                                    HomeEvents.HighlightTodoByDate(
                                                        todayDateIndex
                                                    )
                                                )
                                                todoListState.scrollToItem(viewModel.todoIndex.value)
                                            }
                                        },
                                        currentDate = Date(System.currentTimeMillis()),
                                        viewType = viewType,
                                        searchIconVisibility = viewType != ViewType.TASK && !searchToggle,
                                        onSearchIconClick = {
                                            viewModel.onEvent(HomeEvents.OnSearchToggle(true))
                                        },
                                        onViewChange = {}
                                    )
                                }
                                AnimatedVisibility(visible = viewType != ViewType.SETTING) {
                                    Spacer(modifier = Modifier.Companion.height(if (searchToggle) 20.dp else 0.dp))
                                }
                                AnimatedVisibility(visible = searchToggle) {
                                    SearchBarComponent(
                                        isDarkMode = isDarkMode,
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
                                Spacer(modifier = Modifier.Companion.height(if (viewType == ViewType.TASK) 20.dp else 0.dp))
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
                                    Spacer(modifier = Modifier.Companion.height(20.dp))
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
                                    Spacer(modifier = Modifier.Companion.height(20.dp))
                                }
                                AnimatedVisibility(visible = viewType == ViewType.TASK) {
                                    TodoItemList(
                                        listState = todoListState,
                                        todoList = todoList,
                                        colorMap = colorMap,
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
                                    NoteItemList(
                                        listState = noteListState,
                                        noteList = noteList,
                                        colorMap = colorMap,
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
                                        },
                                        onNoteLongClickEvent = {
                                        }
                                    )
                                }
                                AnimatedVisibility(visible = viewType == ViewType.DECK) {
                                    DeckItemList(
                                        listState = deckListState,
                                        deckList = deckList,
                                        colorMap = colorMap,
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
                                AnimatedVisibility(visible = viewType == ViewType.SETTING) {
                                    Settings(
                                        iconList = settingIcons,
                                        pickerState = pickerState,
                                        loaderState = loaderState,
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
                                            settingViewModel.onEvent(
                                                SettingEvents.OnBackupSettingViewChange(
                                                    AppBackup.IMPORT,
                                                    requireContext()
                                                )
                                            )
                                        },
                                        onExportEvent = {
                                            settingViewModel.onEvent(
                                                SettingEvents.OnBackupSettingViewChange(
                                                    AppBackup.EXPORT,
                                                    requireContext()
                                                )
                                            )
                                        },
                                        onHelpEvent = {},
                                        onFeedbackEvent = {},
                                        onSoftwareUpdateEvent = {
                                            softwareUpdateManager.checkForUpdate()
                                            Toast.makeText(requireContext(), "Checking Software Updates", Toast.LENGTH_SHORT).show()
                                        },
                                        onGeneralSettingItemClick = {
                                            settingViewModel.onEvent(
                                                SettingEvents.OnPickerToggle(
                                                    true
                                                )
                                            )
                                        },
                                        onBackupSettingItemClick = {
                                            settingViewModel.onEvent(
                                                SettingEvents.OnLoaderToggle(
                                                    true
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                            AnimatedVisibility(
                                visible = viewType != ViewType.SETTING,
                                enter = scaleIn(),
                                exit = scaleOut()
                            ) {
                                FloatingActionButton(
                                    modifier = Modifier.Companion
                                        .padding(bottom = 65.dp, end = 20.dp)
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
                            modifier = Modifier.Companion.fillMaxSize(),
                            contentAlignment = Alignment.Companion.BottomEnd
                        ) {
                            BottomNavigationBar(
                                items = navigationItems,
                                selectedScreen = viewType
                            ) {
                                viewModel.onEvent(HomeEvents.OnViewChange(it))
                                viewModel.onEvent(HomeEvents.OnClearSearch)
                                viewModel.onEvent(HomeEvents.OnSearchToggle(false))
                                // impossible index
                                viewModel.onEvent(HomeEvents.OnCategorySortSelected(-1))
                            }
                        }
                        if (viewModel.progressBarVisibility.value) {
                            ProgressBar()
                        }
                        if (viewType == ViewType.SETTING) {
                            Box(
                                modifier = Modifier.Companion
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Companion.Center,
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
                    }
                }
            }
        }
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
        viewModel.archiveTodos(viewModel.todoList.value)
    }

    private fun navigateToTaskViewById(todoId: Int) {
        val bundle = bundleOf()
        bundle.putBoolean(Constants.TASK_VIEW, true)
        bundle.putInt(Constants.TASK_VIEW_ID, todoId)
        Log.i("NOTIFICATION_FLOW", "HomeFragment :: navigateToTaskViewById: navigated to todoId : ${todoId}")
        findNavController().navigate(
            R.id.taskFragment,
            bundle,
            NavOptions.navOptionStack
        )
    }

    private suspend fun scrollToRollItem(currentDateIndex: Int, listState: LazyListState) {
        if (currentDateIndex != -1){
            listState.animateScrollToItem(currentDateIndex)
        }
    }

    private fun navigateByView(viewType : ViewType){
        val bundle = bundleOf()
        when(viewType){
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

}