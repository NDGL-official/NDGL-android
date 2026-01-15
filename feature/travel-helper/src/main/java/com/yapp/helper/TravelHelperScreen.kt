package com.yapp.helper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun TravelHelperRoute(
    viewModel: TravelHelperViewModel = hiltViewModel(),
) {

    TravelHelperScreen()
}

@Composable
internal fun TravelHelperScreen(
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(text = "Travel Helper Screen")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TravelHelperScreenPreview() {
    TravelHelperScreen()
}