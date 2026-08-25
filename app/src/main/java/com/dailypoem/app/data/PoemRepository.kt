package com.dailypoem.app.data

class PoemRepository(private val dao: PoemDao) {
    /** 首次启动时写入预置数据。 */
    suspend fun ensureSeeded() {
        if (dao.count() == 0) {
            dao.insertAll(SeedData.poems)
        }
    }

    suspend fun all(): List<Poem> = dao.getAll()

    suspend fun getById(id: Long): Poem? = dao.getById(id)

    suspend fun byAuthor(author: String): List<Poem> = dao.getByAuthor(author)

    suspend fun search(query: String): List<Poem> {
        val keyword = query.trim()
        return if (keyword.isEmpty()) dao.getAll() else dao.search(keyword)
    }
}
