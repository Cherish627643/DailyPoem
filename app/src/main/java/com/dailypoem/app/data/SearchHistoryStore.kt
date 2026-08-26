package com.dailypoem.app.data

import android.content.Context

/** 使用 SharedPreferences 保存最近搜索关键词，最多保留 20 条。 */
class SearchHistoryStore(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("search_history", Context.MODE_PRIVATE)

    fun load(): List<String> =
        prefs.getString(KEY, null)?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()

    fun add(keyword: String) {
        val cleaned = keyword.trim()
        if (cleaned.isEmpty()) return
        val merged = (listOf(cleaned) + load().filter { it != cleaned }).take(MAX)
        prefs.edit().putString(KEY, merged.joinToString("\n")).apply()
    }

    fun clear() {
        prefs.edit().remove(KEY).apply()
    }

    companion object {
        private const val KEY = "recent_keywords"
        private const val MAX = 20
    }
}
