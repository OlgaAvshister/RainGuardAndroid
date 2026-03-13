package com.olga.avshister.rainguard.data.rent_point

import com.olga.avshister.rainguard.domain.filter.Filter
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.rent.Rent
import com.olga.avshister.rainguard.domain.rent.RentPoint
import com.olga.avshister.rainguard.presentation.ui.utils.Dataset.generateProductsDataset
import com.olga.avshister.rainguard.presentation.ui.utils.Dataset.generateRentPoint

/**
 * Репозиторий для получения данных о точках аренды из локального хранилища
 */
object RentPointLocalRepository: RentPointRepository {
    const val RENT_POINT_LOCAL_REPOSITORY_TAG = "RENT_POINT_LOCAL_REPOSITORY_TAG"
    private val allRentPoints: List<RentPoint> by lazy { generateRentPoint() }

    val rentPoint1 = RentPoint(
        id = 1/*Random.nextLong()*/,
        name = "Исторический музей",
        address = "Красная площадь, 1",
        latitude = 55.752511,
        longitude = 37.621570,
        availableProducts = generateProductsDataset()
    )
    val rentPoint2 = RentPoint(
        id = 2/*Random.nextLong()*/,
        name = "Парк \"Зарядье\", флорариум",
        address = "Улица Варварка, 6с1",
        latitude = 55.751670,
        longitude = 37.629053,
        availableProducts = generateProductsDataset()
    )

    val rentPoint3 = RentPoint(
        id = 3/*Random.nextLong()*/,
        name = "Центральный парк культуры и отдыха им. М. Горького",
        address = "Улица Крымский Вал, 9",
        latitude = 55.731411,
        longitude = 37.601792,
        availableProducts = generateProductsDataset()
    )


    override fun getRentPoints(): List<RentPoint> {
        return allRentPoints
    }

    override fun getRentPointById(id: Long): RentPoint? {
        return runCatching { getRentPoints().first { it.id == id } }.getOrNull()
    }

    override fun searchProducts(filter: Filter, rentPointId: Long): List<Product> {
        return allRentPoints
            .find { it.id == rentPointId }
            ?.availableProducts
            ?.filter {
                matchesFilter(product= it, filter = filter)
            } ?: emptyList()
    }

    override fun searchProducts(articul: Long, rentPointId: Long): List<Product> {
        return allRentPoints
            .find { it.id == rentPointId }
            ?.availableProducts
            ?.filter { it.article == articul } ?: emptyList()
    }

    override fun searchProducts(ids: List<Long>, rentPointId: Long): List<Product> {
        return allRentPoints
            .find { it.id == rentPointId }
            ?.availableProducts
            ?.filter { it.id in ids } ?: emptyList()
    }

    override fun finishRent(
        rentId: Long,
        rentPointId: Long,
        products: List<Product>,
        finishTime: Long
    ) {
        TODO("Not yet implemented")
    }

    override fun addStuff(firstName: String, phone: String) {
        TODO("Not yet implemented")
    }

    override fun addProduct(product: Product) {
        TODO("Not yet implemented")
    }

    override fun getCompletedRents(): List<Rent> {
        TODO("Not yet implemented")
    }

    /**
     * Если хоть по одному параметру нет совпадения, считаем что товар не соответсвует фильтру
     * Если какой-то параметр в фильтре не указан - то считаем, что товар соответсвует фильтру
     * и нужно проверить остальные параметры
     */
    private fun matchesFilter(product: Product, filter: Filter): Boolean {
        if (filter.productType != null && filter.productType != product.productType) {
            return false
        }
        if (filter.formFactor != null && filter.formFactor != product.formFactor) {
            return false
        }
        if (filter.printType != null && filter.printType != product.printType) {
            return false
        }
        if (filter.size != null && filter.size != product.size) {
            return false
        }
        if (filter.color != null && filter.color != product.color) {
            return false
        }
        return true
    }
}