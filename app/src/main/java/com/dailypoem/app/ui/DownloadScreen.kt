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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailypoem.app.data.PoemCatalog
import com.dailypoem.app.data.PoemCategory
import com.dailypoem.app.ui.theme.Accent
import com.dailypoem.app.ui.theme.CardBeige
import com.dailypoem.app.ui.theme.Divider
import com.dailypoem.app.ui.theme.Ink
import com.dailypoem.app.ui.theme.InkMuted
import com.dailypoem.app.ui.theme.PoemSerif

@Composable
fun DownloadScreen(
    viewModel: DownloadViewModel = viewModel(),
    onBack: () -> Unit
) {
    val counts by viewModel.counts.collectAsStateWithLifecycle()
    val active = viewModel.active
    val progress = viewModel.progress
    val error = viewModel.error
    val message = viewModel.message

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
                text = "诗词下载",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = PoemSerif,
                color = Ink
            )
        }

        if (!error.isNullOrBlank()) {
            Text(
                text = error,
                color = Color(0xFFB4573F),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
        if (!message.isNullOrBlank()) {
            Text(
                text = message,
                color = Accent,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(PoemCatalog.categories) { category ->
                CategoryCard(
                    category = category,
                    downloaded = counts[category.name] ?: 0,
                    isActive = active == category.name,
                    progress = progress,
                    onDownload = { viewModel.download(category) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryCard(
    category: PoemCategory,
    downloaded: Int,
    isActive: Boolean,
    progress: Float,
    onDownload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBeige),
        border = BorderStroke(1.dp, Divider)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = PoemSerif,
                        color = Ink
                    )
                    Text(
                        text = "${category.dynasty} · ${category.files.size} 个文件",
                        style = MaterialTheme.typography.labelMedium,
                        color = InkMuted
                    )
                }
                if (downloaded > 0) {
                    Text(
                        text = "已下载 $downloaded 首",
                        style = MaterialTheme.typography.labelMedium,
                        color = Accent
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            if (isActive) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "下载中 ${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = InkMuted
                )
            } else {
                Button(
                    onClick = onDownload,
                    enabled = downloaded == 0,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent,
                        contentColor = Color.White
                    )
                ) {
                    Text(if (downloaded > 0) "已下载" else "下载")
                }
            }
        }
    }
}
