package com.olga.avshister.rainguard.presentation.screens.owner

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.domain.rent.Rent
import com.olga.avshister.rainguard.presentation.ui.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FinancialDialog(
    financialData: List<Rent>,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())

    val groupedByDate = financialData
        .filter { it.finishedAt != null }
        .groupBy {
            val date = Date(it.finishedAt!!)
            dateFormat.format(date)
        }
        .toSortedMap()

    val dates = groupedByDate.keys.toList()

    // todo: высчитать на основе startedAt и completedAt, сейчас просто перемножаем тариф на количество товаров
    val revenue = groupedByDate.values.map { it.sumOf { rent -> rent.rate.priceValue * rent.productIds.size }.toLong() }
    val salesCount = groupedByDate.values.map { it.size }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.owner_finance)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.owner_revenue_by_day),
                            style = MaterialTheme.typography.titleSmall
                        )
                        BarChartRevenue(
                            dates = dates,
                            revenue = revenue
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.owner_sales_number_by_day),
                            style = MaterialTheme.typography.titleSmall
                        )
                        LineChartSales(
                            dates = dates,
                            salesCount = salesCount
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
fun BarChartRevenue(
    dates: List<String>,
    revenue: List<Long>
) {
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                val entries = revenue.mapIndexed { index, value ->
                    BarEntry(index.toFloat(), value.toFloat())
                }

                val dataSet = BarDataSet(entries, context.getString (R.string.owner_revenue)).apply {
                    color = Color.BLUE
                    valueTextColor = Color.BLACK
                    valueTextSize = 10f
                }

                val barData = BarData(dataSet)
                this.data = barData

                xAxis.valueFormatter = IndexAxisValueFormatter(dates)
                xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.labelRotationAngle = -45f
                xAxis.textSize = 10f

                axisLeft.setDrawGridLines(true)
                axisRight.isEnabled = false

                description.isEnabled = false
                legend.isEnabled = true

                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}

@Composable
fun LineChartSales(
    dates: List<String>,
    salesCount: List<Int>
) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                val entries = salesCount.mapIndexed { index, count ->
                    Entry(index.toFloat(), count.toFloat())
                }

                val dataSet = LineDataSet(entries, context.getString(R.string.owner_sales_number)).apply {
                    color = Color.GREEN
                    setCircleColor(Color.GREEN)
                    circleRadius = 4f
                    valueTextColor = Color.BLACK
                    valueTextSize = 10f
                    setDrawFilled(true)
                    fillColor = Color.GREEN
                    fillAlpha = 50
                }

                val lineData = LineData(dataSet)
                this.data = lineData

                xAxis.valueFormatter = IndexAxisValueFormatter(dates)
                xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.labelRotationAngle = -45f
                xAxis.textSize = 10f

                axisLeft.setDrawGridLines(true)
                axisRight.isEnabled = false

                description.isEnabled = false
                legend.isEnabled = true

                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}