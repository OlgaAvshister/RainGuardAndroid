package com.olga.avshister.rainguard.presentation.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.yandex.runtime.image.ImageProvider

class TextImageProvider(val context: Context, private val text: String, val drawableResId: Int): ImageProvider() {
    override fun getId(): String {
        return "text_" + text
    }

    override fun getImage(): Bitmap {
        // Получаем исходное изображение из ресурсов
        val drawable =
            ContextCompat.getDrawable(context, drawableResId) ?: return Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            )

        // Указываем размеры для Bitmap (например, оригинальный размер или свой)
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight

        // Создаем пустой Bitmap с нужными размерами
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Устанавливаем изображение из drawable в Canvas
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)

        // Настраиваем Paint для текста
        val paint = Paint().apply {
            color = Color.WHITE
            textSize = 40f // По необходимости подбираем размер
            isAntiAlias = true
        }

        // Измеряем ширину и высоту текста
        val textWidth = paint.measureText(text)
        val fontMetrics = paint.fontMetrics
        val textHeight = fontMetrics.bottom - fontMetrics.top

        // Расчет позиционирования для центрирования текста
        val x = (width - textWidth) / 2
        val y = (height - textHeight) / 2 - fontMetrics.top

        // Рисуем текст по центру
        canvas.drawText(text, x, y, paint)

        return bitmap
    }
}