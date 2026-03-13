package com.olga.avshister.rainguard.presentation.state.rent

data class RentTotalState(
    val totalTime: String,
    val totalCost: String,
    val isLoading: Boolean
)