package com.olga.avshister.rainguard.data.network.owner

import com.olga.avshister.rainguard.data.network.product.ProductNet
import kotlinx.serialization.Serializable

@Serializable
data class RegisterProductRequest(
    val rentPointId: Long,
    val product: ProductNet,
)