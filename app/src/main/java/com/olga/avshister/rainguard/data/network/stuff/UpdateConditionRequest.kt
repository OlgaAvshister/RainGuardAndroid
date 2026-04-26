package com.olga.avshister.rainguard.data.network.stuff

import com.olga.avshister.rainguard.domain.products.Product

data class UpdateConditionRequest(
    val productId: Long,
    val condition: Product.ProductCondition
)