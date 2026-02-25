package com.yapp.ndgl.feature.travelhelper.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBar
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBarAttr
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationIcon
import com.yapp.ndgl.core.ui.designsystem.NDGLSnackbar
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.feature.travelhelper.R
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperState.TravelUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import com.yapp.ndgl.core.ui.R as CoreR

@Composable
internal fun TravelHelperRoute(
    navigateToSearch: () -> Unit,
    navigateToTravelDetail: (Long, Int) -> Unit,
    navigateToPopularTravelList: () -> Unit,
    navigateToPlaceDetail: (String) -> Unit,
    viewModel: TravelHelperViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val exchangeRateErrorMessage = stringResource(R.string.travel_helper_exchange_rate_error)

    TravelHelperScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onSearchClick = { viewModel.onIntent(TravelHelperIntent.ClickSearch) },
        onNewTravelFindClick = navigateToPopularTravelList,
        onTravelClick = { travelId, days ->
            viewModel.onIntent(TravelHelperIntent.ClickTravelCard(travelId, days))
        },
        onPlaceClick = {
            viewModel.onIntent(TravelHelperIntent.ClickPlace(it))
        },
        onCurrencyInputChange = { viewModel.onIntent(TravelHelperIntent.UpdateCurrencyInput(it)) },
        onSwapCurrency = { viewModel.onIntent(TravelHelperIntent.SwapCurrency) },
        onCurrencySelect = { viewModel.onIntent(TravelHelperIntent.SelectCurrency(it)) },
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            TravelHelperSideEffect.NavigateToSearch -> navigateToSearch()
            is TravelHelperSideEffect.NavigateToTravelDetail -> {
                navigateToTravelDetail(sideEffect.travelId, sideEffect.days)
            }

            TravelHelperSideEffect.ShowExchangeRateError -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(exchangeRateErrorMessage)
                }
            }

            is TravelHelperSideEffect.NavigateToPlaceDetail -> navigateToPlaceDetail(sideEffect.placeId)
        }
    }
}

@Composable
private fun TravelHelperScreen(
    state: TravelHelperState,
    snackbarHostState: SnackbarHostState,
    onSearchClick: () -> Unit,
    onNewTravelFindClick: () -> Unit,
    onTravelClick: (Long, Int) -> Unit,
    onPlaceClick: (String) -> Unit,
    onCurrencyInputChange: (String) -> Unit,
    onSwapCurrency: () -> Unit,
    onCurrencySelect: (String) -> Unit,
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                NDGLSnackbar(
                    modifier = Modifier.padding(bottom = 100.dp),
                    snackbarData = data,
                )
            }
        },
        topBar = {
            NDGLNavigationBar(
                textAlignType = NDGLNavigationBarAttr.TextAlignType.START,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = NDGLTheme.colors.white)
                    .statusBarsPadding(),
                trailingContents = {
                    NDGLNavigationIcon(
                        icon = CoreR.drawable.ic_28_search,
                        onClick = onSearchClick,
                    )
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(top = 20.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp),
        ) {
            when (val travelUiState = state.travelUiState) {
                TravelUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = NDGLTheme.colors.green500)
                        }
                    }
                }

                TravelUiState.NoTravel -> noTravelContent(
                    onSearchClick = onSearchClick,
                    onNewTravelFindClick = onNewTravelFindClick,
                )

                is TravelUiState.UpcomingTravel -> upcomingTravelContent(
                    travel = travelUiState,
                    currencyInput = state.currencyInput,
                    convertedAmount = state.convertedAmount,
                    availableCurrencies = state.availableCurrencies,
                    onTravelClick = onTravelClick,
                    onCurrencyInputChange = onCurrencyInputChange,
                    onSwapCurrency = onSwapCurrency,
                    onCurrencySelect = onCurrencySelect,
                )

                is TravelUiState.OngoingTravel -> inProgressTravelContent(
                    travel = travelUiState,
                    currencyInput = state.currencyInput,
                    convertedAmount = state.convertedAmount,
                    availableCurrencies = state.availableCurrencies,
                    onTravelClick = onTravelClick,
                    onPlaceClick = onPlaceClick,
                    onCurrencyInputChange = onCurrencyInputChange,
                    onSwapCurrency = onSwapCurrency,
                    onCurrencySelect = onCurrencySelect,
                )
            }
        }
    }
}

private fun LazyListScope.noTravelContent(
    onSearchClick: () -> Unit,
    onNewTravelFindClick: () -> Unit,
) {
    item {
        EmptyTravelCard(
            modifier = Modifier,
            onCardClick = onSearchClick,
        )
    }
    item {
        EmptyTravel(
            onNewTravelFindClick = onNewTravelFindClick,
        )
    }
}

@Composable
private fun EmptyTravel(
    onNewTravelFindClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(CoreR.drawable.img_empty_suitcase),
            contentDescription = null,
            modifier = Modifier.size(100.dp),
        )
        Text(
            text = stringResource(R.string.travel_helper_empty_travel_title),
            modifier = Modifier.padding(top = 16.dp),
            color = NDGLTheme.colors.black500,
            style = NDGLTheme.typography.subtitleMdSemiBold,
        )
        Text(
            text = stringResource(R.string.travel_helper_empty_travel_description),
            modifier = Modifier.padding(top = 4.dp),
            color = NDGLTheme.colors.black400,
            style = NDGLTheme.typography.bodyLgRegular,
        )
        FindNewTravelCtaButton(
            modifier = Modifier.padding(top = 12.dp),
            onClick = onNewTravelFindClick,
        )
    }
}

@Composable
private fun FindNewTravelCtaButton(
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(color = NDGLTheme.colors.black200)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.travel_helper_find_new_travel),
            color = NDGLTheme.colors.black800,
            style = NDGLTheme.typography.bodyMdSemiBold,
        )
        Icon(
            imageVector = ImageVector.vectorResource(CoreR.drawable.ic_20_search),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = NDGLTheme.colors.black600,
        )
    }
}

private fun LazyListScope.upcomingTravelContent(
    travel: TravelUiState.UpcomingTravel,
    currencyInput: String,
    convertedAmount: Double?,
    availableCurrencies: ImmutableList<CurrencyOption>,
    onTravelClick: (Long, Int) -> Unit,
    onCurrencyInputChange: (String) -> Unit,
    onSwapCurrency: () -> Unit,
    onCurrencySelect: (String) -> Unit,
) {
    item {
        UpcomingTravelCard(
            modifier = Modifier,
            travel = travel,
            onCardClick = { onTravelClick(travel.id, travel.days) },
        )
    }
    item {
        WeatherSection(weatherState = travel.weatherState)
    }
    item {
        CurrencyCalculatorSection(
            exchangeRateInfo = travel.exchangeRateInfo,
            currencyInput = currencyInput,
            convertedAmount = convertedAmount,
            availableCurrencies = availableCurrencies,
            onInputChange = onCurrencyInputChange,
            onSwap = onSwapCurrency,
            onCurrencySelect = onCurrencySelect,
        )
    }
}

private fun LazyListScope.inProgressTravelContent(
    travel: TravelUiState.OngoingTravel,
    currencyInput: String,
    convertedAmount: Double?,
    availableCurrencies: ImmutableList<CurrencyOption>,
    onTravelClick: (Long, Int) -> Unit,
    onPlaceClick: (String) -> Unit,
    onCurrencyInputChange: (String) -> Unit,
    onSwapCurrency: () -> Unit,
    onCurrencySelect: (String) -> Unit,
) {
    item {
        InProgressTravelCard(
            travel = travel,
            onTravelClick = onTravelClick,
            onPlaceClick = onPlaceClick,
            modifier = Modifier,
        )
    }
    item {
        WeatherSection(weatherState = travel.weatherState)
    }
    item {
        CurrencyCalculatorSection(
            exchangeRateInfo = travel.exchangeRateInfo,
            currencyInput = currencyInput,
            convertedAmount = convertedAmount,
            availableCurrencies = availableCurrencies,
            onInputChange = onCurrencyInputChange,
            onSwap = onSwapCurrency,
            onCurrencySelect = onCurrencySelect,
        )
    }
}
