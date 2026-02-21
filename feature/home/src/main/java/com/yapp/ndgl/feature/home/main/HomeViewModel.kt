package com.yapp.ndgl.feature.home.main

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.travel.model.TravelProgram
import com.yapp.ndgl.data.travel.model.TravelTemplateSummary
import com.yapp.ndgl.data.travel.repository.TravelProgramRepository
import com.yapp.ndgl.data.travel.repository.TravelTemplateRepository
import com.yapp.ndgl.data.travel.repository.UserTravelRepository
import com.yapp.ndgl.feature.home.model.TravelContent
import com.yapp.ndgl.feature.home.model.TravelProgramTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val travelProgramRepository: TravelProgramRepository,
    private val travelTemplateRepository: TravelTemplateRepository,
    private val userTravelRepository: UserTravelRepository,
) : BaseViewModel<HomeState, HomeIntent, HomeSideEffect>(
    initialState = HomeState(),
) {
    init {
        loadHomeContents()
    }

    private fun loadHomeContents() {
        loadMyTravel()
        loadPopularTemplates()
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
                            val dayCount =
                                ChronoUnit.DAYS.between(travel.startDate, today).toInt() + 1
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

    private fun loadPopularTemplates() {
        viewModelScope.launch {
            suspendRunCatching { travelProgramRepository.getAllPrograms() }.onSuccess { programs ->
                loadPopularTemplatesByPrograms(programs = programs)
            }.onFailure {
                // FIXME: 에러 뷰
                Timber.d("fail to load popular $it")
            }
        }
    }

    private fun loadPopularTemplatesByPrograms(programs: List<TravelProgram>) {
        viewModelScope.launch {
            val allTemplateDeferred = async {
                suspendRunCatching { travelTemplateRepository.getAllPopularTravelTemplates() }.getOrNull()
            }
            val popularTemplateDeferred = programs.map { program ->
                async {
                    program to suspendRunCatching {
                        travelTemplateRepository.getPopularTravelTemplates(program.id)
                    }.getOrNull()
                }
            }

            val tabs = buildList {
                add(TravelProgramTab.All)
                programs.forEach { program ->
                    add(
                        TravelProgramTab.Custom(
                            programId = program.id,
                            name = program.name,
                            type = program.type,
                        ),
                    )
                }
            }
            val allTravels = allTemplateDeferred.await()
                ?.content
                ?.map { it.toTravelContent() }
                ?.take(MAX_POPULAR_TRAVEL_COUNT)
                ?: emptyList()

            val travelsByProgram = mutableMapOf<Long, List<TravelContent>>()
            popularTemplateDeferred.awaitAll().forEach { (program, result) ->
                travelsByProgram[program.id] = result?.content
                    ?.map { it.toTravelContent() }
                    ?.take(MAX_POPULAR_TRAVEL_COUNT)
                    ?: emptyList()
            }

            reduce {
                copy(
                    travelProgramTabs = tabs,
                    allPopularTravels = allTravels,
                    popularTravelsByProgram = travelsByProgram,
                )
            }
        }
    }

    private fun loadRecommendedTravel() {
        viewModelScope.launch {
            suspendRunCatching { travelTemplateRepository.getRecommendTravelTemplates() }
                .onSuccess { travels ->
                    val recommendTravels = travels.content.map { it.toTravelContent() }
                    reduce { copy(recommendedContents = recommendTravels) }
                }
        }
    }

    private fun TravelTemplateSummary.toTravelContent() = TravelContent(
        travelId = id,
        title = title,
        country = country,
        city = city,
        nights = nights,
        days = days,
        programName = programName,
        programType = programType,
        thumbnail = thumbnail ?: "",
    )

    override suspend fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.ClickSearchTravelTemplate -> postNavigateToSearchTravelTemplate()
            HomeIntent.ClickSettings -> postNavigateToSettings()
            is HomeIntent.SelectPopularTravelTab -> {
                reduce { copy(popularTravelSelectedTabIndex = intent.index) }
            }

            is HomeIntent.ClickTravel -> postNavigateToTravelTemplate(travelId = intent.travelId, days = intent.days)
            HomeIntent.ClickTravelMore -> postNavigateToTravelMore()
        }
    }

    private fun postNavigateToSearchTravelTemplate() {
        postSideEffect(HomeSideEffect.NavigateToSearchTravelTemplate)
    }

    private fun postNavigateToSettings() {
        postSideEffect(HomeSideEffect.NavigateToSettings)
    }

    private fun postNavigateToTravelTemplate(travelId: Long, days: Int) {
        postSideEffect(HomeSideEffect.NavigateToFollowTravel(travelId = travelId, days = days))
    }

    private fun postNavigateToTravelMore() {
        postSideEffect(HomeSideEffect.NavigateToTravelMore)
    }

    companion object {
        private const val MAX_POPULAR_TRAVEL_COUNT = 9
    }
}
