package com.olga.avshister.rainguard.presentation.ui.utils

import android.util.Log
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.data.rent_point.RentPointLocalRepository.RENT_POINT_LOCAL_REPOSITORY_TAG
import com.olga.avshister.rainguard.data.rent_point.RentPointLocalRepository.rentPoint1
import com.olga.avshister.rainguard.data.rent_point.RentPointLocalRepository.rentPoint2
import com.olga.avshister.rainguard.data.rent_point.RentPointLocalRepository.rentPoint3
import com.olga.avshister.rainguard.domain.payment.Card
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.products.Product.Companion.withGeneratedArticul
import com.olga.avshister.rainguard.domain.rent.Rate
import com.olga.avshister.rainguard.domain.rent.Rent
import com.olga.avshister.rainguard.domain.rent.RentPoint
import kotlin.math.ceil
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
                printType = Product.PrintType.values().random(),
                color = generatedColor,
                formFactor = listOf(
                    Product.FormFactor.FOLDING,
                    Product.FormFactor.STICK,
                ).random(),
                size = null,
                condition = Product.ProductCondition.values().random()
            ).withGeneratedArticul()
        }

        fun generateRaincoat(): Product {
            val generatedColor = listOf(Product.Colors.YELLOW, Product.Colors.RED).random()
            return Product(
                id = generateProductId(),
                productType = Product.ProductType.RAINCOAT,
                printType = Product.PrintType.values().random(),
                color = generatedColor,
                formFactor = listOf(
                    Product.FormFactor.JACKET,
                    Product.FormFactor.RAINCOAT,
                ).random(),
                size = Product.Size.values().random(),
                condition = Product.ProductCondition.values().random()
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

    // Метод для генерации данных о завершенных арендах (для проверки корректности отработки фронта)
    fun generateCompletedRents(): List<Rent> {
        val rents = mutableListOf<Rent>()
        val now = System.currentTimeMillis()
        val tenDaysInMillis = 10L * 24 * 60 * 60 * 1000
        val tenDaysAgo = now - tenDaysInMillis

        // Генерируем от 5 до 10 аренд
        val rentsCount = Random.nextInt(5, 11)

        repeat(rentsCount) {
            // Генерируем случайное время начала аренды за последние 10 дней
            val startedAt = Random.nextLong(tenDaysAgo, now)

            // Генерируем продолжительность аренды от 1 минуты до 3 дней
            val durationMinutes = when (Random.nextInt(1, 4)) {
                1 -> Random.nextInt(1, 60) // от 1 до 60 минут
                2 -> Random.nextInt(60, 24 * 60) // от 1 до 24 часов
                else -> Random.nextInt(24 * 60, 72 * 60) // от 1 до 3 дней
            }

            val durationMillis = durationMinutes * 60 * 1000L
            val completedAt = startedAt + durationMillis

            // Генерируем случайный тариф
            val rate = when (Random.nextInt(0, 3)) {
                0 -> Rate.PER_MINUTE
                1 -> Rate.PER_HOUR
                else -> Rate.PER_DAY
            }

            // Генерируем количество товаров от 1 до 5
            val productsCount = Random.nextInt(1, 6)
            val products = generateRandomProducts(productsCount)

            // Создаем случайную карту
            val card = generateRandomCard()

            // Генерируем случайного клиента
            val customerId = Random.nextLong(1000, 10000)

            rents.add(
                Rent(
                    startedAt = startedAt,
                    finishedAt = completedAt,
                    productIds = products.map { it.id },
                    cardNumber = card.number,
                    rate = rate
                )
            )
        }

        // Сортируем по времени завершения (от новых к старым)
        return rents.sortedByDescending { it.finishedAt }
    }

    /**
     * Генерация случайного списка товаров
     */
    private fun generateRandomProducts(count: Int): List<Product> {
        val products = mutableListOf<Product>()

        repeat(count) {
            products.add(generateProduct())
        }

        return products
    }



    /**
     * Генерация случайной карты
     */
    private fun generateRandomCard(): Card {
        val cardNumber = generateRandomCardNumber()
        val expired = generateRandomExpiryDate()
        val cvv = Random.nextInt(100, 999)

        return Card(
            number = cardNumber,
            expired = expired,
            cvv = cvv
        )
    }

    /**
     * Генерация случайного номера карты
     */
    private fun generateRandomCardNumber(): String {
        val prefixes = listOf("4532", "4916", "5280", "6011", "3782")
        val prefix = prefixes.random()
        val remaining = (1..12).map { Random.nextInt(0, 10) }.joinToString("")
        return "$prefix$remaining"
    }

    /**
     * Генерация случайной даты истечения срока (от 2024 до 2028)
     */
    private fun generateRandomExpiryDate(): String {
        val month = Random.nextInt(1, 13).toString().padStart(2, '0')
        val year = Random.nextInt(24, 29)
        return "$month/$year"
    }

    /**
     * Возвращает случайный ID изображения для зонта
     */
    private fun getRandomImageForUmbrella(): Int {
        val umbrellaImages = listOf(
            R.drawable.ic_umbrella_black,
            R.drawable.ic_umbrella_red,
            R.drawable.ic_umbrella_white,
            R.drawable.ic_umbrella_green
        )
        return umbrellaImages.random()
    }

    /**
     * Возвращает случайный ID изображения для дождевика
     */
    private fun getRandomImageForRaincoat(): Int {
        val raincoatImages = listOf(
            R.drawable.ic_raincoat_yellow,
            R.drawable.ic_raincoat_red,
        )
        return raincoatImages.random()
    }

    // Расширение для расчета стоимости аренды
    fun Rent.calculateTotalCost(): Int {
        val durationMillis = finishedAt!! - startedAt
        val durationMinutes = durationMillis / (60 * 1000.0)

        val billedUnits = when (rate) {
            Rate.PER_MINUTE -> ceil(durationMinutes).toInt()
            Rate.PER_HOUR -> ceil(durationMinutes / 60.0).toInt()
            Rate.PER_DAY -> ceil(durationMinutes / (60.0 * 24)).toInt()
        }

        return billedUnits * rate.priceValue * productIds.size
    }

    // Альтернативная версия с более реалистичными данными
    fun generateCompletedRentsAdvanced(): List<Rent> {
        val rents = mutableListOf<Rent>()
        val now = System.currentTimeMillis()
        val tenDaysInMillis = 10L * 24 * 60 * 60 * 1000
        val tenDaysAgo = now - tenDaysInMillis

        val rentsCount = Random.nextInt(5, 11)

        // Реалистичные распределения для разных сценариев
        val rentScenarios = listOf(
            RentScenario.SHORT_SHOPPING,    // 15-30 минут
            RentScenario.MOVIE,              // 2-3 часа
            RentScenario.WALK,               // 1-2 часа
            RentScenario.WORK_DAY,           // 8-10 часов
            RentScenario.WEEKEND_TRIP        // 2-3 дня
        )

        repeat(rentsCount) {
            val scenario = rentScenarios.random()
            val startedAt = Random.nextLong(tenDaysAgo, now)

            // Продолжительность в зависимости от сценария
            val durationMinutes = when (scenario) {
                RentScenario.SHORT_SHOPPING -> Random.nextInt(15, 31)
                RentScenario.MOVIE -> Random.nextInt(120, 181)
                RentScenario.WALK -> Random.nextInt(60, 121)
                RentScenario.WORK_DAY -> Random.nextInt(480, 601)
                RentScenario.WEEKEND_TRIP -> Random.nextInt(2880, 4321) // 2-3 дня
            }

            val completedAt = startedAt + durationMinutes * 60 * 1000L

            // Тариф в зависимости от продолжительности
            val rate = when (durationMinutes) {
                in 0..60 -> Rate.PER_MINUTE
                in 61..1440 -> Rate.PER_HOUR
                else -> Rate.PER_DAY
            }

            // Количество товаров в зависимости от сценария
            val productsCount = when (scenario) {
                RentScenario.SHORT_SHOPPING -> Random.nextInt(1, 3)
                RentScenario.MOVIE, RentScenario.WALK -> Random.nextInt(1, 4)
                RentScenario.WORK_DAY -> Random.nextInt(2, 5)
                RentScenario.WEEKEND_TRIP -> Random.nextInt(2, 6)
            }

            val products = generateRandomProducts(productsCount)

            // Генерируем случайного клиента с повторяющимися ID для реалистичности
            val customerId = listOf(1001L, 1002L, 1003L, 1004L, 1005L).random()

            val card = generateRandomCard()

            rents.add(
                Rent(
                    startedAt = startedAt,
                    finishedAt = completedAt,
                    productIds = products.map { it.id },
                    cardNumber = card.number,
                    rate = rate
                )
            )
        }

        return rents.sortedByDescending { it.finishedAt }
    }

    enum class RentScenario {
        SHORT_SHOPPING,
        MOVIE,
        WALK,
        WORK_DAY,
        WEEKEND_TRIP
    }
}