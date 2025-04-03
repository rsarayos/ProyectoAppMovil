package equipo.cinco.planeaciondeeventos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreateProduct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_product)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etNombre = findViewById<EditText>(R.id.et_nombre_producto)
        val etLugar = findViewById<EditText>(R.id.et_lugar_producto)
        val etPrecio = findViewById<EditText>(R.id.et_costo_producto)
        val btnAgregar = findViewById<Button>(R.id.btn_agregar_producto)

        val eventId = intent.getStringExtra("eventId")!!
        val taskId = intent.getStringExtra("taskId")!!
        val subtaskId = intent.getStringExtra("subtaskId")!!

        btnAgregar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val lugar = etLugar.text.toString().trim()
            val precio = etPrecio.text.toString().toFloatOrNull() ?: 0f

            if (nombre.isEmpty()) {
                Toast.makeText(this, "Nombre requerido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val ref = FirebaseDatabase.getInstance("https://proyectoappmoviles-150be-default-rtdb.firebaseio.com")
                .getReference("Users").child(uid)
                .child("eventos").child(eventId)
                .child("tareas").child(taskId)
                .child("subtareas").child(subtaskId)
                .child("productos")

            val productId = ref.push().key!!
            val product = Product(productId, nombre, lugar, precio, false)

            ref.child(productId).setValue(product).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

    }
}