package com.dailypoem.app.data

import android.content.Context

class PoemRepository(private val context: Context, private val dao: PoemDao) {
    /** 首次启动时写入预置数据。 */
    suspend fun ensureSeeded() {
        if (dao.count() == 0) {
            dao.insertAll(PoemJsonLoader.load(context))
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
