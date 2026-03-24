package com.olga.avshister.rainguard.presentation.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.olga.avshister.rainguard.presentation.viewmodel.owner.ProductsStatus

@Composable
fun ProductsStatusCard(status: ProductsStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        val readyColor = MaterialTheme.colorScheme.primary
        val dirtyColor = MaterialTheme.colorScheme.secondary
        val brokenColor = MaterialTheme.colorScheme.tertiary

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Круговая диаграмма
            AndroidView(
                factory = { context ->
                    PieChart(context).apply {
                        setUsePercentValues(true)
                        description.isEnabled = false
                        setExtraOffsets(5f, 10f, 5f, 5f)
                        setDrawEntryLabels(false)
                        setDrawHoleEnabled(true)
                        setHoleColor(Color.Transparent.toArgb())
                        holeRadius = 60f
                        transparentCircleRadius = 61f

                        // Анимация
                        animateY(300)

                        // Легенда (у нас своя)
                        legend.isEnabled = false

                        // Данные
                        val entries = ArrayList<PieEntry>().apply {
                            if (status.ready > 0) add(PieEntry(status.ready.toFloat(), "Чистые/готовые"))
                            if (status.dirty > 0) add(PieEntry(status.dirty.toFloat(), "Грязные"))
                            if (status.broken > 0) add(PieEntry(status.broken.toFloat(), "Неисправные"))
                        }

                        val dataSet = PieDataSet(entries, "").apply {
                            colors = listOf(
                                readyColor.toArgb(),
                                dirtyColor.toArgb(),
                                brokenColor.toArgb()
                            )
                            valueTextSize = 12f
                            valueTextColor = Color.Black.toArgb()
                            setDrawValues(true)
                        }

                        data = PieData(dataSet)
                        invalidate()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            // Дополнительная информация
            Column(modifier = Modifier.fillMaxWidth(),
            ) {
                LegendItem("Чистые/готовые", status.ready, readyColor)
                LegendItem("Грязные", status.dirty, dirtyColor)
                LegendItem("Неисправные", status.broken, brokenColor)
            }
        }
    }
}

@Composable
fun LegendItem(label: String, value: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodySmall
        )
    }
}