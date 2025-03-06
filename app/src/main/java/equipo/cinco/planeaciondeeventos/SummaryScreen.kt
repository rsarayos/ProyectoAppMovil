package equipo.cinco.planeaciondeeventos

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class SummaryScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_summary_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var button_regresar : ImageButton = findViewById(R.id.bt_regresar) as ImageButton
        val pieChart = findViewById<PieChart>(R.id.pie_chart)

        // Datos estáticos para el gráfico de pastel
        val entries = listOf(
            PieEntry(40f, "Ventas"),
            PieEntry(25f, "Marketing"),
            PieEntry(20f, "Producción"),
            PieEntry(15f, "Logística")
        )

        // Configuración del conjunto de datos
        val dataSet = PieDataSet(entries, "Distribución de Presupuesto")
        dataSet.colors = listOf(
            Color.BLUE,
            Color.RED,
            Color.GREEN,
            Color.MAGENTA
        ) // Colores para cada segmento
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 12f

        // Asignar datos al gráfico
        val pieData = PieData(dataSet)
        pieChart.data = pieData

        // Personalización del gráfico
        val description = Description()
        description.text = "Distribución de Presupuesto Anual"
        pieChart.description = description
        pieChart.setEntryLabelColor(Color.BLACK) // Color de los textos en el gráfico
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setUsePercentValues(true) // Mostrar valores como porcentajes
        pieChart.animateY(1000) // Animación de entrada
        pieChart.invalidate() // Refrescar el gráfico

        button_regresar.setOnClickListener{
            val intent = Intent(this@SummaryScreen,eventScreen::class.java)
            startActivity(intent)
        }

    }
}