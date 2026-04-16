package com.olga.avshister.rainguard.presentation.ui.utils

import android.util.Log
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.rent.Rate
import kotlin.math.ceil

object Utils {
    fun millisToHumanTime(timeInMillis: Long): String {
        val hours = timeInMillis / 3600000
        val minutes = (timeInMillis % 3600000) / 60000
        val seconds = (timeInMillis % 60000) / 1000

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun calculateCost(timeInMillis: Long, rate: Rate, productsCount: Int): Int {
        Log.d("CALCULATE_COST", "timeInMillis=$timeInMillis, rate=$rate, productsCount=$productsCount")
        val millisInMinute = 60000.toDouble()
        val minutesInHour = 60.toDouble()
        val hoursInDay = 24.toDouble()
        val minutes = (timeInMillis.toDouble() / millisInMinute)
        val hours = minutes / minutesInHour
        val days = hours / hoursInDay

        val cost = when(rate) {
            Rate.PER_MINUTE -> {
                // округляем до большего числа, умножаем на количество товаров, взятых в аренду,
                // умножаем на цену согласно тарифу
                (ceil(minutes) * productsCount * rate.priceValue).toInt()
            }
            Rate.PER_HOUR -> {
                (ceil(hours) * productsCount * rate.priceValue).toInt()
            }
            Rate.PER_DAY -> {
                (ceil(days) * productsCount * rate.priceValue).toInt()
            }
        }
        Log.d("CALCULATE_COST", "cost=$cost")
        return cost
    }
}
