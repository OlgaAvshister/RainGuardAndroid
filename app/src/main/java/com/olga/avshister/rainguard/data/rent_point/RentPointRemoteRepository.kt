package com.olga.avshister.rainguard.data.rent_point

import android.content.Context
import com.olga.avshister.rainguard.data.common.PrefsRepository
import com.olga.avshister.rainguard.data.common.PrefsRepositoryImpl
import com.olga.avshister.rainguard.data.common.PrefsRepositoryImpl.Companion.KEY_FINISH_RENT_POINT_ID
import com.olga.avshister.rainguard.data.common.PrefsRepositoryImpl.Companion.KEY_START_RENT_POINT_ID
import com.olga.avshister.rainguard.data.network.NetworkClient
import com.olga.avshister.rainguard.data.network.rentPoint.RentPointNet.Companion.toDomain
import com.olga.avshister.rainguard.data.rent_point.RentPointLocalRepository.matchesFilter
import com.olga.avshister.rainguard.domain.filter.Filter
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.rent.Rent
import com.olga.avshister.rainguard.domain.rent.Rent.Companion.toNet
import com.olga.avshister.rainguard.domain.rent.RentPoint

/**
 * Репозиторий для получения данных о точках аренды с удаленного сервера
 */
class RentPointRemoteRepository(val context: Context): RentPointRepository {
    private val prefs: PrefsRepository = PrefsRepositoryImpl(context)
    val apiService = NetworkClient(context).apiService

    override suspend fun setStartRentPointId(id: Long) {
        prefs.setLong(KEY_START_RENT_POINT_ID, id)
    }

    override suspend fun getStartRentPointId(): Long {
        return prefs.getLong(KEY_START_RENT_POINT_ID, -1)
    }

    override suspend fun setFinishRentPointId(id: Long) {
        prefs.setLong(KEY_FINISH_RENT_POINT_ID, id)
    }

    override suspend fun getFinishRentPointId(): Long {
        return prefs.getLong(KEY_FINISH_RENT_POINT_ID, -1)
    }

    override suspend fun registerRentPoint(rentPoint: RentPoint) {
        TODO("Not yet implemented")
    }

    override suspend fun getRentPoints(): List<RentPoint> {
        return apiService.getRentPoints().toDomain()
    }

    override suspend fun getRentPointById(id: Long): RentPoint? {
        TODO("Not yet implemented")
    }

    override suspend fun searchProducts(filter: Filter?, rentPointId: Long): List<Product> {
        return apiService
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
        return apiService
            .getRentPoints()
            .toDomain()
            .find { it.id == rentPointId }
            ?.availableProducts
            ?.filter {
                it.article == article
            } ?: emptyList()
    }

    override suspend fun searchProducts(ids: List<Long>, rentPointId: Long): List<Product> {
        val rentPoint = apiService.getRentPoints().toDomain().find { it.id == rentPointId }
        val availableProducts = rentPoint?.availableProducts
        val foundProducts = availableProducts?.filter { product ->
            ids.contains(product.id)
        }

        return foundProducts ?: emptyList()
    }

    override fun deleteRentPoint(rentPointId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun startRent(rent: Rent) {
        apiService.startRent(rent.toNet())
    }

    override suspend fun finishRent(rent: Rent) {
        apiService.finishRent(rent.toNet())
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