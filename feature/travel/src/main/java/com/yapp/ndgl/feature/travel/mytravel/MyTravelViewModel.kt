package com.yapp.ndgl.feature.travel.mytravel

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.travel.model.UpcomingTravelResponse
import com.yapp.ndgl.data.travel.repository.TravelTemplateRepository
import com.yapp.ndgl.data.travel.repository.UserTravelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class MyTravelViewModel @Inject constructor(
    private val userTravelRepository: UserTravelRepository,
    private val travelTemplateRepository: TravelTemplateRepository,
) : BaseViewModel<MyTravelState, MyTravelIntent, MyTravelSideEffect>(
    initialState = MyTravelState(),
) {
    init {
        viewModelScope.launch {
            val upcomingDeferred = async { loadUpcomingTravel() }
            val listDeferred = async { loadUpcomingTravelList() }

            val upcomingTravel = upcomingDeferred.await()
            val upcomingTravels = listDeferred.await()

            reduce { copy(upcomingTravel = upcomingTravel, upcomingTravels = upcomingTravels) }

            if (upcomingTravel == null && upcomingTravels.isEmpty()) {
                loadRecommendedTravels()
            }
        }

        // 초기 로드 후 이벤트 구독
        subscribeToTravelCreatedEvent()
    }

    private suspend fun loadUpcomingTravel(): MyTravelState.UpcomingTravel? {
        val travel = suspendRunCatching {
            userTravelRepository.getUpcomingTravel()
        }.getOrNull() ?: return null

        return mapToUpcomingTravel(travel)
    }

    private fun mapToUpcomingTravel(travel: UpcomingTravelResponse): MyTravelState.UpcomingTravel? {
        val today = LocalDate.now()
        return when {
            today < travel.startDate -> {
                val dDay = ChronoUnit.DAYS.between(today, travel.startDate).toInt()
                MyTravelState.UpcomingTravel.Upcoming(
                    travelId = travel.userTravelId,
                    title = travel.title,
                    startDate = travel.startDate,
                    endDate = travel.endDate,
                    imageUrl = travel.thumbnail ?: "",
                    dDay = dDay,
                )
            }

            today <= travel.endDate -> {
                val dayCount =
                    ChronoUnit.DAYS.between(travel.startDate, today).toInt() + 1
                val upcomingPlace = travel.upcomingUserTravelPlace
                MyTravelState.UpcomingTravel.InProgress(
                    travelId = travel.userTravelId,
                    title = travel.title,
                    startDate = travel.startDate,
                    endDate = travel.endDate,
                    dayCount = dayCount,
                    currentPlace = upcomingPlace?.place?.let { place ->
                        MyTravelState.TravelPlace(
                            placeId = place.googlePlaceId,
                            category = place.category,
                            estimatedDuration = upcomingPlace.estimatedDuration,
                            name = place.name,
                            thumbnailUrl = place.thumbnail ?: "",
                        )
                    },
                )
            }

            else -> null
        }
    }

    private suspend fun loadUpcomingTravelList(): ImmutableList<MyTravelState.UpcomingTravelItem> {
        val result = suspendRunCatching {
            userTravelRepository.getUpcomingTravelList()
        }.getOrNull() ?: return persistentListOf()

        val today = LocalDate.now()
        return result.content.map { travel ->
            val dDay = ChronoUnit.DAYS.between(today, travel.startDate).toInt()
            MyTravelState.UpcomingTravelItem(
                travelId = travel.id,
                title = travel.title,
                startDate = travel.startDate,
                endDate = travel.endDate,
                imageUrl = travel.thumbnail ?: "",
                dDay = dDay,
            )
        }.toImmutableList()
    }

    private suspend fun loadRecommendedTravels() {
        suspendRunCatching {
            travelTemplateRepository.getRecommendTravelTemplates()
        }.onSuccess { result ->
            val travels = result.content.map { template ->
                MyTravelState.RecommendedTravel(
                    travelId = template.id,
                    title = template.title,
                    country = template.country,
                    city = template.city,
                    nights = template.nights,
                    days = template.days,
                    programName = template.programName,
                    programType = template.programType,
                    thumbnailUrl = template.thumbnail ?: "",
                )
            }.toImmutableList()
            reduce { copy(recommendedTravels = travels) }
        }
    }

    private fun subscribeToTravelCreatedEvent() = viewModelScope.launch {
        userTravelRepository.travelCreatedEvent.collect { event ->
            refreshTravelData()
        }
    }

    private fun refreshTravelData() = viewModelScope.launch {
        // 병렬로 두 API 호출
        val upcomingDeferred = async { loadUpcomingTravel() }
        val listDeferred = async { loadUpcomingTravelList() }

        val upcomingTravel = upcomingDeferred.await()
        val upcomingTravels = listDeferred.await()

        reduce { copy(upcomingTravel = upcomingTravel, upcomingTravels = upcomingTravels) }

        // 여행 목록이 비어있으면 추천 여행 로드
        if (upcomingTravel == null && upcomingTravels.isEmpty()) {
            loadRecommendedTravels()
        }
    }

    override suspend fun handleIntent(intent: MyTravelIntent) {
        when (intent) {
            MyTravelIntent.ClickSearchTravelTemplate -> postNavigateToSearchTravelTemplate()
            MyTravelIntent.ClickSettings -> postNavigateToSettings()
            is MyTravelIntent.ClickTravel -> postNavigateToFollowTravel(travelId = intent.travelId, days = intent.days)
            is MyTravelIntent.ClickTravelDetail -> postNavigateToTravelDetail(travelId = intent.travelId, days = intent.days)
            is MyTravelIntent.ClickPlaceDetail -> postNavigateToPlaceDetail(placeId = intent.placeId)
            MyTravelIntent.ClickFindNewTravel -> postNavigateToPopularTravelList()
        }
    }

    private fun postNavigateToSearchTravelTemplate() {
        postSideEffect(MyTravelSideEffect.NavigateToSearchTravelTemplate)
    }

    private fun postNavigateToSettings() {
        postSideEffect(MyTravelSideEffect.NavigateToSettings)
    }

    private fun postNavigateToFollowTravel(travelId: Long, days: Int) {
        postSideEffect(MyTravelSideEffect.NavigateToFollowTravel(travelId = travelId, days = days))
    }

    private fun postNavigateToTravelDetail(travelId: Long, days: Int) {
        postSideEffect(MyTravelSideEffect.NavigateToTravelDetail(travelId = travelId, days = days))
    }

    private fun postNavigateToPlaceDetail(placeId: String) {
        postSideEffect(MyTravelSideEffect.NavigateToTravelPlace(placeId = placeId))
    }

    private fun postNavigateToPopularTravelList() {
        postSideEffect(MyTravelSideEffect.NavigateToPopularTravelList)
    }
}
