package com.olga.avshister.rainguard.presentation.state

import com.olga.avshister.rainguard.domain.rent.RentPoint

data class MapMainContentState(
    val isLoading: Boolean,
    val hasActiveRent: Boolean,
    val rentPoints: List<RentPoint>,
)