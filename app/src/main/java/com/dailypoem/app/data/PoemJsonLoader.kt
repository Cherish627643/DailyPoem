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
                    title = obj.getString("title"),
                    author = obj.getString("author"),
                    dynasty = obj.getString("dynasty"),
                    content = obj.getString("content"),
                    tags = obj.optString("tags", ""),
                    note = obj.optString("note", "")
                )
            )
        }
        return poems
    }
}
