package com.dailypoem.app.data

import android.content.Context

class PoemRepository(private val context: Context, private val dao: PoemDao) {
    /** 按标题+作者幂等同步内置数据，新增诗词时无需清库或升级数据库。 */
    suspend fun ensureSeeded() {
        val existingKeys = dao.getAll().map { it.title to it.author }.toSet()
        val missing = PoemJsonLoader.load(context)
            .filter { (it.title to it.author) !in existingKeys }
        if (missing.isNotEmpty()) {
            dao.insertAll(missing)
        }
    }

    suspend fun all(): List<Poem> = dao.getAll()

    suspend fun getById(id: Long): Poem? = dao.getById(id)

    suspend fun byAuthor(author: String): List<Poem> = dao.getByAuthor(author)

    suspend fun setFavorite(id: Long, favorite: Boolean) = dao.setFavorite(id, favorite)

    suspend fun favorites(): List<Poem> = dao.getFavorites()

    suspend fun search(query: String): List<Poem> {
        val keyword = query.trim()
        return if (keyword.isEmpty()) dao.getAll() else dao.search(keyword)
    }
}
