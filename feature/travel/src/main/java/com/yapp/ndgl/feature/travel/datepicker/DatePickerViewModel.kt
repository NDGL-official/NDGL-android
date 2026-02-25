package com.yapp.ndgl.feature.travel.datepicker

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.travel.exception.DuplicateTravelPeriodException
import com.yapp.ndgl.data.travel.model.TravelCreatedEvent
import com.yapp.ndgl.data.travel.repository.TravelTemplateRepository
import com.yapp.ndgl.data.travel.repository.UserTravelRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import timber.log.Timber

@HiltViewModel(assistedFactory = DatePickerViewModel.Factory::class)
class DatePickerViewModel @AssistedInject constructor(
    @Assisted private val templateId: Long,
    @Assisted private val tripDays: Int,
    private val travelTemplateRepository: TravelTemplateRepository,
    private val userTravelRepository: UserTravelRepository,
) : BaseViewModel<DatePickerState, DatePickerIntent, DatePickerSideEffect>(
    initialState = DatePickerState(templateId = templateId, tripDays = tripDays),
) {
    override suspend fun handleIntent(intent: DatePickerIntent) {
        when (intent) {
            is DatePickerIntent.SelectDate -> selectDate(intent.date)
            is DatePickerIntent.SelectPreviousMonth -> selectPreviousMonth()
            is DatePickerIntent.SelectNextMonth -> selectNextMonth()
            is DatePickerIntent.SelectYearMonth -> selectYearMonth(intent.year, intent.month)
            is DatePickerIntent.ClickCompleteButton -> clickCompleteButton()
            is DatePickerIntent.ClickDatePickerModalNegativeButton -> clickDatePickerModalNegativeButton()
            is DatePickerIntent.ConfirmDatePickerModal -> confirmDatePickerModal()
            is DatePickerIntent.DismissDatePickerModal -> dismissDatePickerModal()
        }
    }

    private fun selectDate(date: LocalDate) {
        reduce {
            when {
                startDate == null -> {
                    copy(
                        startDate = date,
                        endDate = null,
                        isSelectingRange = true,
                    )
                }

                isSelectingRange -> {
                    if (date == startDate) {
                        copy(startDate = null, isSelectingRange = false)
                    } else if (date < startDate) {
                        copy(
                            startDate = date,
                            endDate = startDate,
                            isSelectingRange = false,
                        )
                    } else {
                        copy(
                            endDate = date,
                            isSelectingRange = false,
                        )
                    }
                }

                else -> {
                    copy(
                        startDate = date,
                        endDate = null,
                        isSelectingRange = true,
                    )
                }
            }
        }
    }

    private fun selectPreviousMonth() {
        reduce {
            val newMonth = if (currentMonth == 1) 12 else currentMonth - 1
            val newYear = if (currentMonth == 1) currentYear - 1 else currentYear
            copy(currentYear = newYear, currentMonth = newMonth)
        }
    }

    private fun selectNextMonth() {
        reduce {
            val newMonth = if (currentMonth == 12) 1 else currentMonth + 1
            val newYear = if (currentMonth == 12) currentYear + 1 else currentYear
            copy(currentYear = newYear, currentMonth = newMonth)
        }
    }

    private fun selectYearMonth(year: Int, month: Int) {
        reduce { copy(currentYear = year, currentMonth = month) }
    }

    private fun clickCompleteButton() {
        val startDate = state.value.startDate
        val endDate = state.value.endDate

        if (startDate != null && endDate != null) {
            createTravelFromTemplate(startDate, endDate)
        }
    }

    private fun clickDatePickerModalNegativeButton() {
        postSideEffect(DatePickerSideEffect.NavigateBack)
    }

    private fun dismissDatePickerModal() {
        reduce { copy(showDatePickerModal = false) }
    }

    private fun confirmDatePickerModal() {
        val travelId = state.value.createdTravelId
        if (travelId != null) {
            postSideEffect(DatePickerSideEffect.NavigateToTravelDetail(travelId = travelId, days = tripDays))
        }
    }

    private fun createTravelFromTemplate(startDate: LocalDate, endDate: LocalDate) = viewModelScope.launch {
        reduce { copy(isLoading = true) }
        suspendRunCatching {
            travelTemplateRepository.createTravelFromTemplate(
                templateId = templateId,
                startDate = startDate.toString(),
                endDate = endDate.toString(),
            )
        }.onSuccess { response ->
            // 이벤트 발행 - 홈/내 여행 탭 자동 새로고침
            userTravelRepository.emitTravelCreatedEvent(
                TravelCreatedEvent(
                    userTravelId = response.userTravelId,
                    templateId = templateId,
                ),
            )

            reduce {
                copy(
                    isLoading = false,
                    showDatePickerModal = true,
                    createdTravelId = response.userTravelId,
                )
            }
        }.onFailure { exception ->
            reduce { copy(isLoading = false) }

            when (exception) {
                is DuplicateTravelPeriodException -> {
                    // FIXME: UI 구현 필요 - 중복 기간 에러 모달 표시
                    Timber.w("Duplicate travel period: ${exception.message}")
                }

                else -> {
                    // FIXME: UI 구현 필요
                    Timber.e(exception, "Failed to create travel from template")
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted templateId: Long,
            @Assisted tripDays: Int,
        ): DatePickerViewModel
    }
}
