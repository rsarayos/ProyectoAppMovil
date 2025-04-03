package equipo.cinco.planeaciondeeventos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class createTask : AppCompatActivity() {

    lateinit var et_nombre_tarea: EditText
    lateinit var et_descripcion_tarea: EditText
    lateinit var et_costo_estimado: EditText
    var eventId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        et_nombre_tarea = findViewById(R.id.et_nombreTarea)
        et_descripcion_tarea = findViewById(R.id.et_descripcion)
        et_costo_estimado = findViewById(R.id.et_precio)

        var bundle = intent.extras

        if (bundle != null) {
            eventId = bundle.getString("eventId")
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val tareasRef = FirebaseDatabase.getInstance("https://proyectoappmoviles-150be-default-rtdb.firebaseio.com")
            .getReference("Users").child(userId!!).child("eventos").child(eventId!!).child("tareas")

        var button_regresar: ImageButton = findViewById(R.id.bt_regresar) as ImageButton
        var button_siguiente: Button = findViewById(R.id.bt_siguiente)as Button

        button_siguiente.setOnClickListener{
            val nombre = et_nombre_tarea.text.toString()
            val descripcion = et_descripcion_tarea.text.toString()
            val costoEstimado = et_costo_estimado.text.toString().toFloat()

            val taskId = tareasRef.push().key!!
            val tarea = Task(taskId, nombre, descripcion, costoEstimado)

            tareasRef.child(taskId).setValue(tarea).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Tarea guardada", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error al guardar tarea", Toast.LENGTH_SHORT).show()
                }
            }
        }

        button_regresar.setOnClickListener{
            finish()
        }

    }
}