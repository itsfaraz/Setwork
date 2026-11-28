package com.designlife.setwork.note

import android.text.Editable
import android.text.Selection
import android.text.TextWatcher

class ListAutoFormatWatcher(
    private val onUpdate: (String) -> Unit
) : TextWatcher {
    private var isFormatting = false
    private var beforeText = ""

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (isFormatting) return
        beforeText = s?.toString() ?: ""
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isFormatting || s == null) return
        val text = s.toString()
        val cursor = Selection.getSelectionEnd(s)
        val bulletTriggerIdx = text.lastIndexOf("* ", cursor)
        if (bulletTriggerIdx >= 0 && cursor == bulletTriggerIdx + 2) {
            val lineStart = text.lastIndexOf('\n', bulletTriggerIdx - 1) + 1
            if (bulletTriggerIdx == lineStart) {
                isFormatting = true
                s.replace(bulletTriggerIdx, bulletTriggerIdx + 2, "• ")
                Selection.setSelection(s, bulletTriggerIdx + 2)
                onUpdate(s.toString())
                isFormatting = false
                return
            }
        }
        val enterPressed = text.length == beforeText.length + 1 &&
                cursor > 0 && text[cursor - 1] == '\n'
        if (!enterPressed) {
            onUpdate(text)
            return
        }
        val newlinePos = cursor - 1
        val prevLineStart = beforeText.lastIndexOf('\n') + 1
        val prevLine = beforeText.substring(prevLineStart)
        when {
            prevLine == "• " -> {
                isFormatting = true
                s.delete(prevLineStart, newlinePos + 1)
                Selection.setSelection(s, prevLineStart)
                isFormatting = false
            }
            prevLine.startsWith("• ") -> {
                isFormatting = true
                s.insert(cursor, "• ")
                Selection.setSelection(s, cursor + 2)
                isFormatting = false
            }
            prevLine.matches(Regex("""^\d+\. .*""")) -> {
                val num = prevLine.takeWhile { it.isDigit() }.toIntOrNull() ?: run {
                    onUpdate(s.toString()); return
                }
                val body = prevLine.removePrefix("$num. ")
                isFormatting = true
                if (body.isEmpty()) {
                    s.delete(prevLineStart, newlinePos + 1)
                    Selection.setSelection(s, prevLineStart)
                } else {
                    val next = "${num + 1}. "
                    s.insert(cursor, next)
                    Selection.setSelection(s, cursor + next.length)
                }
                isFormatting = false
            }
            prevLine.matches(Regex("""^[a-z]\. .*""")) -> {
                val ch = prevLine[0]
                val body = prevLine.substring(3)
                isFormatting = true
                if (body.isEmpty()) {
                    s.delete(prevLineStart, newlinePos + 1)
                    Selection.setSelection(s, prevLineStart)
                } else if (ch < 'z') {
                    val next = "${ch + 1}. "
                    s.insert(cursor, next)
                    Selection.setSelection(s, cursor + next.length)
                }
                isFormatting = false
            }
        }
        onUpdate(s.toString())
    }
}