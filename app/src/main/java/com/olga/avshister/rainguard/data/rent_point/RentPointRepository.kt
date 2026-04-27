package com.olga.avshister.rainguard.data.rent_point

import com.olga.avshister.rainguard.domain.filter.Filter
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.rent.Rent
import com.olga.avshister.rainguard.domain.rent.RentPoint

interface RentPointRepository {
    suspend fun setStartRentPointId(id: Long)
    suspend fun getStartRentPointId(): Long

    suspend fun setFinishRentPointId(id: Long)
    suspend fun getFinishRentPointId(): Long

    suspend fun registerRentPoint(
        name: String,
        fullAddress: String,
        latitude: Double,
        longitude: Double,
        workHours: String
    )
    suspend fun getRentPoints(): List<RentPoint>
    suspend fun getRentPointById(id: Long): RentPoint?

    suspend fun searchProducts(filter: Filter?, rentPointId: Long): List<Product>
    suspend fun searchProducts(article: Long, rentPointId: Long): List<Product>

    suspend fun searchProducts(ids: List<Long>, rentPointId: Long?): List<Product>

    suspend fun deleteRentPoint(rentPointId: Long)

    suspend fun startRent(rent: Rent)

    /**
     * Завершить аренду в выбранной точке возврата.
     * Перенести товары из Rent в точку возврата
     */
    suspend fun finishRent(rent: Rent)

    suspend fun addProduct(rentPointId: Long, product: Product)
    suspend fun getCompletedRents(rentPointId: Long): List<Rent>
    suspend fun updateCondition(productId: Long, condition: Product.ProductCondition)
}