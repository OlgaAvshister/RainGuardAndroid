package com.olga.avshister.rainguard.data.network.product

import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.products.Product.Colors
import com.olga.avshister.rainguard.domain.products.Product.FormFactor
import com.olga.avshister.rainguard.domain.products.Product.PrintType
import com.olga.avshister.rainguard.domain.products.Product.ProductCondition
import com.olga.avshister.rainguard.domain.products.Product.ProductType
import com.olga.avshister.rainguard.domain.products.Product.Size
import kotlinx.serialization.Serializable

@Serializable
data class ProductNet(
    val id: Long, // инвентарный номер
    val productType: ProductType, // зонт/дождевик
    val article: Long = -1, // поле, которое однозначно закрепляет за собой набор характеристик (несколько товаров с одинаковыми атрибутами должны иметь одинаковый артикул)
    val printType: PrintType, // есть принт/нет принта
    val color: Colors,
    val formFactor: FormFactor, // FOLDING (складывающийся)/STICK (трость) для зонта; JACKET (куртка)/ FULLBODY_RAINCOAT для дождевика на всё тело
    val size: Size?, // только для дождевика
    val condition: ProductCondition? = ProductCondition.READY // состояние товара
) {
    companion object {
        fun List<ProductNet>.toDomain() = this.map {
            Product(
                id = it.id,
                productType = it.productType,
                article = it.article,
                printType = it.printType,
                color = it.color,
                formFactor = it.formFactor,
                size = it.size,
                condition = it.condition,
            )
        }
    }
}