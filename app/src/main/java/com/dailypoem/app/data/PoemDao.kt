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
            "OR content LIKE '%' || :query || '%' ORDER BY id"
    )
    suspend fun search(query: String): List<Poem>

    @Insert
    suspend fun insertAll(poems: List<Poem>)

    @Query("SELECT COUNT(*) FROM poems")
    suspend fun count(): Int
}
