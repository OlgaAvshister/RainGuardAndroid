package com.olga.avshister.rainguard.presentation.state

import com.olga.avshister.rainguard.domain.profile.Role
import com.olga.avshister.rainguard.domain.rent.RentPoint

data class MapMainContentState(
    val isLoading: Boolean,
    val hasActiveRent: Boolean,
    val role: Role,
    val rentPoints: List<RentPoint>,
)