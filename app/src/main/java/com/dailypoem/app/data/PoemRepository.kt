package com.dailypoem.app.data

import android.content.Context

class PoemRepository(private val context: Context, private val dao: PoemDao) {
    /** 与内置 JSON 做幂等同步：新增、更新字段、删除已下架诗词，并保留收藏状态。 */
    suspend fun ensureSeeded() {
        val jsonPoems = PoemJsonLoader.load(context)
        val existing = dao.getAll()
        val existingByKey = existing.associateBy { it.title to it.author }
        val jsonKeys = jsonPoems.map { it.title to it.author }.toSet()

        val toInsert = ArrayList<Poem>()
        val toUpdate = ArrayList<Poem>()
        for (poem in jsonPoems) {
            val old = existingByKey[poem.title to poem.author]
            if (old == null) {
                toInsert.add(poem)
            } else if (old.content != poem.content || old.note != poem.note ||
                old.dynasty != poem.dynasty || old.tags != poem.tags
            ) {
                toUpdate.add(
                    old.copy(
                        content = poem.content,
                        note = poem.note,
                        dynasty = poem.dynasty,
                        tags = poem.tags
                    )
                )
            }
        }

        if (toInsert.isNotEmpty()) dao.insertAll(toInsert)
        toUpdate.forEach { dao.update(it) }

        val stale = existing.filter { (it.title to it.author) !in jsonKeys }
        if (stale.isNotEmpty()) dao.deleteByIds(stale.map { it.id })
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
