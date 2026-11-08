package com.designlife.justdo.note

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.designlife.justdo.MainActivity
import com.designlife.justdo.R
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.repeat.RepeatRepository
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.NoteRepository
import com.designlife.justdo.common.domain.repositories.TodoCategoryRepository
import com.designlife.justdo.common.domain.repositories.TodoRepository
import com.designlife.justdo.common.presentation.components.CommonCustomHeader
import com.designlife.justdo.common.presentation.components.CustomAttachmentsTab
import com.designlife.justdo.common.presentation.components.ProgressBar
import com.designlife.justdo.common.presentation.components.ToolBarPopUpComponent
import com.designlife.justdo.common.presentation.components.TopPaddingComponent
//import com.designlife.justdo.common.utils.AppServiceLocator
import com.designlife.justdo.common.utils.constants.Constants
import com.designlife.justdo.common.utils.enums.ScreenType
import com.designlife.justdo.common.utils.enums.ViewType
import com.designlife.justdo.note.presentation.components.NoteComponent
import com.designlife.justdo.note.presentation.components.NoteReminderComponent
import com.designlife.justdo.note.presentation.viewmodel.NoteViewModel
import com.designlife.justdo.note.presentation.viewmodel.NoteViewModelFactory
import com.designlife.justdo.setworkllm.SetworkOLLM
import com.designlife.justdo.ui.theme.UIComponentBackground
import com.designlife.justdo_provider.SetworkProvider
import com.designlife.orchestrator.SchedulingEngine
import org.koin.android.ext.android.inject
import java.util.Calendar
import java.util.Date

class NoteFragment : Fragment(), SetworkOLLM.SetworkMessage {

    private lateinit var viewModel: NoteViewModel

    private val noteRepository : NoteRepository by inject()
    private val categoryRepository : CategoryRepository by inject()
    private val schedulingEngine : SchedulingEngine by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.ollmSDK?.protocol(this)
        val factory = NoteViewModelFactory(
            noteRepository,
            categoryRepository,
            schedulingEngine.notificationScheduler()
        )

        viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]


        val noteView = arguments?.getBoolean(Constants.NOTE_VIEW) ?: false
        val noteId = arguments?.getInt(Constants.NOTE_VIEW_ID) ?: -1

        if (noteView && noteId != -1) {
            viewModel.initExisting(noteId.toLong())
        }else{
            viewModel.init(
                arguments?.getLong("noteId"),
                arguments?.getInt("categoryIndex")
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val state = viewModel.state.value
                val calendar = Calendar.getInstance()
                Box(Modifier
                    .fillMaxSize()
                    .background(color = UIComponentBackground.value)) {
                    Column {
                        TopPaddingComponent()
                        CommonCustomHeader(
                            headerTitle = state.title.ifEmpty { "New Note" },
                            onCloseEvent = {
                                viewModel.saveNow()
                                findNavController().navigateUp()
                            },
                            onAutoSaveEvent = {
                                viewModel.saveNow()
                            },
                            onReminderEvent = {
                                viewModel.toggleReminder()
                            },
                            onAIChatEvent = {
                                viewModel.toggleAI()
                            },
                            onThreeDotEvent = {
                                viewModel.toggleToolbar()
                            },
                            hasDone = false,
                            forTask = false,
                            isOverview = true,
                            viewType = ViewType.NOTE,
                            onButtonClickEvent = {}
                        )
                        AnimatedVisibility(state.reminderEnabled) {
                            NoteReminderComponent(
                                dateText = IDateGenerator.getGracefullyDateFromDate(Date()),
                                timeText = IDateGenerator.getGracefullyTimeFromEpoch(System.currentTimeMillis()),
                                onDateChange = {
                                    DatePickerDialog(
                                        context,
                                        R.style.CustomDatePickerTheme,
                                        { _, y, m, d ->
                                            viewModel.onDateChange(d, m + 1, y)
                                        },
                                        calendar[Calendar.YEAR],
                                        calendar[Calendar.MONTH],
                                        calendar[Calendar.DAY_OF_MONTH]
                                    ).show()
                                },
                                onTimeChange = {
                                    TimePickerDialog(
                                        context,
                                        R.style.CustomTimePickerTheme,
                                        { _, h, m ->
                                            viewModel.onTimeChange(h, m)
                                        },
                                        calendar[Calendar.HOUR_OF_DAY],
                                        calendar[Calendar.MINUTE],
                                        false
                                    ).show()
                                }
                            )
                        }
                        AnimatedVisibility(state.aiEnabled) {
                            SetworkOLLM.ChatTextView()
                        }
                        CustomAttachmentsTab(
                            hasCover = true,
                            onGalleryEvent = { viewModel.onCoverChange(it) },
                            categoryList = state.categories,
                            selectedCategoryIndex = state.selectedCategoryIndex,
                            onCategoryEvent = viewModel::onCategoryChange,
                            addCategoryEvent = {
                                val bundle = bundleOf()
                                bundle.putInt(Constants.SCREEN_TYPE, ScreenType.CATEGORY.ordinal)
                                findNavController().navigate(
                                    R.id.containerFragment,
                                    bundle
                                )
                            }
                        )
                        NoteComponent(
                            title = state.title,
                            noteText = state.content,
                            onTitleUpdate = viewModel::onTitleChange,
                            onNoteUpdate = viewModel::onContentChange
                        )
                    }

                    if (state.isLoading) {
                        ProgressBar()
                    }

                    AnimatedVisibility(
                        state.toolbarVisible,
                        enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                        exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
                    ) {
                        ToolBarPopUpComponent(
                            onCloseEvent = { viewModel.toggleToolbar() },
                            onCopyEvent = {
                                viewModel.toggleToolbar()
                                copy(state.content)
                            },
                            onDuplicateEvent = {
                                viewModel.toggleToolbar()
                                viewModel.duplicateNote()
                            },
                            onExportPdfEvent = {
                                viewModel.toggleToolbar()
                                viewModel.exportPdf(requireContext())
                            },
                            onExportPngEvent = {
                                viewModel.toggleToolbar()
                                viewModel.exportPng(requireContext())
                            },
                            onDeleteEvent = {
                                viewModel.deleteNote()
                                findNavController().navigateUp()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.saveNow()
            findNavController().navigateUp()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveNow()
    }

    private fun copy(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("note", text))
    }

    override fun onChatRelay(message: String) {
        Log.i("AI_FLOW", "onChatRelay: REPONSE FROM AI ::: ${message}")
        val updated = viewModel.state.value.content + "\n\n$message\n\n\n"
        viewModel.onContentChange(updated)
        viewModel.saveNow()
    }
}