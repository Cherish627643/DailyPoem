package com.dailypoem.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dailypoem.app.ui.theme.Accent
import com.dailypoem.app.ui.theme.AccentLight
import com.dailypoem.app.ui.theme.Divider
import com.dailypoem.app.ui.theme.Ink
import com.dailypoem.app.ui.theme.InkMuted
import com.dailypoem.app.ui.theme.PoemSerif

@Composable
fun DetailScreen(
    viewModel: PoemViewModel,
    poemId: Long,
    onBack: () -> Unit
) {
    val allPoems by viewModel.allPoems.collectAsStateWithLifecycle()
    val poem = allPoems.firstOrNull { it.id == poemId }
    var favorite by rememberSaveable(poemId) { mutableStateOf(false) }

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
                text = "诗词详情",
                style = MaterialTheme.typography.titleMedium,
                color = Ink
            )
        }

        if (poem == null) {
            Text(
                text = "未找到该诗词",
                modifier = Modifier.padding(24.dp),
                color = InkMuted
            )
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "${poem.dynasty} · ${poem.author}",
                    style = MaterialTheme.typography.labelLarge,
                    color = InkMuted
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = poem.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = PoemSerif,
                    color = Ink
                )
                if (poem.tags.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        poem.tags.split(",").filter { it.isNotBlank() }.forEach { tag ->
                            TagChip(tag)
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 20.dp),
                    color = Divider
                )
                Text(
                    text = poem.content,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = PoemSerif,
                    color = Ink,
                    lineHeight = 34.sp
                )
                Spacer(Modifier.height(24.dp))
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = { favorite = !favorite },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (favorite) Accent else Ink,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = if (favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (favorite) "已收藏" else "收藏")
                }
            }
        }
    }
}
