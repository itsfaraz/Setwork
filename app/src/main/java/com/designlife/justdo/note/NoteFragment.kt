package com.designlife.justdo.note

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.designlife.justdo.common.domain.repositories.NoteRepository
import com.designlife.justdo.common.presentation.components.CommonCustomHeader
import com.designlife.justdo.common.utils.AppServiceLocator
import com.designlife.justdo.note.presentation.components.NoteComponent
import com.designlife.justdo.note.presentation.events.NoteEvents
import com.designlife.justdo.note.presentation.viewmodel.NoteViewModel
import com.designlife.justdo.note.presentation.viewmodel.NoteViewModelFactory
import com.designlife.justdo.ui.theme.PrimaryBackgroundColor

class NoteFragment : Fragment() {
    private lateinit var viewModel: NoteViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    // save updates
                    viewModel.insertNote()
                    Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val noteRepository = AppServiceLocator.provideNoteRepository(requireContext())
        val factory = NoteViewModelFactory(noteRepository)
        viewModel =  ViewModelProvider(this,factory)[NoteViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply{
            setContent {
                val noteTitle = viewModel.titleValue.value
                val noteContent = viewModel.contentValue.value
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = PrimaryBackgroundColor),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CommonCustomHeader(
                        headerTitle = if(noteTitle.isEmpty()) "New Note" else noteTitle,
                        onCloseEvent = {
                            // save updates
                            viewModel.insertNote()
                            Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        }
                    ) { /* do something */ }
                    NoteComponent(
                        title = noteTitle,
                        onTitleUpdate = {
                            viewModel.onEvent(NoteEvents.OnTitleChange(it))
                        },
                        noteText = noteContent,
                        onNoteUpdate = {
                            viewModel.onEvent(NoteEvents.OnContentChange(it))
                        }
                    )
                }
            }
        }
    }

}