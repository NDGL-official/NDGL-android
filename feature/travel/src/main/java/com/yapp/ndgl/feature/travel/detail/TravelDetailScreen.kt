package com.yapp.ndgl.feature.travel.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun TravelDetailRoute(
    viewModel: TravelDetailViewModel = hiltViewModel(),
) {
    val travelId by viewModel.travelId.collectAsStateWithLifecycle()

    TravelDetailScreen(travelId = travelId)
}

@Composable
internal fun TravelDetailScreen(
    travelId: String,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Travel Detail Screen",
                fontSize = 24.sp
            )
        }
        item {
            Text(
                text = "Travel ID: $travelId",
                fontSize = 18.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TravelDetailScreenPreview() {
    TravelDetailScreen(travelId = "12345")
}