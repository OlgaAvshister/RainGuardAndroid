package com.olga.avshister.rainguard.presentation.ui.utils

import android.util.Log
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.data.rent_point.RentPointLocalRepository.RENT_POINT_LOCAL_REPOSITORY_TAG
import com.olga.avshister.rainguard.data.rent_point.RentPointLocalRepository.rentPoint1
import com.olga.avshister.rainguard.data.rent_point.RentPointLocalRepository.rentPoint2
import com.olga.avshister.rainguard.data.rent_point.RentPointLocalRepository.rentPoint3
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.products.Product.Companion.withGeneratedArticul
import com.olga.avshister.rainguard.domain.rent.RentPoint
import kotlin.random.Random

object Dataset {
    fun generateRentPoint(): MutableList<RentPoint> {
        return mutableListOf(
            rentPoint1,
            rentPoint2,
            rentPoint3,
        ).apply {
            Log.d(RENT_POINT_LOCAL_REPOSITORY_TAG, "generated rent points: size=${this.size}, rent points: ${this.joinToString(separator = "\n")}")
        }
    }

    fun generateProductsDataset(): List<Product> {
        val products: ArrayList<Product> = arrayListOf()
        repeat(100) { products.add(generateProduct()) }
        Log.d(RENT_POINT_LOCAL_REPOSITORY_TAG, "Umbrellas generated, total=${products.filter { it.productType == Product.ProductType.UMBRELLA }.size}")
        Log.d(RENT_POINT_LOCAL_REPOSITORY_TAG, "Umbrellas by color: " +
                "white=${products.filter { it.productType == Product.ProductType.UMBRELLA && it.color == Product.Colors.WHITE}.size}" +
                "black=${products.filter { it.productType == Product.ProductType.UMBRELLA && it.color == Product.Colors.BLACK}.size}" +
                "red=${products.filter { it.productType == Product.ProductType.UMBRELLA && it.color == Product.Colors.RED}.size}" +
                "yellow=${products.filter { it.productType == Product.ProductType.UMBRELLA && it.color == Product.Colors.YELLOW}.size}" +
                "green=${products.filter { it.productType == Product.ProductType.UMBRELLA && it.color == Product.Colors.GREEN}.size}" +
                "purple=${products.filter { it.productType == Product.ProductType.UMBRELLA && it.color == Product.Colors.PURPLE}.size}"
        )
        Log.d(RENT_POINT_LOCAL_REPOSITORY_TAG, "Raincoats generated, total=${products.filter { it.productType == Product.ProductType.RAINCOAT }.size}")
        Log.d(RENT_POINT_LOCAL_REPOSITORY_TAG, "generated product's dataset: size=${products.size}, products:${products.joinToString(separator = "\n")}")

        return products
    }

    private fun generateProduct(): Product {
        fun getImageResource(productType: Product.ProductType, colors: Product.Colors): Int {
            var resId: Int = -1

            when (productType) {
                Product.ProductType.UMBRELLA -> {
                    resId = when (colors) {
                        Product.Colors.RED -> {
                            R.drawable.ic_umbrella_red
                        }

                        Product.Colors.YELLOW -> {
                            R.drawable.ic_umbrella_yellow
                        }

                        Product.Colors.WHITE -> {
                            R.drawable.ic_umbrella_white
                        }

                        Product.Colors.GREEN -> {
                            R.drawable.ic_umbrella_green
                        }

                        Product.Colors.BLACK -> {
                            R.drawable.ic_umbrella_black
                        }

                        Product.Colors.PURPLE -> {
                            R.drawable.ic_umbrella_purple
                        }
                    }
                }

                Product.ProductType.RAINCOAT -> {
                    resId = when (colors) {
                        Product.Colors.RED -> {
                            R.drawable.ic_raincoat_red
                        }
                        Product.Colors.YELLOW -> {
                            R.drawable.ic_raincoat_yellow
                        }
                        else -> {
                            throw IllegalArgumentException("Недопустимый цвет для дождевика")
                        }
                    }
                }
            }
            return resId
        }

        fun generateProductType(): Product.ProductType {
            return Product.ProductType.values().random()
        }

        fun generateProductId(): Long {
            return Random.nextLong(1, 10000)
        }

        fun generateUmbrella(): Product {
            val generatedColor = Product.Colors.values().random()
            return Product(
                id = generateProductId(),
                productType = Product.ProductType.UMBRELLA,
                image = getImageResource(productType = Product.ProductType.UMBRELLA, colors = generatedColor),
                printType = Product.PrintType.values().random(),
                color = generatedColor,
                formFactor = listOf(
                    Product.FormFactor.FOLDING,
                    Product.FormFactor.STICK,
                ).random(),
                size = null,
            ).withGeneratedArticul()
        }

        fun generateRaincoat(): Product {
            val generatedColor = listOf(Product.Colors.YELLOW, Product.Colors.RED).random()
            return Product(
                id = generateProductId(),
                productType = Product.ProductType.RAINCOAT,
                image = getImageResource(productType = Product.ProductType.RAINCOAT, colors = generatedColor),
                printType = Product.PrintType.values().random(),
                color = generatedColor,
                formFactor = listOf(
                    Product.FormFactor.JACKET,
                    Product.FormFactor.RAINCOAT,
                ).random(),
                size = Product.Size.values().random(),
            ).withGeneratedArticul()
        }

        return when (generateProductType()) {
            Product.ProductType.UMBRELLA -> {
                generateUmbrella()
            }
            Product.ProductType.RAINCOAT -> {
                generateRaincoat()
            }
        }
    }
}