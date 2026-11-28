package com.designlife.setwork.common.domain.entities

import java.util.Date

data class Note(
    val noteId : Long = 0L,
    val title : String = "",
    val content : String = "",
    val emoji : String = "📓",
    val categoryId : Long = 0L,
    val coverImage : ByteArray? = null,
    val isArchive : Boolean = false,
    val createdTime: Date = Date(System.currentTimeMillis()),
    val lastModified : Date = Date(System.currentTimeMillis()),
) : SearchMarker(referenceId = noteId) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Note

        if (coverImage != null) {
            if (other.coverImage == null) return false
            if (!coverImage.contentEquals(other.coverImage)) return false
        } else if (other.coverImage != null) return false

        return true
    }

    override fun hashCode(): Int {
        return coverImage?.contentHashCode() ?: 0
    }

    fun toNote() : com.designlife.setwork.common.data.entities.Note{
        return com.designlife.setwork.common.data.entities.Note(
             noteId = this.noteId,
             title = this.title,
             content = this.content,
             emoji = this.emoji,
             categoryId = this.categoryId,
             coverImage = this.coverImage,
             createdTime = this.createdTime.time,
             lastModified = this.lastModified.time,
             isArchive = this.isArchive
            )
    }
}
