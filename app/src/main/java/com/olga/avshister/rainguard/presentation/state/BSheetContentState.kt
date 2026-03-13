package com.olga.avshister.rainguard.presentation.state

import com.olga.avshister.rainguard.domain.filter.Filter
import com.olga.avshister.rainguard.domain.rent.RentPoint

sealed class BSheetContentState {
    object IdleState: BSheetContentState()
    data class RentPointState(val rentPoint: RentPoint?): BSheetContentState()
    object CurrentRentState: BSheetContentState()
    data class CatalogState(val filter: Filter, val rentPointId: Long): BSheetContentState()
    data class CartState(val selectedArticles: Set<Long>, val rentPointId: Long): BSheetContentState()
    object SelectIdsStateToTakeState: BSheetContentState()
    object CheckoutState: BSheetContentState()
    object CardsState: BSheetContentState()
    object SelectIdsStateToDropState: BSheetContentState()
    object GiveToCheckState: BSheetContentState()
    object FillStuffNumberState: BSheetContentState()
    object RentTotalState: BSheetContentState()
    object PayInProgressState: BSheetContentState()
    object PaySuccessState: BSheetContentState()
}