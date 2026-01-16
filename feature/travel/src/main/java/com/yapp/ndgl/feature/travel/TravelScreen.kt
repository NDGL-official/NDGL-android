package com.yapp.ndgl.feature.travel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.ui.base.collectSideEffect

@Composable
internal fun TravelRoute(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: TravelViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is TravelSideEffect.NavigateToDetail -> onNavigateToDetail(sideEffect.travelId)
        }
    }

    TravelScreen(
        state = state,
        onTravelClick = { id -> viewModel.onIntent(TravelIntent.OnTravelClick(id)) })
}

@Composable
internal fun TravelScreen(
    state: TravelState = TravelState(),
    onTravelClick: (Int) -> Unit = {},
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
            Text(text = state.displayText)
        }
        item {
            Button(onClick = {
                onTravelClick(123)
            }) {
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
