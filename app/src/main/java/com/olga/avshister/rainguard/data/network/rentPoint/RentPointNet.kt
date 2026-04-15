package com.olga.avshister.rainguard.data.network.rentPoint

import com.olga.avshister.rainguard.data.network.product.ProductNet
import com.olga.avshister.rainguard.data.network.product.ProductNet.Companion.toDomain
import com.olga.avshister.rainguard.domain.rent.RentPoint
import kotlinx.serialization.Serializable

@Serializable
data class RentPointNet(
    val id: Long,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val workHours: String,
    val availableProducts: List<ProductNet>,
) {
    companion object {
        fun List<RentPointNet>.toDomain(): List<RentPoint> = this.map {
            RentPoint(
                id = it.id,
                name = it.name,
                address = it.address,
                latitude = it.latitude,
                longitude = it.longitude,
                workHours = it.workHours,
                availableProducts = it.availableProducts.toDomain()
            )
        }
    }
}