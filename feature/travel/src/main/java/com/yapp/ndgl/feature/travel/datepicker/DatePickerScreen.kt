package com.yapp.ndgl.feature.travel.datepicker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.designsystem.NDGLCTAButton
import com.yapp.ndgl.core.ui.designsystem.NDGLCTAButtonAttr
import com.yapp.ndgl.core.ui.designsystem.NDGLModal
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBar
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBarAttr
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.feature.travel.datepicker.component.CalendarView
import kotlinx.datetime.LocalDate

@Composable
internal fun DatePickerRoute(
    viewModel: DatePickerViewModel,
    navigateBack: () -> Unit,
    navigateToTravelDetail: (Long, Int) -> Unit,
) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is DatePickerSideEffect.NavigateToTravelDetail -> {
                navigateToTravelDetail(sideEffect.travelId, sideEffect.days)
            }
            is DatePickerSideEffect.NavigateBack -> {
                navigateBack()
            }
        }
    }

    DatePickerScreen(
        state = state,
        onBackClick = navigateBack,
        onDateSelected = { date -> viewModel.onIntent(DatePickerIntent.SelectDate(date)) },
        onPreviousMonthClick = { viewModel.onIntent(DatePickerIntent.SelectPreviousMonth) },
        onNextMonthClick = { viewModel.onIntent(DatePickerIntent.SelectNextMonth) },
        onCompleteClick = { viewModel.onIntent(DatePickerIntent.ClickCompleteButton) },
        onDatePickerModalNegativeButtonClick = { viewModel.onIntent(DatePickerIntent.ClickDatePickerModalNegativeButton) },
        onConfirmDatePickerModal = { viewModel.onIntent(DatePickerIntent.ConfirmDatePickerModal) },
        onDismissDatePickerModal = { viewModel.onIntent(DatePickerIntent.DismissDatePickerModal) },
    )
}

@Composable
private fun DatePickerScreen(
    state: DatePickerState,
    onBackClick: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onDatePickerModalNegativeButtonClick: () -> Unit,
    onConfirmDatePickerModal: () -> Unit,
    onDismissDatePickerModal: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            NDGLNavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                headline = stringResource(R.string.date_picker_title),
                textAlignType = NDGLNavigationBarAttr.TextAlignType.CENTER,
                leadingIcon = R.drawable.ic_28_chevron_left,
                onLeadingIconClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NDGLTheme.colors.white)
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp, bottom = 16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                ) {
                    CalendarView(
                        year = state.currentYear,
                        month = state.currentMonth,
                        startDate = state.startDate,
                        endDate = state.endDate,
                        onDateSelected = onDateSelected,
                        onPreviousMonth = onPreviousMonthClick,
                        onNextMonth = onNextMonthClick,
                    )

                    if (state.isInsufficientDuration) {
                        Spacer(Modifier.height(24.dp))
                        Text(
                            stringResource(
                                R.string.date_picker_error_insufficient,
                            ),
                            color = NDGLTheme.colors.red500,
                            style = NDGLTheme.typography.bodySmMedium,
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    NDGLCTAButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        type = NDGLCTAButtonAttr.Type.PRIMARY,
                        size = NDGLCTAButtonAttr.Size.LARGE,
                        status = if (state.isDateSelected) {
                            NDGLCTAButtonAttr.Status.ACTIVE
                        } else {
                            NDGLCTAButtonAttr.Status.DISABLED
                        },
                        label = stringResource(R.string.date_picker_complete),
                        onClick = onCompleteClick,
                    )
                }
            }

            if (state.showDatePickerModal) {
                NDGLModal(
                    onDismissRequest = onDismissDatePickerModal,
                    title = stringResource(R.string.date_picker_modal_title),
                    body = stringResource(R.string.date_picker_modal_body),
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                    negativeButtonText = stringResource(R.string.date_picker_modal_negative),
                    onNegativeButtonClick = onDatePickerModalNegativeButtonClick,
                    positiveButtonText = stringResource(R.string.date_picker_modal_positive),
                    onPositiveButtonClick = onConfirmDatePickerModal,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DatePickerScreenPreview() {
    NDGLTheme {
        DatePickerScreen(
            state = DatePickerState(
                templateId = 1L,
                tripDays = 3,
            ),
            onBackClick = {},
            onDateSelected = {},
            onPreviousMonthClick = {},
            onNextMonthClick = {},
            onCompleteClick = {},
            onConfirmDatePickerModal = {},
            onDismissDatePickerModal = {},
            onDatePickerModalNegativeButtonClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DatePickerScreenWithDialogPreview() {
    NDGLTheme {
        DatePickerScreen(
            state = DatePickerState(
                templateId = 1L,
                currentYear = 2026,
                currentMonth = 4,
                tripDays = 3,
                startDate = LocalDate(2026, 4, 23),
                endDate = LocalDate(2026, 4, 27),
                showDatePickerModal = true,
            ),
            onBackClick = {},
            onDateSelected = {},
            onPreviousMonthClick = {},
            onNextMonthClick = {},
            onCompleteClick = {},
            onConfirmDatePickerModal = {},
            onDismissDatePickerModal = {},
            onDatePickerModalNegativeButtonClick = {},
        )
    }
}
