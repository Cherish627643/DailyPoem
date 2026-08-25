package com.dailypoem.app.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dailypoem.app.data.AppDatabase
import com.dailypoem.app.data.Poem
import com.dailypoem.app.data.PoemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.random.Random

class PoemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PoemRepository(AppDatabase.get(application).poemDao())

    private val _allPoems = MutableStateFlow<List<Poem>>(emptyList())
    val allPoems: StateFlow<List<Poem>> = _allPoems.asStateFlow()

    private val _authorPoems = MutableStateFlow<List<Poem>>(emptyList())
    val authorPoems: StateFlow<List<Poem>> = _authorPoems.asStateFlow()

    private val _favorites = MutableStateFlow<List<Poem>>(emptyList())
    val favorites: StateFlow<List<Poem>> = _favorites.asStateFlow()

    var query by mutableStateOf("")
        private set

    var currentPoem by mutableStateOf<Poem?>(null)
        private set

    var selectedAuthor by mutableStateOf<String?>(null)
        private set

    val todayText: String = LocalDate.now().format(
        DateTimeFormatter.ofPattern("yyyy年MM月dd日 EEEE", Locale.CHINA)
    )

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            val all = repository.all()
            _allPoems.value = all
            currentPoem = dailyPoem(all)
            _favorites.value = repository.favorites()
        }
    }

    fun onQueryChange(value: String) {
        query = value
    }

    /** 手动换一首，仅改变当前显示，不影响次日按日期生成的推荐。 */
    fun swapPoem() {
        val all = _allPoems.value
        if (all.isEmpty()) return
        val pool = if (all.size == 1) all else all.filter { it.id != currentPoem?.id }
        currentPoem = pool[Random.nextInt(pool.size)]
    }

    /** 进入诗人作品页前，记录作者并查询其全部作品。 */
    fun openPoet(author: String) {
        selectedAuthor = author
        viewModelScope.launch {
            _authorPoems.value = repository.byAuthor(author)
        }
    }

    /** 收藏或取消收藏，并同步刷新各列表。 */
    fun toggleFavorite(id: Long, favorite: Boolean) {
        viewModelScope.launch {
            repository.setFavorite(id, favorite)
            _favorites.value = repository.favorites()
            _allPoems.value = _allPoems.value.map { if (it.id == id) it.copy(isFavorite = favorite) else it }
            _authorPoems.value = _authorPoems.value.map { if (it.id == id) it.copy(isFavorite = favorite) else it }
        }
    }

    private fun dailyPoem(all: List<Poem>): Poem? {
        if (all.isEmpty()) return null
        val seed = LocalDate.now().toEpochDay()
        val index = Math.floorMod(seed, all.size.toLong()).toInt()
        return all.getOrNull(index)
    }
}
