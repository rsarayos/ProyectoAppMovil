import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import equipo.cinco.planeaciondeeventos.R

class CustomCircleDrawable : Drawable {

    var coordenadas: RectF? = null
    var anguloBarrido: Float = 0.0F
    var anguloInicio: Float = 0.0F
    var grosorMetrica: Int = 0
    var grosorFondo: Int = 0
    var context: Context? = null
//    var emociones = ArrayList<Emociones>()

    constructor(context: Context){
//        , emociones: ArrayList<Emociones>
        this.context = context
        grosorMetrica = 10
        grosorFondo = 15
//        this.emociones = emociones
        grosorMetrica = context.resources.getDimensionPixelSize(R.dimen.graphWith)

    }

    override fun draw(canvas: Canvas) {
        val fondo: Paint = Paint()
        fondo.style = Paint.Style.STROKE
        fondo.strokeWidth = (this.grosorFondo).toFloat()
        fondo.isAntiAlias = true
        fondo.strokeCap = Paint.Cap.ROUND
        fondo.color = context?.resources?.getColor(R.color.gray) ?: R.color.gray
        val ancho: Float = (canvas.width -25).toFloat()
        val alto: Float = (canvas.height - 25).toFloat()

        coordenadas = RectF(25.0F, 25.0F, ancho, alto)

        canvas.drawArc(coordenadas!!, 0.0F, 360.0F, false, fondo)

//        if (emociones.size != 0){
//            for (e in emociones){
//                val degree: Float = (e.porcentaje*360)/100
//                this.anguloBarrido = degree
//
//                var seccion : Paint = Paint()
//                seccion.style = Paint.Style.STROKE
//                seccion.isAntiAlias = true
//                seccion.strokeWidth = (this.grosorMetrica).toFloat()
//                seccion.strokeCap = Paint.Cap.SQUARE
//                seccion.color = ContextCompat.getColor(this.context!!, e.color)
//
//                canvas.drawArc(coordenadas!!, this.anguloInicio, this.anguloBarrido, false, seccion)
//                this.anguloInicio += this.anguloBarrido
//            }
//        }
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

}