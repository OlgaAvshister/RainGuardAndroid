package com.olga.avshister.rainguard.data.rent_point

import com.olga.avshister.rainguard.domain.filter.Filter
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.rent.Rent
import com.olga.avshister.rainguard.domain.rent.RentPoint
import com.olga.avshister.rainguard.presentation.ui.utils.Dataset.generateCompletedRents
import com.olga.avshister.rainguard.presentation.ui.utils.Dataset.generateProductsDataset
import com.olga.avshister.rainguard.presentation.ui.utils.Dataset.generateRentPoint

/**
 * Репозиторий для получения данных о точках аренды из локального хранилища
 */
object RentPointLocalRepository: RentPointRepository {
    const val RENT_POINT_LOCAL_REPOSITORY_TAG = "RENT_POINT_LOCAL_REPOSITORY_TAG"
    private val allRentPoints: MutableList<RentPoint> by lazy { generateRentPoint() }

    val rentPoint1 = RentPoint(
        id = 1/*Random.nextLong()*/,
        name = "Исторический музей",
        address = "Красная площадь, 1",
        latitude = 55.752511,
        longitude = 37.621570,
        workHours = "Сегодня 11:00-19:00",
        availableProducts = generateProductsDataset()
    )
    val rentPoint2 = RentPoint(
        id = 2/*Random.nextLong()*/,
        name = "Парк \"Зарядье\", флорариум",
        address = "Улица Варварка, 6с1",
        latitude = 55.751670,
        longitude = 37.629053,
        workHours = "Сегодня 11:00-19:00",
        availableProducts = generateProductsDataset()
    )

    val rentPoint3 = RentPoint(
        id = 3/*Random.nextLong()*/,
        name = "Центральный парк культуры и отдыха им. М. Горького",
        address = "Улица Крымский Вал, 9",
        latitude = 55.731411,
        longitude = 37.601792,
        workHours = "Сегодня 11:00-19:00",
        availableProducts = generateProductsDataset()
    )

    override suspend fun setRentPointId(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getRentPointId(): Long {
        TODO("Not yet implemented")
    }

    override suspend fun registerRentPoint(rentPoint: RentPoint) {
        allRentPoints.add(rentPoint)
    }


    override suspend fun getRentPoints(): List<RentPoint> {
        return allRentPoints
    }

    override suspend fun getRentPointById(id: Long): RentPoint? {
        return runCatching { getRentPoints().first { it.id == id } }.getOrNull()
    }

    override suspend fun searchProducts(filter: Filter?, rentPointId: Long): List<Product> {
        return allRentPoints
            .find { it.id == rentPointId }
            ?.availableProducts
            ?.filter {
                matchesFilter(product= it, filter = filter)
            } ?: emptyList()
    }

    override suspend fun searchProducts(article: Long, rentPointId: Long): List<Product> {
        return allRentPoints
            .find { it.id == rentPointId }
            ?.availableProducts
            ?.filter { it.article == article } ?: emptyList()
    }

    override suspend fun searchProducts(ids: List<Long>, rentPointId: Long): List<Product> {
        return allRentPoints
            .find { it.id == rentPointId }
            ?.availableProducts
            ?.filter { it.id in ids } ?: emptyList()
    }

    override fun deleteRentPoint(rentPointId: Long) {
        val index = allRentPoints.indexOfFirst { it.id == rentPointId }
        allRentPoints.removeAt(index)
    }

    override suspend fun startRent(rent: Rent) {
        TODO("Not yet implemented")
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

    override fun addProduct(rentPointId: Long, product: Product) {
        val index = allRentPoints.indexOfFirst { it.id == rentPointId }
        val initialRentPoint = allRentPoints[index]
        val modifiedAvailableProducts = initialRentPoint.availableProducts.toMutableList()
        .apply {
            add(product)
        }
        .toList()

        val modifiedRentPoint = initialRentPoint.copy(
            availableProducts = modifiedAvailableProducts
        )

        allRentPoints[index] = modifiedRentPoint
    }

    override fun getCompletedRents(rentPointId: Long): List<Rent> {
        // todo: сделать отдельный репозиторий под завершенные аренды. в этом репозитории наверное нехорошо хранить? т.к. аренда може тбыть начата в одном пункте, а завершена в другом
        return generateCompletedRents()
    }

    /**
     * Если хоть по одному параметру нет совпадения, считаем что товар не соответсвует фильтру
     * Если какой-то параметр в фильтре не указан - то считаем, что товар соответсвует фильтру
     * и нужно проверить остальные параметры
     */
    fun matchesFilter(product: Product, filter: Filter?): Boolean {
        // если фильтр не задан, то товар автоматически соответствует фильтру
        if (filter == null)
            return true

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