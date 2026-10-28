package com.dailypoem.app.data

import android.content.Context
import org.json.JSONArray

/** 从 assets/poems.json 读取内置诗词数据，首次启动时写入 Room。 */
object PoemJsonLoader {
    fun load(context: Context): List<Poem> {
        val json = context.assets.open("poems.json")
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }
        val array = JSONArray(json)
        val poems = ArrayList<Poem>(array.length())
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            poems.add(
                Poem(
                    id = 0,
                    title = obj.optString("title").takeIf { it.isNotBlank() }
                        ?: obj.optString("rhythmic").takeIf { it.isNotBlank() }
                        ?: "无题",
                    author = obj.optString("author").ifBlank { "佚名" },
                    dynasty = obj.optString("dynasty").ifBlank { "未知" },
                    content = join(obj.optJSONArray("paragraphs"))
                        .ifBlank { obj.optString("content", "") },
                    tags = obj.optString("tags", ""),
                    note = join(obj.optJSONArray("notes"))
                        .ifBlank { obj.optString("note", "") }
                )
            )
        }
        return poems
    }

    private fun join(array: JSONArray?): String {
        if (array == null || array.length() == 0) return ""
        return buildString {
            for (i in 0 until array.length()) {
                if (i > 0) append('\n')
                append(array.optString(i))
            }
        }
    }
}
