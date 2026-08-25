package com.dailypoem.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dailypoem.app.data.Poem
import com.dailypoem.app.ui.theme.Accent
import com.dailypoem.app.ui.theme.AccentLight
import com.dailypoem.app.ui.theme.CardBeige
import com.dailypoem.app.ui.theme.Divider
import com.dailypoem.app.ui.theme.Ink
import com.dailypoem.app.ui.theme.InkMuted
import com.dailypoem.app.ui.theme.PoemSerif

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PoemViewModel,
    onPoemClick: (Long) -> Unit
) {
    val allPoems by viewModel.allPoems.collectAsStateWithLifecycle()
    val query = viewModel.query
    val current = viewModel.currentPoem
    val results = remember(allPoems, query) {
        if (query.isBlank()) {
            emptyList()
        } else {
            allPoems.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.author.contains(query, ignoreCase = true) ||
                    it.content.contains(query, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = viewModel.todayText,
                    style = MaterialTheme.typography.labelMedium,
                    color = InkMuted
                )
                Text(
                    text = "每日古诗",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = PoemSerif,
                    color = Ink
                )
            }
            IconButton(onClick = viewModel::swapPoem) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "换一首",
                    tint = Ink
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        SearchField(query = query, onQueryChange = viewModel::onQueryChange)

        Spacer(Modifier.height(20.dp))

        if (query.isBlank()) {
            if (current != null) {
                TodayCard(poem = current, onClick = { onPoemClick(current.id) })
            } else {
                Text(
                    text = "正在准备今日推荐…",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    textAlign = TextAlign.Center,
                    color = InkMuted
                )
            }
        } else {
            SearchResults(
                poems = results,
                onPoemClick = onPoemClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("搜索题目、诗人或正文", color = InkMuted) },
        leadingIcon = {
            Icon(Icons.Filled.Search, contentDescription = null, tint = InkMuted)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Close, contentDescription = "清空", tint = InkMuted)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Accent,
            unfocusedBorderColor = Divider,
            focusedContainerColor = CardBeige,
            unfocusedContainerColor = CardBeige,
            cursorColor = Ink
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodayCard(poem: Poem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, Divider)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFBF7EE), Color(0xFFF1E9D9))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "${poem.dynasty} · ${poem.author}",
                    style = MaterialTheme.typography.labelMedium,
                    color = InkMuted
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = poem.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = PoemSerif,
                    color = Ink
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = poem.preview,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = PoemSerif,
                    color = Ink,
                    lineHeight = 30.sp
                )
                if (poem.tags.isNotBlank()) {
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        poem.tags.split(",").filter { it.isNotBlank() }.forEach { tag ->
                            TagChip(tag)
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "点击查看全文",
                    style = MaterialTheme.typography.labelMedium,
                    color = Accent
                )
            }
        }
    }
}

@Composable
private fun SearchResults(
    poems: List<Poem>,
    onPoemClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (poems.isEmpty()) {
        Text(
            text = "没有找到相关诗词",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp),
            textAlign = TextAlign.Center,
            color = InkMuted
        )
        return
    }
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(poems, key = { it.id }) { poem ->
            PoemListCard(poem = poem, onClick = { onPoemClick(poem.id) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PoemListCard(poem: Poem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBeige),
        border = BorderStroke(1.dp, Divider)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = poem.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = PoemSerif,
                    color = Ink,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${poem.dynasty} · ${poem.author}",
                    style = MaterialTheme.typography.labelMedium,
                    color = InkMuted
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = poem.preview,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = PoemSerif,
                color = InkMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
