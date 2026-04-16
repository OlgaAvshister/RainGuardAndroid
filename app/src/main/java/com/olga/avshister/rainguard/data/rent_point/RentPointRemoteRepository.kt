package com.olga.avshister.rainguard.data.rent_point

import android.content.Context
import com.olga.avshister.rainguard.data.network.NetworkClient
import com.olga.avshister.rainguard.data.network.rentPoint.RentPointNet.Companion.toDomain
import com.olga.avshister.rainguard.data.rent_point.RentPointLocalRepository.matchesFilter
import com.olga.avshister.rainguard.domain.filter.Filter
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.rent.Rent
import com.olga.avshister.rainguard.domain.rent.RentPoint

/**
 * Репозиторий для получения данных о точках аренды с удаленного сервера
 */
class RentPointRemoteRepository(val context: Context): RentPointRepository {
    override suspend fun registerRentPoint(rentPoint: RentPoint) {
        TODO("Not yet implemented")
    }

    override suspend fun getRentPoints(): List<RentPoint> {
        return NetworkClient(context).apiService.getRentPoints().toDomain()
    }

    override suspend fun getRentPointById(id: Long): RentPoint? {
        TODO("Not yet implemented")
    }

    override suspend fun searchProducts(filter: Filter?, rentPointId: Long): List<Product> {
        return NetworkClient(context)
            .apiService
            .getRentPoints()
            .toDomain()
            .find { it.id == rentPointId }
            ?.availableProducts
            ?.filter {
                matchesFilter(product= it, filter = filter)
            } ?: emptyList()
    }

    override suspend fun searchProducts(
        article: Long,
        rentPointId: Long
    ): List<Product> {
        return NetworkClient(context)
            .apiService
            .getRentPoints()
            .toDomain()
            .find { it.id == rentPointId }
            ?.availableProducts
            ?.filter {
                it.article == article
            } ?: emptyList()
    }

    override suspend fun searchProducts(ids: List<Long>, rentPointId: Long): List<Product> {
        return NetworkClient(context)
            .apiService
            .getRentPoints()
            .toDomain()
            .find { it.id == rentPointId }
            ?.availableProducts
            ?.filter {
                ids.contains(it.id)
            } ?: emptyList()
    }

    override fun deleteRentPoint(rentPointId: Long) {
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
        TODO("Not yet implemented")
    }

    override fun getCompletedRents(rentPointId: Long): List<Rent> {
        TODO("Not yet implemented")
    }
}