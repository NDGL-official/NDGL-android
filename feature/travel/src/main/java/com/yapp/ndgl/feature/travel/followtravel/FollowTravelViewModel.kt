package com.yapp.ndgl.feature.travel.followtravel

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.core.util.toCountryName
import com.yapp.ndgl.data.travel.model.TravelTemplateContentInfo
import com.yapp.ndgl.data.travel.model.TravelTemplateItinerary
import com.yapp.ndgl.data.travel.repository.TravelTemplateRepository
import com.yapp.ndgl.feature.travel.model.AlternativePlace
import com.yapp.ndgl.feature.travel.model.TipContent
import com.yapp.ndgl.feature.travel.model.TransportSegment
import com.yapp.ndgl.feature.travel.model.toOpeningHours
import com.yapp.ndgl.feature.travel.model.toPlaceType
import com.yapp.ndgl.feature.travel.model.toTransportType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

@HiltViewModel(assistedFactory = FollowTravelViewModel.Factory::class)
class FollowTravelViewModel @AssistedInject constructor(
    @Assisted private val travelId: Long,
    @Assisted private val days: Int,
    private val travelTemplateRepository: TravelTemplateRepository,
) : BaseViewModel<FollowTravelState, FollowTravelIntent, FollowTravelSideEffect>(
    initialState = FollowTravelState(travelId = travelId, days = days),
) {
    init {
        loadTravelTemplateItinerary()
        loadTravelTemplateContentInfo()
    }

    private fun loadTravelTemplateItinerary() = viewModelScope.launch {
        reduce { copy(itineraries = List(days) { Itinerary() }) }
        for (day in 1..days) {
            launch {
                suspendRunCatching {
                    travelTemplateRepository.getTravelTemplateItinerary(travelId = travelId, day = day)
                }.onSuccess { itinerary ->
                    reduce {
                        val updated = itineraries.toMutableList()
                        updated[day - 1] = itinerary.toItinerary()
                        copy(itineraries = updated)
                    }
                }.onFailure {
                    // FIXME: 일차 화면 별 에러 뷰
                }
            }
        }
    }

    private fun loadTravelTemplateContentInfo() = viewModelScope.launch {
        suspendRunCatching {
            travelTemplateRepository.getTravelTemplateContentInfo(travelId = travelId)
        }.onSuccess { info ->
            reduce {
                copy(
                    countryCode = info.countryCode,
                    contentInfo = info.toContentInfo(),
                )
            }
        }.onFailure {
            // TODO: 에러 처리
        }
    }

    override suspend fun handleIntent(intent: FollowTravelIntent) {
        when (intent) {
            is FollowTravelIntent.SelectDay -> {
                reduce { copy(selectedDay = intent.day) }
            }

            is FollowTravelIntent.ClickFollowTravel -> {
                // TODO: Handle follow
            }

            is FollowTravelIntent.ClickPlaceItem -> {
                postSideEffect(
                    FollowTravelSideEffect.NavigateToFollowPlaceDetail(
                        placeId = intent.place.googlePlaceId,
                        tipContent = intent.place.travelerTips.takeIf { it.isNotEmpty() }?.let {
                            TipContent(creatorName = state.value.contentInfo.videoInfo.creatorName, tips = it)
                        },
                        alternativePlaces = intent.place.alternativePlaces.takeIf { it.isNotEmpty() },
                    ),
                )
            }
        }
    }

    private fun TravelTemplateItinerary.toItinerary(): Itinerary = Itinerary(
        places = itineraries.mapIndexed { index, item ->
            val nextItem = itineraries.getOrNull(index + 1)
            TravelPlace(
                id = item.id,
                day = item.day,
                sequence = item.sequence,
                estimatedDuration = (item.estimatedDuration).minutes,
                googlePlaceId = item.place.googlePlaceId,
                thumbnail = item.place.thumbnail,
                latitude = item.place.latitude,
                longitude = item.place.longitude,
                name = item.place.name,
                openingHours = item.place.regularOpeningHours.toOpeningHours(),
                googleMapsUri = item.place.googleMapsUri,
                placeType = item.place.category.toPlaceType(),
                transportToNext = nextItem?.transportation?.firstOrNull()?.let { transport ->
                    TransportSegment(
                        type = transport.mode.toTransportType(),
                        duration = transport.timeMin.minutes,
                        distance = ((nextItem.distanceKm ?: 0.0) * 1000).toInt(),
                    )
                },
                travelerTips = item.travelerTips.orEmpty(),
                alternativePlaces = item.planB.orEmpty().map { planB ->
                    AlternativePlace(
                        id = "",
                        name = planB.name,
                        thumbnail = planB.feature ?: "",
                        placeType = item.place.category.toPlaceType(),
                    )
                },
            )
        },
    )

    private fun TravelTemplateContentInfo.toContentInfo(): ContentInfo = ContentInfo(
        country = countryCode.toCountryName(),
        city = city,
        budgetPerPerson = Budget(budgetPerPerson ?: 0),
        nights = nights,
        days = days,
        videoInfo = VideoInfo(
            title = program.title,
            creatorName = program.creatorName,
            profileImage = program.profileImage,
            thumbnail = program.thumbnail,
            link = program.link,
            summary = program.summary,
        ),
    )

    @AssistedFactory
    interface Factory {
        fun create(travelId: Long, days: Int): FollowTravelViewModel
    }
}
