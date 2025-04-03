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
import com.google.firebase.database.FirebaseDatabase

class ActivityEditEvent : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etCosto: EditText
    private lateinit var btnEditar: Button
    private var eventId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_event)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etNombre = findViewById(R.id.et_name_event)
        etDescripcion = findViewById(R.id.et_description)
        etCosto = findViewById(R.id.et_estimated_price)
        btnEditar = findViewById(R.id.btn_edit)

        val bundle = intent.extras
        eventId = bundle?.getString("id")
        val nombre = bundle?.getString("nombre")
        val descripcion = bundle?.getString("descripcion")
        val costo = bundle?.getFloat("costo")

        etNombre.setText(nombre)
        etDescripcion.setText(descripcion)
        etCosto.setText(costo?.toString() ?: "")

        btnEditar.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val nuevoNombre = etNombre.text.toString().trim()
            val nuevaDesc = etDescripcion.text.toString().trim()
            val nuevoCosto = etCosto.text.toString().toFloatOrNull() ?: 0f

            if (nuevoNombre.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (nuevaDesc.isEmpty()) {
                Toast.makeText(this, "La descripcion no puede estar vacia", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (nuevoCosto < 0) {
                Toast.makeText(this, "El costo no puede ser negativo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ref = FirebaseDatabase.getInstance("https://proyectoappmoviles-150be-default-rtdb.firebaseio.com")
                .getReference("Users").child(uid)
                .child("eventos").child(eventId!!)

            val cambios = mapOf(
                "name" to nuevoNombre,
                "desc" to nuevaDesc,
                "estimated_cost" to nuevoCosto
            )

            ref.updateChildren(cambios).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Evento actualizado correctamente", Toast.LENGTH_SHORT).show()
                    finish() // Regresa a la pantalla anterior
                } else {
                    Toast.makeText(this, "Error al actualizar el evento", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<ImageButton>(R.id.bt_regresar).setOnClickListener {
            finish()
        }

    }
}