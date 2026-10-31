package com.dailypoem.app.data

import org.json.JSONArray
import org.json.JSONObject

/** 统一处理 chinese-poetry 各分类不一致的 JSON 字段。 */
object PoemFieldMapper {
    private val punctuation = "，。！？；：、（）《》"

    fun map(obj: JSONObject, defaultDynasty: String, defaultTags: String = ""): Poem {
        val rawTitle = obj.optString("title").takeIf { it.isNotBlank() }
            ?: obj.optString("rhythmic").takeIf { it.isNotBlank() }
            ?: "无题"
        val body = bodyLines(obj)
        val (title, lines) = cleanTitle(rawTitle, body)
        return Poem(
            title = title,
            author = obj.optString("author").ifBlank { "佚名" },
            dynasty = resolveDynasty(obj, defaultDynasty),
            content = lines.joinToString("\n"),
            tags = obj.optString("tags", "").ifBlank { defaultTags },
            note = arrayLines(obj.optJSONArray("notes")).joinToString("\n")
                .ifBlank { obj.optString("note", "") }
        )
    }

    fun isTitlePolluted(title: String): Boolean {
        val sep = listOf("・", "·").firstOrNull { title.contains(it) } ?: return false
        val after = title.substring(title.indexOf(sep) + sep.length)
        return after.any { it in punctuation }
    }

    private fun bodyLines(obj: JSONObject): List<String> {
        for (key in listOf("paragraphs", "content", "para")) {
            val lines = arrayLines(obj.optJSONArray(key))
            if (lines.any { it.isNotBlank() }) return lines
        }
        val text = obj.optString("content", "").trim()
        return if (text.isNotEmpty() && !text.startsWith("[")) listOf(text) else emptyList()
    }

    private fun cleanTitle(rawTitle: String, body: List<String>): Pair<String, List<String>> {
        if (!isTitlePolluted(rawTitle)) return rawTitle to body
        val sep = listOf("・", "·").first { rawTitle.contains(it) }
        val idx = rawTitle.indexOf(sep)
        val before = rawTitle.substring(0, idx).trim()
        val after = rawTitle.substring(idx + sep.length).trim()
        return before to (listOf(after) + body)
    }

    private fun resolveDynasty(obj: JSONObject, default: String): String {
        val dynasty = obj.optString("dynasty", "").trim()
        return if (dynasty.isNotEmpty() && dynasty.any { it.code >= 0x4E00 }) dynasty else default
    }

    private fun arrayLines(array: JSONArray?): List<String> {
        if (array == null || array.length() == 0) return emptyList()
        return (0 until array.length()).map { array.optString(it) }
    }
}
