package com.dailypoem.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 诗词实体，正文 content 保留原始换行，tags 使用逗号分隔多个标签。
 */
@Entity(tableName = "poems")
data class Poem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val author: String,
    val dynasty: String,
    val content: String,
    val tags: String = "",
    val isFavorite: Boolean = false
) {
    /** 取正文前两行作为列表预览。 */
    val preview: String
        get() = content.lineSequence()
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("\n")
}
