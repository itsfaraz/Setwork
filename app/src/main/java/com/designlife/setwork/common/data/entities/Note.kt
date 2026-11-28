package com.designlife.setwork.common.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.designlife.setwork.common.utils.serializer.LongSerializer
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
@Entity(tableName = "Note")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val noteId : Long = 0L,
    val title : String,
    val content : String,
    val emoji : String = "📓",
    val categoryId : Long,
    val coverImage : ByteArray? = null,
    @Serializable(with = LongSerializer::class)
    @SerializedName("createdTime")
    val createdTime: Long,
    @Serializable(with = LongSerializer::class)
    @SerializedName("lastModified")
    val lastModified : Long,
    val isArchive : Boolean = false,
) {
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
}
