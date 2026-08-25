package com.dailypoem.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dailypoem.app.data.Poem
import com.dailypoem.app.ui.theme.CardBeige
import com.dailypoem.app.ui.theme.Divider
import com.dailypoem.app.ui.theme.Ink
import com.dailypoem.app.ui.theme.InkMuted
import com.dailypoem.app.ui.theme.PoemSerif

@Composable
fun PoetScreen(
    viewModel: PoemViewModel,
    onBack: () -> Unit,
    onPoemClick: (Long) -> Unit
) {
    val author = viewModel.selectedAuthor.orEmpty()
    val poems by viewModel.authorPoems.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回", tint = Ink)
            }
            Text(
                text = author,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = PoemSerif,
                color = Ink
            )
        }

        Text(
            text = "共 ${poems.size} 首作品",
            style = MaterialTheme.typography.labelMedium,
            color = InkMuted,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(Modifier.height(12.dp))

        if (poems.isEmpty()) {
            Text(
                text = "暂无作品",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                color = InkMuted
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(poems, key = { it.id }) { poem ->
                    PoetPoemCard(poem = poem, onClick = { onPoemClick(poem.id) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PoetPoemCard(poem: Poem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBeige),
        border = BorderStroke(1.dp, Divider)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = poem.title,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = PoemSerif,
                color = Ink
            )
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
