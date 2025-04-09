package equipo.cinco.planeaciondeeventos.utilities

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import equipo.cinco.planeaciondeeventos.R
import kotlin.math.cos
import kotlin.math.sin

class CustomCircleDrawable(
    val context: Context,
    val categorias: ArrayList<CategoriaCosto>
) : Drawable() {

    private var coordenadas: RectF? = null
    private var anguloInicio: Float = 0.0F

    override fun draw(canvas: Canvas) {
        val ancho: Float = canvas.width.toFloat()
        val alto: Float = canvas.height.toFloat()
        coordenadas = RectF(0f, 0f, ancho, alto)

        val centroX = canvas.width / 2f
        val centroY = canvas.height / 2f
        val radioTexto = (canvas.width / 3f)

        for (categoria in categorias) {
            val anguloBarrido: Float = (categoria.porcentaje * 360) / 100

            val baseColor = ContextCompat.getColor(context, categoria.color)

            val seccion = Paint().apply {
                style = Paint.Style.FILL
                isAntiAlias = true
                color = baseColor
            }

            canvas.drawArc(coordenadas!!, anguloInicio, anguloBarrido, true, seccion)

            if (categoria.porcentaje>= 1) {
                val anguloCentro = Math.toRadians((anguloInicio + anguloBarrido / 2).toDouble())
                val x = (centroX + radioTexto * cos(anguloCentro)).toFloat()
                val y = (centroY + radioTexto * sin(anguloCentro)).toFloat()

                val textoColor = if (isColorDark(baseColor)) Color.WHITE else Color.BLACK
                val texto = "${categoria.nombre} ${categoria.total.toInt()} (${String.format("%.1f", categoria.porcentaje)}%)"

                val textoPaint = Paint().apply {
                    color = textoColor
                    textSize = 26f
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                }

                canvas.drawText(texto, x, y, textoPaint)
            }

            anguloInicio += anguloBarrido
        }
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}
    override fun getOpacity(): Int = PixelFormat.OPAQUE

    private fun isColorDark(color: Int): Boolean {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        val brightness = (r * 299 + g * 587 + b * 114) / 1000
        return brightness < 128
    }
}
