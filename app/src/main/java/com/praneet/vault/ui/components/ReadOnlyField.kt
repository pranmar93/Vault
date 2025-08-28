package com.praneet.vault.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReadOnlyScrollableField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    minHeightDp: Int = 96,
    maxHeightDp: Int = 240
) {
    val scroll = rememberScrollState()
    Column(modifier) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .heightIn(min = minHeightDp.dp, max = maxHeightDp.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scroll)
                    .padding(16.dp)
            ) {
                Text(text = value, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}


