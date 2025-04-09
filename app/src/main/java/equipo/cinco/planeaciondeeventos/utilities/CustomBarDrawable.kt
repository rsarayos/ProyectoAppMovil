package equipo.cinco.planeaciondeeventos.utilities

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import equipo.cinco.planeaciondeeventos.R

class CustomBarDrawable(
    val context: Context,
    val categoria: CategoriaCosto
) : Drawable() {

    private var coordenadas: RectF? = null

    override fun draw(canvas: Canvas) {
        val fondo = Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            color = ContextCompat.getColor(context, R.color.gray)
        }

        val ancho = (canvas.width - 10).toFloat()
        val alto = (canvas.height - 10).toFloat()
        coordenadas = RectF(0.0F, 0.0F, ancho, alto)
        canvas.drawRect(coordenadas!!, fondo)

        val porcentaje = categoria.porcentaje * ancho / 100
        val seccion = Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            color = ContextCompat.getColor(context, categoria.color)
        }

        val coordenadas2 = RectF(0.0F, 0.0F, porcentaje, alto)
        canvas.drawRect(coordenadas2, seccion)
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}
    override fun getOpacity(): Int = PixelFormat.OPAQUE
}
