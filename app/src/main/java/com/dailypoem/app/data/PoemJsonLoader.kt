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
            poems.add(PoemFieldMapper.map(array.getJSONObject(i), "未知", ""))
        }
        return poems
    }
}
