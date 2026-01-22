package com.yapp.travel.travel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
internal fun TravelRoute(
    onNavigateToDetail: (String) -> Unit,
    viewModel: TravelViewModel = hiltViewModel(),
) {

    TravelScreen(
        onNavigateToDetail = onNavigateToDetail
    )
}

@Composable
internal fun TravelScreen(
    onNavigateToDetail: (String) -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(text = "Travel Screen")
        }
        item {
            Button(onClick = { onNavigateToDetail("travel-123") }) {
                Text(text = "Go to Travel Detail")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TravelScreenPreview() {
    TravelScreen()
}