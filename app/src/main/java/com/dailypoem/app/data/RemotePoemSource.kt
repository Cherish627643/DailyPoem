package com.dailypoem.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

data class PoemCategory(
    val name: String,
    val dynasty: String,
    val files: List<String>
)

object PoemCatalog {
    val categories: List<PoemCategory> = listOf(
        PoemCategory(
            "五代诗词", "五代",
            listOf(
                "五代诗词/huajianji/huajianji-0-preface.json",
                "五代诗词/huajianji/huajianji-1-juan.json",
                "五代诗词/huajianji/huajianji-2-juan.json",
                "五代诗词/huajianji/huajianji-3-juan.json",
                "五代诗词/huajianji/huajianji-4-juan.json",
                "五代诗词/huajianji/huajianji-5-juan.json",
                "五代诗词/huajianji/huajianji-6-juan.json",
                "五代诗词/huajianji/huajianji-7-juan.json",
                "五代诗词/huajianji/huajianji-8-juan.json",
                "五代诗词/huajianji/huajianji-9-juan.json",
                "五代诗词/huajianji/huajianji-x-juan.json",
                "五代诗词/nantang/poetrys.json"
            )
        ),
        PoemCategory(
            "宋词", "宋",
            listOf(
                "宋词/ci.song.0.json",
                "宋词/ci.song.1000.json",
                "宋词/ci.song.2000.json",
                "宋词/ci.song.3000.json",
                "宋词/ci.song.4000.json",
                "宋词/ci.song.5000.json",
                "宋词/ci.song.6000.json",
                "宋词/ci.song.7000.json",
                "宋词/ci.song.8000.json",
                "宋词/ci.song.9000.json",
                "宋词/ci.song.10000.json",
                "宋词/ci.song.11000.json",
                "宋词/ci.song.12000.json",
                "宋词/ci.song.13000.json",
                "宋词/ci.song.14000.json",
                "宋词/ci.song.15000.json",
                "宋词/ci.song.16000.json",
                "宋词/ci.song.17000.json",
                "宋词/ci.song.18000.json",
                "宋词/ci.song.19000.json",
                "宋词/ci.song.20000.json",
                "宋词/ci.song.21000.json",
                "宋词/ci.song.2019y.json",
                "宋词/宋词三百首.json"
            )
        ),
        PoemCategory("元曲", "元", listOf("元曲/yuanqu.json")),
        PoemCategory("诗经", "先秦", listOf("诗经/shijing.json")),
        PoemCategory("楚辞", "先秦", listOf("楚辞/chuci.json")),
        PoemCategory("纳兰性德", "清", listOf("纳兰性德/纳兰性德诗集.json")),
        PoemCategory("曹操诗集", "魏晋", listOf("曹操诗集/caocao.json"))
    )
}

object RemotePoemDownloader {
    private val mirrors = listOf(
        "https://raw.githubusercontent.com/chinese-poetry/chinese-poetry/master/",
        "https://cdn.jsdelivr.net/gh/chinese-poetry/chinese-poetry@master/",
        "https://gitee.com/mirrors/chinese-poetry/raw/master/"
    )

    suspend fun downloadCategory(
        category: PoemCategory,
        onFile: suspend (List<Poem>) -> Unit,
        onProgress: (Int, Int) -> Unit
    ): Int = withContext(Dispatchers.IO) {
        var total = 0
        category.files.forEachIndexed { index, path ->
            val poems = parse(category, downloadText(path))
            if (poems.isNotEmpty()) {
                onFile(poems)
                total += poems.size
            }
            onProgress(index + 1, category.files.size)
        }
        total
    }

    private fun downloadText(path: String): String {
        var last: Exception? = null
        for (base in mirrors) {
            try {
                val url = URL(base + encodePath(path))
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 20_000
                conn.readTimeout = 60_000
                conn.instanceFollowRedirects = true
                conn.setRequestProperty("User-Agent", "DailyPoem")
                try {
                    if (conn.responseCode in 200..299) {
                        return conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                    }
                    last = IllegalStateException("HTTP ${conn.responseCode}")
                } finally {
                    conn.disconnect()
                }
            } catch (e: Exception) {
                last = e
            }
        }
        throw last ?: IllegalStateException("下载失败: $path")
    }

    private fun parse(category: PoemCategory, text: String): List<Poem> {
        val array = JSONArray(text)
        val result = ArrayList<Poem>(array.length())
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val title = obj.optString("title").takeIf { it.isNotBlank() }
                ?: obj.optString("rhythmic").takeIf { it.isNotBlank() }
                ?: "无题"
            val author = obj.optString("author").ifBlank { "佚名" }
            val content = joinArray(obj.optJSONArray("paragraphs")).ifBlank {
                obj.optString("content", "")
            }
            val note = joinArray(obj.optJSONArray("notes"))
            result.add(
                Poem(
                    title = title,
                    author = author,
                    dynasty = category.dynasty,
                    content = content,
                    tags = category.name,
                    note = note,
                    category = category.name
                )
            )
        }
        return result
    }

    private fun joinArray(array: JSONArray?): String {
        if (array == null || array.length() == 0) return ""
        return buildString {
            for (i in 0 until array.length()) {
                if (i > 0) append('\n')
                append(array.optString(i))
            }
        }
    }

    private fun encodePath(path: String): String = buildString {
        for (b in path.toByteArray(Charsets.UTF_8)) {
            val c = b.toInt() and 0xFF
            val safe = (c in 0x30..0x39) || (c in 0x41..0x5A) || (c in 0x61..0x7A) ||
                c == '/'.code || c == '-'.code || c == '_'.code || c == '.'.code || c == '~'.code
            if (safe) append(c.toChar()) else append('%').append(String.format("%02X", c))
        }
    }
}
