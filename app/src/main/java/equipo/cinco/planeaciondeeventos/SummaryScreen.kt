package equipo.cinco.planeaciondeeventos

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import equipo.cinco.planeaciondeeventos.utilities.CategoriaCosto
import equipo.cinco.planeaciondeeventos.utilities.CustomCircleDrawable

class SummaryScreen : AppCompatActivity() {

    private lateinit var graphLayout: ConstraintLayout
    private lateinit var leyendaLayout: LinearLayout
    private lateinit var database: DatabaseReference
    private lateinit var userID: String
    private lateinit var eventID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_screen)

        graphLayout = findViewById(R.id.graph)
        leyendaLayout = findViewById(R.id.leyendaLayout)
        val buttonRegresar: ImageButton = findViewById(R.id.bt_regresar)

        eventID = intent.getStringExtra("eventId") ?: return.also {
            Log.e("DEBUG", "No se recibió el ID del evento en el Intent")
        }
        userID = FirebaseAuth.getInstance().currentUser?.uid ?: return.also {
            Log.e("DEBUG", "Usuario no autenticado")
        }

        database = FirebaseDatabase.getInstance().reference
        cargarCostosPorTarea()

        buttonRegresar.setOnClickListener {
            finish()
        }
    }

    private fun cargarCostosPorTarea() {
        val eventoRef = database.child("Users").child(userID).child("eventos").child(eventID)

        eventoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tareas = snapshot.child("tareas")
                val lista = ArrayList<CategoriaCosto>()
                var totalReal = 0f
                var totalEstimado = 0f

                val coloresTareas = listOf(
                    R.color.orange,
                    R.color.mustard,
                    R.color.greenie,
                    R.color.blue,
                    R.color.deepBlue,
                    R.color.gray
                )

                for (tareaSnapshot in tareas.children) {
                    var totalTareaReal = 0f
                    val nombreTarea = tareaSnapshot.child("name").getValue(String::class.java) ?: "Sin nombre"

                    // Sumar al estimado (lo planeado)
                    val estimado = tareaSnapshot.child("estimated_cost").getValue(Float::class.java) ?: 0f
                    totalEstimado += estimado

                    val subtareas = tareaSnapshot.child("subtareas")
                    for (subSnapshot in subtareas.children) {
                        val productos = subSnapshot.child("productos")
                        for (prodSnapshot in productos.children) {
                            val seleccionado = prodSnapshot.child("seleccionado").getValue(Boolean::class.java) ?: false
                            if (seleccionado) {
                                val precio = prodSnapshot.child("precio").getValue(Float::class.java) ?: 0f
                                totalTareaReal += precio
                            }
                        }
                    }

                    if (totalTareaReal > 0f) {
                        totalReal += totalTareaReal
                        val colorIndex = lista.size % coloresTareas.size
                        val color = coloresTareas[colorIndex]
                        lista.add(CategoriaCosto(nombreTarea, 0f, color, totalTareaReal))
                    }
                }

                // Actualizar TextViews de Totales
                val tvTotalEstimado = findViewById<TextView>(R.id.tv_total_estimated)
                val tvTotalReal = findViewById<TextView>(R.id.tv_total_real)

                tvTotalEstimado.text = "$%.2f".format(totalEstimado)
                tvTotalReal.text = "$%.2f".format(totalReal)

                if (totalReal == 0f) {
                    Toast.makeText(this@SummaryScreen, "No hay productos seleccionados.", Toast.LENGTH_SHORT).show()
                    return
                }

                for (cat in lista) {
                    cat.porcentaje = (cat.total * 100) / totalReal
                }

                graphLayout.background = CustomCircleDrawable(this@SummaryScreen, lista)
                agregarLeyenda(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE", "Error: ${error.message}")
            }
        })
    }



    private fun agregarLeyenda(categorias: List<CategoriaCosto>) {
        leyendaLayout.removeAllViews()

        for (cat in categorias) {
            val fila = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 8, 0, 8)
            }

            val colorView = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(40, 40)
                setBackgroundColor(ContextCompat.getColor(this@SummaryScreen, cat.color))
            }

            val nombreText = TextView(this).apply {
                text = cat.nombre
                setTextColor(Color.WHITE)
                textSize = 16f
                setPadding(16, 0, 0, 0)
            }

            val porcentajeText = TextView(this).apply {
                text = "${cat.porcentaje.toInt()}%"
                setTextColor(Color.LTGRAY)
                textSize = 14f
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.weight = 1f
                params.marginStart = 16
                layoutParams = params
            }

            fila.addView(colorView)
            fila.addView(nombreText)
            fila.addView(porcentajeText)

            leyendaLayout.addView(fila)
        }
    }
}
