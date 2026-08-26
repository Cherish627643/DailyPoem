package com.dailypoem.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PoemDao {
    @Query("SELECT * FROM poems ORDER BY id")
    suspend fun getAll(): List<Poem>

    @Query("SELECT * FROM poems WHERE id = :id")
    suspend fun getById(id: Long): Poem?

    @Query(
        "SELECT * FROM poems WHERE title LIKE '%' || :query || '%' " +
            "OR author LIKE '%' || :query || '%' " +
            "OR content LIKE '%' || :query || '%' " +
            "OR tags LIKE '%' || :query || '%' ORDER BY id"
    )
    suspend fun search(query: String): List<Poem>

    @Query("SELECT * FROM poems WHERE author = :author ORDER BY id")
    suspend fun getByAuthor(author: String): List<Poem>

    @Query("UPDATE poems SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean)

    @Query("SELECT * FROM poems WHERE isFavorite = 1 ORDER BY id")
    suspend fun getFavorites(): List<Poem>

    @Insert
    suspend fun insertAll(poems: List<Poem>)

    @Query("SELECT COUNT(*) FROM poems")
    suspend fun count(): Int
}
