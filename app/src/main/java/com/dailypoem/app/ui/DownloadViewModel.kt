package com.dailypoem.app.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dailypoem.app.data.AppDatabase
import com.dailypoem.app.data.PoemCatalog
import com.dailypoem.app.data.PoemCategory
import com.dailypoem.app.data.PoemRepository
import com.dailypoem.app.data.RemotePoemDownloader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DownloadViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.get(application).poemDao()
    private val repository = PoemRepository(application, dao)

    private val _counts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val counts: StateFlow<Map<String, Int>> = _counts.asStateFlow()

    var active by mutableStateOf<String?>(null)
        private set

    var progress by mutableStateOf(0f)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var message by mutableStateOf<String?>(null)
        private set

    init {
        refreshCounts()
    }

    fun refreshCounts() {
        viewModelScope.launch {
            val map = mutableMapOf<String, Int>()
            PoemCatalog.categories.forEach { c -> map[c.name] = repository.countByCategory(c.name) }
            _counts.value = map
        }
    }

    fun download(category: PoemCategory, replace: Boolean = false) {
        if (active != null) return
        viewModelScope.launch {
            active = category.name
            progress = 0f
            error = null
            message = null
            try {
                if (replace) repository.deleteCategory(category.name)
                val count = RemotePoemDownloader.downloadCategory(
                    category = category,
                    onFile = { batch -> repository.insertDownloaded(batch) },
                    onProgress = { done, total -> progress = done.toFloat() / total }
                )
                refreshCounts()
                message = "下载成功，共 $count 首"
            } catch (e: Exception) {
                error = e.message ?: "下载失败"
            } finally {
                active = null
            }
        }
    }

    fun deleteCategory(category: PoemCategory) {
        if (active != null) return
        viewModelScope.launch {
            repository.deleteCategory(category.name)
            refreshCounts()
            message = "已删除「${category.name}」"
        }
    }
}
