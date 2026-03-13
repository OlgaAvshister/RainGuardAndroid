package com.olga.avshister.rainguard.domain.filter

import android.os.Parcelable
import com.olga.avshister.rainguard.domain.products.Product.Colors
import com.olga.avshister.rainguard.domain.products.Product.FormFactor
import com.olga.avshister.rainguard.domain.products.Product.PrintType
import com.olga.avshister.rainguard.domain.products.Product.ProductType
import com.olga.avshister.rainguard.domain.products.Product.Size
import kotlinx.parcelize.Parcelize

@Parcelize
data class Filter(
    val productType: ProductType?, // зонт/дождевик
    val printType: PrintType?, // есть принт/нет принта
    val color: Colors?,
    val formFactor: FormFactor?, // FOLDING (складывающийся)/STICK (трость) для зонта; JACKET (куртка)/ FULLBODY_RAINCOAT для дождевика на всё тело
    val size: Size?, // только для дождевика
) : Parcelable