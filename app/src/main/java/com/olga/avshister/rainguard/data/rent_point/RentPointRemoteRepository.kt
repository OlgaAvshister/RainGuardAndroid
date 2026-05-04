package com.olga.avshister.rainguard.data.rent_point

import android.content.Context
import com.olga.avshister.rainguard.data.common.PrefsRepository
import com.olga.avshister.rainguard.data.common.PrefsRepositoryImpl
import com.olga.avshister.rainguard.data.common.PrefsRepositoryImpl.Companion.KEY_FINISH_RENT_POINT_ID
import com.olga.avshister.rainguard.data.common.PrefsRepositoryImpl.Companion.KEY_START_RENT_POINT_ID
import com.olga.avshister.rainguard.data.network.NetworkClient
import com.olga.avshister.rainguard.data.network.owner.RegisterProductRequest
import com.olga.avshister.rainguard.data.network.owner.RegisterRentPointRequest
import com.olga.avshister.rainguard.data.network.rent.RentNet.Companion.toDomain
import com.olga.avshister.rainguard.data.network.rentPoint.RentPointNet.Companion.toDomain
import com.olga.avshister.rainguard.data.network.stuff.UpdateConditionRequest
import com.olga.avshister.rainguard.domain.filter.ConcatFilter
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.products.Product.Companion.toNet
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

    override suspend fun registerRentPoint(
        name: String,
        fullAddress: String,
        latitude: Double,
        longitude: Double,
        workHours: String
    ) {
        val request = RegisterRentPointRequest(
            name = name,
            fullAddress = fullAddress,
            latitude = latitude,
            longitude = longitude,
            workHours = workHours
        )
        apiService.registerRentPoint(request)
    }

    override suspend fun getRentPoints(): List<RentPoint> {
        return apiService.getRentPoints().toDomain()
    }

    override suspend fun getRentPointById(id: Long): RentPoint? {
        return runCatching { apiService.getRentPoints().toDomain().first { it.id == id } }.getOrNull()
    }

    override suspend fun searchProducts(filter: ConcatFilter, rentPointId: Long): List<Product> {
        return apiService
            .getRentPoints()
            .toDomain()
            .find { it.id == rentPointId }
            ?.availableProducts
            ?.filter {
                matchesFilter(product = it, filter = filter)
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

    override suspend fun searchProducts(ids: List<Long>, rentPointId: Long?): List<Product> {
        val rentPoints = apiService.getRentPoints().toDomain()
        val allProducts = if (rentPointId == null) {
            rentPoints.flatMap { i ->
                i.availableProducts
            }
        }  else {
            rentPoints
                .find { it.id == rentPointId }
                ?.availableProducts.orEmpty()
        }

        val foundProducts = allProducts.filter { product ->
            ids.contains(product.id)
        }

        return foundProducts
    }

    override suspend fun deleteRentPoint(rentPointId: Long) {
        apiService.deleteRentPoint(rentPointId)
    }

    override suspend fun startRent(rent: Rent) {
        apiService.startRent(rent.toNet())
    }

    override suspend fun finishRent(rent: Rent) {
        apiService.finishRent(rent.toNet())
    }

    override suspend fun addProduct(rentPointId: Long, product: Product) {
        apiService.registerProduct(
            RegisterProductRequest(
                rentPointId = rentPointId, product = product.toNet()
            )
        )
    }

    override suspend fun getCompletedRents(rentPointId: Long): List<Rent> {
        return apiService.getCompletedRents(rentPointId).map { it.toDomain() }
    }

    override suspend fun updateCondition(
        productId: Long,
        condition: Product.ProductCondition
    ) {
        apiService.updateCondition(UpdateConditionRequest(productId, condition))
    }

    /**
     * Если хоть по одному параметру нет совпадения, считаем что товар не соответствует фильтру
     * Если какой-то параметр в фильтре не указан - то считаем, что товар соответствует фильтру
     * и нужно проверить остальные параметры
     */
    fun matchesFilter(product: Product, filter: ConcatFilter): Boolean {
        // если совсем ничего не выбрано, то считаем что пользователь хочет увидеть все товары
        if (filter.umbrellaFilter == null && filter.raincoatFilter == null)
            return true

        when (product.productType) {
            Product.ProductType.UMBRELLA -> {
                // если фильтр для зонта не заполнен, сразу считаем что не соответствует фильтру
                val filter = filter.umbrellaFilter ?: return false

                if (filter.formFactor != null && filter.formFactor != product.formFactor) {
                    return false
                }
                if (filter.printType != null && filter.printType != product.printType) {
                    return false
                }
                if (filter.color != null && filter.color != product.color) {
                    return false
                }
                return true
            }
            Product.ProductType.RAINCOAT -> {
                val filter = filter.raincoatFilter ?: return false

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
    }
}