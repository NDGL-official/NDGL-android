package com.yapp.ndgl.feature.travel.mytravel

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.travel.repository.UserTravelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class MyTravelViewModel @Inject constructor(
    private val userTravelRepository: UserTravelRepository,
) : BaseViewModel<MyTravelState, MyTravelIntent, MyTravelSideEffect>(
    initialState = MyTravelState(),
) {
    init {
        loadUpcomingTravel()
        loadUpcomingTravelList()
    }

    private fun loadUpcomingTravel() {
        viewModelScope.launch {
            suspendRunCatching {
                userTravelRepository.getUpcomingTravel()
            }.onSuccess { travel ->
                if (travel == null) {
                    reduce { copy(upcomingTravel = null) }
                    return@onSuccess
                }

                val today = LocalDate.now()
                val myTravel = when {
                    today < travel.startDate -> {
                        val dDay = ChronoUnit.DAYS.between(today, travel.startDate).toInt()
                        MyTravelState.UpcomingTravel.Upcoming(
                            travelId = travel.userTravelId,
                            title = travel.title,
                            startDate = travel.startDate,
                            endDate = travel.endDate,
                            imageUrl = travel.upcomingUserTravelPlace?.place?.thumbnail ?: "",
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
                reduce { copy(upcomingTravel = myTravel) }
            }.onFailure {
                reduce { copy(upcomingTravel = null) }
            }
        }
    }

    private fun loadUpcomingTravelList() {
        viewModelScope.launch {
            suspendRunCatching { userTravelRepository.getUpcomingTravelList() }
                .onSuccess { result ->
                    val today = LocalDate.now()
                    val travels = result.content.map { travel ->
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
                    reduce { copy(upcomingTravels = travels) }
                }
                .onFailure {
                    reduce { copy(upcomingTravels = persistentListOf()) }
                }
        }
    }

    override suspend fun handleIntent(intent: MyTravelIntent) {
        when (intent) {
            is MyTravelIntent.ClickTravel -> postNavigateToFollowTravel(travelId = intent.travelId)
            is MyTravelIntent.ClickTravelDetail -> postNavigateToTravelDetail(travelId = intent.travelId)
            is MyTravelIntent.ClickPlaceDetail -> postNavigateToPlaceDetail(placeId = intent.placeId)
            MyTravelIntent.ClickFindNewTravel -> postNavigateToPopularTravelList()
        }
    }

    private fun postNavigateToFollowTravel(travelId: Long, days: Int = 1) {
        postSideEffect(MyTravelSideEffect.NavigateToFollowTravel(travelId = travelId, days = days))
    }

    private fun postNavigateToTravelDetail(travelId: Long) {
        postSideEffect(MyTravelSideEffect.NavigateToTravelDetail(travelId = travelId))
    }

    private fun postNavigateToPlaceDetail(placeId: String) {
        postSideEffect(MyTravelSideEffect.NavigateToTravelPlace(placeId = placeId))
    }

    private fun postNavigateToPopularTravelList() {
        postSideEffect(MyTravelSideEffect.NavigateToPopularTravelList)
    }
}
