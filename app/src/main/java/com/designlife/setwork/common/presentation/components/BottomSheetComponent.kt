package com.designlife.setwork.common.presentation.components

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.designlife.setwork.R
import com.google.android.material.bottomsheet.BottomSheetDialog

object BottomSheet {
    @SuppressLint("MissingInflatedId")
    fun dialog(
        context: Activity,
        isDarkMode : Boolean,
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
        val bottomSheetLayout = view.findViewById<CardView>(R.id.bottom_sheet_layout)
        val noteView = view.findViewById<TextView>(R.id.note_view)
        val deckView = view.findViewById<TextView>(R.id.deck_view)
        val taskView = view.findViewById<TextView>(R.id.task_view)
        bottomSheetLayout.background = ColorDrawable(android.graphics.Color.parseColor(if (isDarkMode) "#161616" else "#FFFFFF"))
        noteView.setTextColor(android.graphics.Color.parseColor(if (isDarkMode) "#B1B1B1" else "#000000"))
        deckView.setTextColor(android.graphics.Color.parseColor(if (isDarkMode) "#B1B1B1" else "#000000"))
        taskView.setTextColor(android.graphics.Color.parseColor(if (isDarkMode) "#B1B1B1" else "#000000"))

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