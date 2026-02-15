package com.yapp.ndgl.feature.home.main

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.auth.repository.AuthRepository
import com.yapp.ndgl.data.travel.repository.HomeRepository
import com.yapp.ndgl.data.travel.repository.UserTravelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val homeRepository: HomeRepository,
    private val userTravelRepository: UserTravelRepository,
) : BaseViewModel<HomeState, HomeIntent, HomeSideEffect>(
    initialState = HomeState(),
) {
    init {
        initSession()
    }

    private fun initSession() = viewModelScope.launch {
        suspendRunCatching {
            authRepository.initSession()
        }.onSuccess {
            loadHomeContents()
        }.onFailure { exception ->
            Timber.e("fail to init session: $exception")
            loadHomeContents()
        }
    }

    private fun loadHomeContents() {
        loadMyTravel()
        loadPopularTravel()
        loadRecommendedTravel()
    }

    private fun loadMyTravel() {
        viewModelScope.launch {
            suspendRunCatching { userTravelRepository.getUpcomingTravel() }
                .onSuccess { travel ->
                    if (travel == null) {
                        reduce { copy(myTravel = HomeState.MyTravel.None) }
                        return@onSuccess
                    }

                    val today = LocalDate.now()
                    val myTravel = when {
                        today < travel.startDate -> {
                            val dDay = ChronoUnit.DAYS.between(today, travel.startDate).toInt()
                            HomeState.MyTravel.Upcoming(
                                title = travel.title,
                                imageUrl = travel.upcomingUserTravelPlace?.place?.thumbnail ?: "",
                                dDay = dDay,
                                startDate = travel.startDate,
                                endDate = travel.endDate,
                            )
                        }
                        today <= travel.endDate -> {
                            val dayCount = ChronoUnit.DAYS.between(travel.startDate, today).toInt() + 1
                            val upcomingPlace = travel.upcomingUserTravelPlace
                            HomeState.MyTravel.InProgress(
                                title = travel.title,
                                dayCount = dayCount,
                                startDate = travel.startDate,
                                endDate = travel.endDate,
                                currentPlace = upcomingPlace?.place?.let { place ->
                                    HomeState.TravelPlace(
                                        category = place.category,
                                        estimatedDuration = upcomingPlace.estimatedDuration,
                                        name = place.name,
                                        thumbnailUrl = place.thumbnail ?: "",
                                    )
                                },
                            )
                        }
                        else -> HomeState.MyTravel.None
                    }
                    reduce { copy(myTravel = myTravel) }
                }.onFailure {
                    Timber.e("Failed to load upcoming travel: $it")
                }
        }
    }

    private fun loadPopularTravel() {
        viewModelScope.launch {
            suspendRunCatching { homeRepository.getPopularTravels() }
                .onSuccess { travels ->
                    val travelsByYoutuber = travels.groupBy { travel ->
                        travel.youtube.youtuber
                    }
                    val tabs = travelsByYoutuber.keys
                        .map { youtuber ->
                            HomeState.PopularTravelTab(
                                tag = youtuber,
                                name = youtuber,
                                icon = R.drawable.ic_20_video,
                            )
                        }.toMutableList()
                        .apply {
                            add(
                                index = 0,
                                element = HomeState.PopularTravelTab(
                                    tag = "all",
                                    name = "전체",
                                    icon = null,
                                ),
                            )
                        }.toList()
                    val travelsByTab = travelsByYoutuber.toMutableMap().apply {
                        put("all", travels)
                    }
                    reduce {
                        copy(
                            popularTravelTabs = tabs,
                            popularTravelsByTab = travelsByTab,
                        )
                    }
                }
        }
    }

    private fun loadRecommendedTravel() {
        viewModelScope.launch {
            suspendRunCatching { homeRepository.getRecommendedTravels() }
                .onSuccess { travels ->
                    reduce { copy(recommendedContents = travels) }
                }
        }
    }

    override suspend fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SelectPopularTravelTab -> {
                reduce { copy(popularTravelSelectedTabIndex = intent.index) }
            }
        }
    }
}
