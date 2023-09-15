package com.designlife.justdo.common.presentation.components

import android.app.Activity
import android.widget.Button
import android.widget.LinearLayout
import com.designlife.justdo.R
import com.google.android.material.bottomsheet.BottomSheetDialog

object BottomSheet {
    fun dialog(
        context: Activity,
        onCloseEvent: () -> Unit,
        onNoteEvent: () -> Unit,
        onTaskEvent: () -> Unit,
        onDeckEvent: () -> Unit,
    ): BottomSheetDialog {
        val dialog = BottomSheetDialog(context, R.style.BottomSheet)
        val view = context.layoutInflater.inflate(R.layout.app_bottom_sheet, null)
        val closeEvent = view.findViewById<Button>(R.id.sheet_close_btn)
        val noteEvent = view.findViewById<LinearLayout>(R.id.note_event)
        val taskEvent = view.findViewById<LinearLayout>(R.id.task_event)
        val deckEvent = view.findViewById<LinearLayout>(R.id.deck_event)

        closeEvent.setOnClickListener {
            dialog.dismiss()
            onCloseEvent()
        }
        noteEvent.setOnClickListener {
            onNoteEvent()
            onCloseEvent()
            dialog.dismiss()
        }
        taskEvent.setOnClickListener {
            onTaskEvent()
            onCloseEvent()
            dialog.dismiss()
        }
        deckEvent.setOnClickListener {
            onDeckEvent()
            onCloseEvent()
            dialog.dismiss()
        }
        dialog.setOnCancelListener {
            onCloseEvent()
            dialog.dismiss()
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        return dialog
    }
}