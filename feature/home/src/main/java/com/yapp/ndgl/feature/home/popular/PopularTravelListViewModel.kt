package com.yapp.ndgl.feature.home.popular

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.travel.model.TravelProgram
import com.yapp.ndgl.data.travel.model.TravelTemplateSummary
import com.yapp.ndgl.data.travel.repository.TravelProgramRepository
import com.yapp.ndgl.data.travel.repository.TravelTemplateRepository
import com.yapp.ndgl.feature.home.model.TravelContent
import com.yapp.ndgl.feature.home.model.TravelProgramTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PopularTravelListViewModel @Inject constructor(
    private val travelProgramRepository: TravelProgramRepository,
    private val travelTemplateRepository: TravelTemplateRepository,
) : BaseViewModel<PopularTravelListState, PopularTravelListIntent, PopularTravelListSideEffect>(
    initialState = PopularTravelListState(),
) {
    init {
        loadPopularTemplates()
    }

    private fun loadPopularTemplates() {
        viewModelScope.launch {
            suspendRunCatching { travelProgramRepository.getAllPrograms() }
                .onSuccess { programs ->
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
            }.toImmutableList()

            val allTravels = allTemplateDeferred.await()
                ?.content
                ?.map { it.toTravelContent() }
                ?.toImmutableList()
                ?: persistentListOf()

            val travelsByProgram = mutableMapOf<Long, ImmutableList<TravelContent>>()
            popularTemplateDeferred.awaitAll().forEach { (program, result) ->
                travelsByProgram[program.id] = result?.content
                    ?.map { it.toTravelContent() }
                    ?.toImmutableList()
                    ?: persistentListOf()
            }

            reduce {
                copy(
                    travelProgramTabs = tabs,
                    allPopularTravels = allTravels,
                    popularTravelsByProgram = travelsByProgram.toImmutableMap(),
                )
            }
        }
    }

    private fun TravelTemplateSummary.toTravelContent() = TravelContent(
        travelId = id,
        title = title,
        country = country,
        countryName = countryName,
        city = city,
        nights = nights,
        days = days,
        programName = programName,
        programType = programType,
        thumbnail = thumbnail ?: "",
    )

    override suspend fun handleIntent(intent: PopularTravelListIntent) {
        when (intent) {
            PopularTravelListIntent.ClickSearchTravelTemplate -> postNavigateToSearchTravelTemplate()
            is PopularTravelListIntent.SelectPopularTravelTab -> selectTab(intent.index)
            is PopularTravelListIntent.ClickTravel -> postNavigateToTravelTemplate(intent.travelId, intent.days)
        }
    }

    private fun postNavigateToSearchTravelTemplate() {
        postSideEffect(PopularTravelListSideEffect.NavigateToSearchTravelTemplate)
    }

    private fun selectTab(index: Int) {
        reduce { copy(selectedTabIndex = index) }
    }

    private fun postNavigateToTravelTemplate(travelId: Long, days: Int) {
        postSideEffect(
            PopularTravelListSideEffect.NavigateToFollowTravel(
                travelId = travelId,
                days = days,
            ),
        )
    }
}
