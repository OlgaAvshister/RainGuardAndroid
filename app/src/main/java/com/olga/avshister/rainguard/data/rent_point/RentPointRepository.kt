package com.olga.avshister.rainguard.data.rent_point

import com.olga.avshister.rainguard.domain.filter.Filter
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.rent.Rent
import com.olga.avshister.rainguard.domain.rent.RentPoint

interface RentPointRepository {
    suspend fun setRentPointId(id: Long)
    suspend fun getRentPointId(): Long
    suspend fun registerRentPoint(rentPoint: RentPoint)
    suspend fun getRentPoints(): List<RentPoint>
    suspend fun getRentPointById(id: Long): RentPoint?

    suspend fun searchProducts(filter: Filter?, rentPointId: Long): List<Product>
    suspend fun searchProducts(article: Long, rentPointId: Long): List<Product>

    suspend fun searchProducts(ids: List<Long>, rentPointId: Long): List<Product>

    fun deleteRentPoint(rentPointId: Long)

    suspend fun startRent(rent: Rent)

    /**
     * Завершить аренду в выбранной точке возврата.
     * Добавить прибыль
     * Добавить товары в эту точку возврата
     */
    fun finishRent(rentId: Long, rentPointId: Long, products: List<Product>, finishTime: Long)

    fun addStuff(firstName: String, phone: String)
    fun addProduct(rentPointId: Long, product: Product)
    fun getCompletedRents(rentPointId: Long): List<Rent>
}