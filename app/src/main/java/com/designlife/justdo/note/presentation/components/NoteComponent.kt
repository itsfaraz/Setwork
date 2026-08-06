package com.designlife.justdo.note.presentation.components

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.InputType
import android.text.method.ArrowKeyMovementMethod
import android.util.TypedValue
import android.view.ActionMode
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatEditText
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.designlife.justdo.note.ListAutoFormatWatcher
import com.designlife.justdo.ui.theme.ButtonHighLightPrimary
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.TypographyColor
import com.designlife.justdo.ui.theme.UIComponentBackground
import com.designlife.justdo.ui.theme.noteContentStyleSize
import com.designlife.justdo.ui.theme.noteTitleStyle
import com.designlife.justdo.ui.theme.noteTitleStyleSize

@Composable
fun NoteComponent(
    title: String,
    onTitleUpdate: (String) -> Unit,
    noteText: String,
    onNoteUpdate: (String) -> Unit,
) {
    val lineColor     = ButtonHighLightPrimary.value.copy(alpha = 0.4f)
    val textColor     = TypographyColor.value
    val cursorColor   = ButtonPrimary.value
    val bgColor       = UIComponentBackground.value
    val contentSizeSp = noteContentStyleSize.value

    var wordCount by remember { mutableIntStateOf(0) }
    var charCount by remember { mutableIntStateOf(0) }
    var canUndo   by remember { mutableStateOf(false) }
    var canRedo   by remember { mutableStateOf(false) }
    var editorRef by remember { mutableStateOf<LinedNoteEditText?>(null) }

    // Stable argb values — computed once, never inside AndroidView update
    val textArgb   = remember(textColor)     { textColor.toArgb() }
    val cursorArgb = remember(cursorColor)   { cursorColor.toArgb() }
    val lineArgb   = remember(lineColor)     { lineColor.toArgb() }
    val spSz       = remember(contentSizeSp) { contentSizeSp.value }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
    ) {
        HorizontalDivider(color = ButtonHighLightPrimary.value, thickness = 0.5.dp)

        TitleField(
            title         = title,
            onTitleUpdate = onTitleUpdate,
            textColor     = textColor,
            cursorColor   = cursorColor,
        )

        HorizontalDivider(color = ButtonHighLightPrimary.value, thickness = 0.5.dp)

        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory  = { ctx ->
                    LinedNoteEditText(
                        context            = ctx,
                        lineColorArgb      = lineArgb,
                        onStatsChanged     = { wc, cc -> wordCount = wc; charCount = cc },
                        onUndoStateChanged = { u, r -> canUndo = u; canRedo = r },
                    ).also { view ->
                        view.layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        view.background = null
                        view.setTextColor(textArgb)
                        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, spSz)
                        view.setPadding(
                            16.dpPx(ctx), 14.dpPx(ctx),
                            16.dpPx(ctx), 32.dpPx(ctx),
                        )
                        view.setCursorColor(cursorArgb)
                        view.inputType = InputType.TYPE_CLASS_TEXT or
                                InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                        view.gravity = Gravity.TOP or Gravity.START
                        view.setHorizontallyScrolling(false)
                        view.maxLines = Int.MAX_VALUE

                        view.movementMethod = ArrowKeyMovementMethod.getInstance()
                        view.isNestedScrollingEnabled = false
                        view.overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
                        view.isVerticalScrollBarEnabled = false
                        view.setLineSpacing(0f, 1.5f)
                        view.isLongClickable = true
                        view.isFocusableInTouchMode = true

                        view.customSelectionActionModeCallback = NoteSelectionCallback(view)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            view.customInsertionActionModeCallback = NoteInsertionCallback(view)
                        }
                        view.setText(noteText)
                        if (noteText.isNotEmpty()) view.setSelection(noteText.length)

                        view.addTextChangedListener(
                            ListAutoFormatWatcher { updated -> onNoteUpdate(updated) }
                        )

                        editorRef = view
                    }
                },
                update = { view ->
                    val displayed = view.text?.toString() ?: ""
                    val externallyChanged = displayed.length != noteText.length ||
                            displayed != noteText
                    if (externallyChanged && !view.isFocused) {
                        val cursor = view.selectionStart.coerceIn(0, noteText.length)
                        view.setText(noteText)
                        view.setSelection(cursor)
                    }
                    if (view.currentTextColor != textArgb) {
                        view.setTextColor(textArgb)
                    }
                },
            )
        }

        NoteToolbar(
            wordCount    = wordCount,
            charCount    = charCount,
            canUndo      = canUndo,
            canRedo      = canRedo,
            accentColor  = cursorColor,
            dividerColor = ButtonHighLightPrimary.value,
            onUndo       = { editorRef?.performUndo() },
            onRedo       = { editorRef?.performRedo() },
            onBullet     = { editorRef?.insertAtLineStart("• ") },
            onHeading    = { editorRef?.insertAtLineStart("# ") },
        )
    }
}

@Composable
private fun TitleField(
    title: String,
    onTitleUpdate: (String) -> Unit,
    textColor: Color,
    cursorColor: Color,
) {
    BasicTextField(
        value         = title,
        onValueChange = onTitleUpdate,
        singleLine    = true,
        textStyle     = noteTitleStyle.value.copy(
            color    = textColor,
            fontSize = noteTitleStyleSize.value,
        ),
        cursorBrush = SolidColor(cursorColor),
        modifier    = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        decorationBox = { inner ->
            if (title.isEmpty()) {
                Text(
                    text     = "Untitled",
                    color    = textColor.copy(alpha = 0.3f),
                    fontSize = noteTitleStyleSize.value,
                )
            }
            inner()
        },
    )
}


@Composable
private fun NoteToolbar(
    wordCount   : Int,
    charCount   : Int,
    canUndo     : Boolean,
    canRedo     : Boolean,
    accentColor : Color,
    dividerColor: Color,
    onUndo      : () -> Unit,
    onRedo      : () -> Unit,
    onBullet    : () -> Unit,
    onHeading   : () -> Unit,
) {
    val dim    = accentColor.copy(alpha = 0.30f)
    val active = accentColor.copy(alpha = 0.80f)

    HorizontalDivider(color = dividerColor, thickness = 0.5.dp)

    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .imePadding()
            .height(40.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        ToolbarBtn(label = "↩", enabled = canUndo,  active = active, dim = dim, onClick = onUndo)
        ToolbarBtn(label = "↪", enabled = canRedo,  active = active, dim = dim, onClick = onRedo)

        Box(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .width(0.5.dp)
                .height(16.dp)
                .background(dividerColor.copy(alpha = 0.4f))
        )

        ToolbarBtn(label = "•", enabled = true, active = active, dim = dim, onClick = onBullet)
        ToolbarBtn(label = "H", enabled = true, active = active, dim = dim, onClick = onHeading)

        Spacer(Modifier.weight(1f))

        Text(
            text          = "$wordCount w  $charCount c",
            color         = accentColor.copy(alpha = 0.22f),
            fontSize      = 10.sp,
            letterSpacing = 0.5.sp,
        )
    }
}

@Composable
private fun ToolbarBtn(
    label  : String,
    enabled: Boolean,
    active : Color,
    dim    : Color,
    onClick: () -> Unit,
) {
    Text(
        text     = label,
        color    = if (enabled) active else dim,
        fontSize = 15.sp,
        modifier = Modifier
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// LinedNoteEditText
// ─────────────────────────────────────────────────────────────────────────────

class LinedNoteEditText(
    context: Context,
    lineColorArgb: Int,
    private val onStatsChanged     : (Int, Int) -> Unit = { _, _ -> },
    private val onUndoStateChanged : (Boolean, Boolean) -> Unit = { _, _ -> },
) : AppCompatEditText(context) {

    private val linePaint = Paint().apply {
        isAntiAlias = false
        color       = lineColorArgb
        strokeWidth = resources.displayMetrics.density
        style       = Paint.Style.STROKE
    }

    private var lineHeightPx  = 0f
    private var paddingTopPx  = 0f
    private var paddingLeftF  = 0f
    private var paddingRightF = 0f

    private val undoStack  = ArrayDeque<String>(52)
    private val redoStack  = ArrayDeque<String>(52)
    private var lastSaved  = ""
    private var ignoreChange = false

    init {
        addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {
                if (ignoreChange || s == null) return
                val snap = s.toString()
                if (snap != lastSaved) {
                    if (undoStack.size >= 50) undoStack.removeFirst()
                    undoStack.addLast(snap)
                    redoStack.clear()
                    lastSaved = snap
                    onUndoStateChanged(true, false)
                }
            }

            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}

            override fun afterTextChanged(e: android.text.Editable?) {
                if (e == null) return
                val txt = e.toString()
                val wc  = if (txt.isBlank()) 0
                else txt.trim().split(Regex("\\s+")).size
                onStatsChanged(wc, txt.length)
            }
        })
    }

    fun performUndo() {
        if (undoStack.isEmpty()) return
        val cur = text?.toString() ?: ""
        if (redoStack.size >= 50) redoStack.removeFirst()
        redoStack.addLast(cur)
        ignoreChange = true
        val prev = undoStack.removeLast()
        setText(prev)
        setSelection(prev.length)
        ignoreChange = false
        lastSaved = prev
        onUndoStateChanged(undoStack.isNotEmpty(), true)
    }

    fun performRedo() {
        if (redoStack.isEmpty()) return
        val cur = text?.toString() ?: ""
        if (undoStack.size >= 50) undoStack.removeFirst()
        undoStack.addLast(cur)
        ignoreChange = true
        val next = redoStack.removeLast()
        setText(next)
        setSelection(next.length)
        ignoreChange = false
        lastSaved = next
        onUndoStateChanged(true, redoStack.isNotEmpty())
    }

    fun insertAtLineStart(prefix: String) {
        val e      = text ?: return
        val cursor = selectionStart.coerceAtLeast(0)
        val ls     = e.lastIndexOf('\n', cursor - 1) + 1
        val end    = minOf(ls + prefix.length, e.length)
        if (end > ls && e.substring(ls, end) == prefix) {
            e.delete(ls, end)      // toggle off
        } else {
            e.insert(ls, prefix)   // toggle on
        }
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val l = layout ?: return
        if (l.lineCount > 0) {
            lineHeightPx  = (l.getLineBottom(0) - l.getLineTop(0)).toFloat()
        }
        paddingTopPx  = totalPaddingTop.toFloat()
        paddingLeftF  = paddingLeft.toFloat()
        paddingRightF = (width - paddingRight).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        val l = layout
        if (l != null && lineHeightPx > 0f) {
            drawRuledLines(canvas, l)
        }
        super.onDraw(canvas)
    }

    private fun drawRuledLines(canvas: Canvas, l: android.text.Layout) {
        val lineCount = l.lineCount
        if (lineCount == 0 || lineHeightPx == 0f) return

        val left      = paddingLeftF
        val right     = paddingRightF
        val topOff    = paddingTopPx
        val sy        = scrollY
        val viewBot   = sy + height

        val first = l.getLineForVertical(sy).coerceIn(0, lineCount - 1)
        val last  = l.getLineForVertical(viewBot).coerceIn(0, lineCount - 1)

        for (i in first..last) {
            val y = l.getLineBaseline(i).toFloat() + topOff + 1f
            canvas.drawLine(left, y, right, y, linePaint)
        }

        if (last >= lineCount - 1) {
            val lastY = l.getLineBaseline(lineCount - 1).toFloat() + topOff + 1f
            var y = lastY + lineHeightPx
            while (y < viewBot) {
                canvas.drawLine(left, y, right, y, linePaint)
                y += lineHeightPx
            }
        }
    }
}


private class NoteSelectionCallback(
    private val ed: AppCompatEditText,
) : ActionMode.Callback {

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        menu.add(Menu.NONE, ID_BOLD,   100, "Bold")
        menu.add(Menu.NONE, ID_ITALIC, 101, "Italic")
        menu.add(Menu.NONE, ID_UPPER,  102, "UPPER")
        menu.add(Menu.NONE, ID_LOWER,  103, "lower")
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        val s = ed.selectionStart; val e = ed.selectionEnd
        if (s < 0 || e < 0 || s >= e) return false
        val txt = ed.text ?: return false
        val sel = txt.subSequence(s, e).toString()
        return when (item.itemId) {
            ID_BOLD   -> { txt.replace(s, e, "**$sel**");      mode.finish(); true }
            ID_ITALIC -> { txt.replace(s, e, "_${sel}_");      mode.finish(); true }
            ID_UPPER  -> { txt.replace(s, e, sel.uppercase()); mode.finish(); true }
            ID_LOWER  -> { txt.replace(s, e, sel.lowercase()); mode.finish(); true }
            else      -> false
        }
    }

    override fun onDestroyActionMode(mode: ActionMode) = Unit

    companion object {
        private const val ID_BOLD = 1001; private const val ID_ITALIC = 1002
        private const val ID_UPPER = 1003; private const val ID_LOWER = 1004
    }
}

private class NoteInsertionCallback(
    private val ed: AppCompatEditText,
) : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        val cb = ed.context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        return cb?.hasPrimaryClip() == true
    }
    override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false
    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        if (item.itemId == android.R.id.paste) {
            ed.onTextContextMenuItem(android.R.id.paste)
            mode.finish()
            return true
        }
        return false
    }
    override fun onDestroyActionMode(mode: ActionMode) = Unit
}

@SuppressLint("DiscouragedPrivateApi")
fun EditText.setCursorColor(@ColorInt color: Int) {
    try {
        val drawable = GradientDrawable().apply {
            setColor(color)
            setSize(2, this@setCursorColor.lineHeight)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            textCursorDrawable = drawable
        } else {
            val editorField = TextView::class.java.getDeclaredField("mEditor")
            editorField.isAccessible = true
            val editor = editorField.get(this)
            val f = editor.javaClass.getDeclaredField("mCursorDrawable")
            f.isAccessible = true
            f.set(editor, arrayOf(drawable, drawable))
        }
    } catch (_: Exception) {}
}

private fun Int.dpPx(ctx: Context): Int =
    (this * ctx.resources.displayMetrics.density + 0.5f).toInt()