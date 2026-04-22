package com.olga.avshister.rainguard.domain.products

import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.data.network.product.ProductNet
import kotlin.math.absoluteValue

data class Product(
    val id: Long, // инвентарный номер
    val productType: ProductType, // зонт/дождевик
    val article: Long = -1, // поле, которое однозначно закрепляет за собой набор характеристик (несколько товаров с одинаковыми атрибутами должны иметь одинаковый артикул)
    val printType: PrintType, // есть принт/нет принта
    val color: Colors,
    val formFactor: FormFactor, // FOLDING (складывающийся)/STICK (трость) для зонта; JACKET (куртка)/ FULLBODY_RAINCOAT для дождевика на всё тело
    val size: Size?, // только для дождевика
    val condition: ProductCondition? = ProductCondition.READY // состояние товара
) {

    enum class ProductType (val value: String) {
        UMBRELLA ("Зонт"), RAINCOAT ("Дождевик")
    }

    enum class Colors(val value: String) {
        RED("Красный"), YELLOW("Желтый"), WHITE("Белый"),
        GREEN("Зеленый"), BLACK("Черный"), PURPLE("Фиолетовый")
    }
    enum class PrintType(val value: String) {
        WITH_PRINT("С принтом"),
        WITHOUT_PRINT("Без принта")
    }
    enum class FormFactor(val value: String) {
        JACKET("Куртка"),
        RAINCOAT("На все тело"),
        FOLDING("Складной"),
        STICK("Трость")
    }

    enum class Size(val value: String) {
        XS ("XS"), S ("S"), M ("M"), L (""), XL ("XL")
    }

    /**
     * Данный класс используется для записи состояния товара при сдаче клиентом на пункт приема,
     * а также при выводе аналитики для владельца
     */
    enum class ProductCondition(val valueStuff: String, val valueOwner: String) {
        READY(valueStuff = "Все хорошо", valueOwner = "Чистые/готовые"),
        DIRTY(valueStuff = "Нужна стирка", valueOwner = "Грязные"),
        BROKEN(valueStuff = "Нужно заменить", valueOwner = "Неисправные"),
        BOUGHT(valueStuff = "Выкуплен", valueOwner = "Выкуплен"),
    }


    fun getImageResource(): Int {
        var resId: Int = -1

        when (productType) {
            ProductType.UMBRELLA -> {
                resId = when (color) {
                    Colors.RED -> {
                        R.drawable.ic_umbrella_red
                    }

                    Colors.YELLOW -> {
                        R.drawable.ic_umbrella_yellow
                    }

                    Colors.WHITE -> {
                        R.drawable.ic_umbrella_white
                    }

                    Colors.GREEN -> {
                        R.drawable.ic_umbrella_green
                    }

                    Colors.BLACK -> {
                        R.drawable.ic_umbrella_black
                    }

                    Colors.PURPLE -> {
                        R.drawable.ic_umbrella_purple
                    }
                }
            }

            ProductType.RAINCOAT -> {
                resId = when (color) {
                    Colors.RED -> {
                        R.drawable.ic_raincoat_red
                    }
                    Colors.YELLOW -> {
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

    companion object {
        /**
         * Extension-функция для генерации артикула
         *
         * Все продукты с одинаковыми характеристиками (кроме id)
         * будут иметь одинаковый article.
         */
        fun Product.generateArticle(): Long {
            val key = buildString {
                append(productType.name)
                append("|")
                append(printType.name)
                append("|")
                append(color.name)
                append("|")
                append(formFactor.name)
                append("|")
                append(size?.name ?: "NO_SIZE")
            }.lowercase()

            return key.hashCode().toLong().absoluteValue
        }

        /**
         * Возвращает копию товара с заполненным артикулом
         */
        fun Product.withGeneratedArticle(): Product {
            return this.copy(
                article = generateArticle()
            )
        }

        /**
         * Разбиваем на группы таким образом, чтобы в каталоге выводились только товары с разными артикулами, повторов быть не должно
         */
        fun List<Product>.toSetByArticle(): List<Product> {
            return this.distinctBy { it.article }
        }

        fun Product.toNet(): ProductNet {
            return ProductNet(
                id = id,
                productType = productType,
                article = article,
                printType = printType,
                color = color,
                formFactor = formFactor,
                size = size,
                condition = condition
            )
        }
    }
}