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
import com.yapp.ndgl.feature.home.popular.PopularTravelListSideEffect.ShowSnackBar.Type
import com.yapp.ndgl.feature.home.popular.PopularTravelListState.Success.PopularTravelListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PopularTravelListViewModel @Inject constructor(
    private val travelProgramRepository: TravelProgramRepository,
    private val travelTemplateRepository: TravelTemplateRepository,
) : BaseViewModel<PopularTravelListState, PopularTravelListIntent, PopularTravelListSideEffect>(
    initialState = PopularTravelListState.Loading,
) {
    @Volatile
    private var isLoadingMore = false

    init {
        loadPopularTemplates()
    }

    private fun loadPopularTemplates() {
        viewModelScope.launch {
            suspendRunCatching { travelProgramRepository.getAllPrograms() }
                .onSuccess { programs ->
                    loadPopularTemplatesByPrograms(programs = programs)
                }.onFailure {
                    reduce { PopularTravelListState.Error }
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

            val allResult = allTemplateDeferred.await()
            val allTravels = buildList {
                allResult?.content?.forEach { add(PopularTravelListItem.Travel(it.toTravelContent())) }
                if (allResult?.hasNext == true) add(PopularTravelListItem.Loading(nextPage = 1))
            }.toImmutableList()

            val travelsByProgram = mutableMapOf<Long, ImmutableList<PopularTravelListItem>>()
            popularTemplateDeferred.awaitAll().forEach { (program, result) ->
                travelsByProgram[program.id] = buildList {
                    result?.content?.forEach { add(PopularTravelListItem.Travel(it.toTravelContent())) }
                    if (result?.hasNext == true) add(PopularTravelListItem.Loading(nextPage = 1))
                }.toImmutableList()
            }

            reduce {
                PopularTravelListState.Success(
                    travelProgramTabs = tabs,
                    allPopularTravels = allTravels,
                    popularTravelsByProgram = travelsByProgram.toImmutableMap(),
                )
            }
        }
    }

    private fun loadMore(nextPage: Int) {
        if (isLoadingMore) return

        val currentUiState = state.value
        if ((currentUiState is PopularTravelListState.Success).not()) return

        isLoadingMore = true

        val selectedTab = currentUiState
            .travelProgramTabs
            .getOrElse(currentUiState.selectedTabIndex, { TravelProgramTab.All })

        viewModelScope.launch {
            suspendRunCatching {
                when (selectedTab) {
                    TravelProgramTab.All -> travelTemplateRepository.getAllPopularTravelTemplates(
                        page = nextPage,
                    )

                    is TravelProgramTab.Custom -> travelTemplateRepository.getPopularTravelTemplates(
                        travelProgramId = selectedTab.programId,
                        page = nextPage,
                    )
                }
            }.onSuccess { templates ->
                val newItems = buildList {
                    templates.content.forEach {
                        add(PopularTravelListItem.Travel(it.toTravelContent()))
                    }
                    if (templates.hasNext) {
                        add(PopularTravelListItem.Loading(nextPage = nextPage + 1))
                    }
                }
                reduce {
                    if ((this is PopularTravelListState.Success).not()) return@reduce this

                    when (selectedTab) {
                        TravelProgramTab.All -> copy(
                            allPopularTravels = allPopularTravels.appendNextPage(newItems)
                                .toImmutableList(),
                        )

                        is TravelProgramTab.Custom -> {
                            val current =
                                popularTravelsByProgram[selectedTab.programId] ?: persistentListOf()
                            copy(
                                popularTravelsByProgram = popularTravelsByProgram
                                    .toMutableMap()
                                    .apply {
                                        set(
                                            selectedTab.programId,
                                            current.appendNextPage(newItems).toImmutableList(),
                                        )
                                    }
                                    .toImmutableMap(),
                            )
                        }
                    }
                }
            }.onFailure {
                postSideEffect(PopularTravelListSideEffect.ShowSnackBar(type = Type.ERR_UNKNOWN))
            }

            isLoadingMore = false
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

    private fun ImmutableList<PopularTravelListItem>.appendNextPage(
        newItems: List<PopularTravelListItem>,
    ) = (if (lastOrNull() is PopularTravelListItem.Loading) dropLast(1) else this) + newItems

    override suspend fun handleIntent(intent: PopularTravelListIntent) {
        when (intent) {
            PopularTravelListIntent.ClickSearchTravelTemplate -> postNavigateToSearchTravelTemplate()
            is PopularTravelListIntent.SelectPopularTravelTab -> selectTab(intent.index)
            is PopularTravelListIntent.ClickTravel -> postNavigateToTravelTemplate(
                intent.travelId,
                intent.days,
            )

            is PopularTravelListIntent.LoadMore -> loadMore(intent.nextPage)
            PopularTravelListIntent.ClickRetry -> loadPopularTemplates()
        }
    }

    private fun postNavigateToSearchTravelTemplate() {
        postSideEffect(PopularTravelListSideEffect.NavigateToSearchTravelTemplate)
    }

    private fun selectTab(index: Int) {
        reduce {
            if ((this is PopularTravelListState.Success).not()) return@reduce this
            copy(selectedTabIndex = index)
        }
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
