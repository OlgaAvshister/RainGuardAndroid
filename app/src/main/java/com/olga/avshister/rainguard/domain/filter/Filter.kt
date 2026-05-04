package com.olga.avshister.rainguard.domain.filter

import android.os.Parcelable
import com.olga.avshister.rainguard.domain.products.Product.Colors
import com.olga.avshister.rainguard.domain.products.Product.FormFactor
import com.olga.avshister.rainguard.domain.products.Product.PrintType
import com.olga.avshister.rainguard.domain.products.Product.ProductType
import com.olga.avshister.rainguard.domain.products.Product.Size
import kotlinx.parcelize.Parcelize

data class ConcatFilter(
    val umbrellaFilter: Filter?,
    val raincoatFilter: Filter?
) {
    @Parcelize
    data class Filter(
        val printType: PrintType? = null, // есть принт/нет принта
        val color: Colors? = null,
        val formFactor: FormFactor? = null, // FOLDING (складывающийся)/STICK (трость) для зонта; JACKET (куртка)/ RAINCOAT (для дождевика на всё тело)
        val size: Size? = null, // только для дождевика
    ) : Parcelable
}

