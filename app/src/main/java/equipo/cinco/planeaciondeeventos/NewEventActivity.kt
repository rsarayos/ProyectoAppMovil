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

class NewEventActivity : AppCompatActivity() {

    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser?.uid
    private val userRef = FirebaseDatabase.getInstance("https://proyectoappmoviles-150be-default-rtdb.firebaseio.com")
        .getReference("Users").child(uid!!).child("eventos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_event)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var button_regresar : ImageButton = findViewById(R.id.bt_regresar) as ImageButton
        var button_crear : Button = findViewById(R.id.btn_save) as Button

        button_crear.setOnClickListener{
            val name = findViewById<EditText>(R.id.et_name_event).text.toString()
            val desc = findViewById<EditText>(R.id.et_description).text.toString()
            val estimated_cost = findViewById<EditText>(R.id.et_estimated_price).text.toString().toFloat()
            saveEvent(name, desc, estimated_cost)
        }

        button_regresar.setOnClickListener{
            finish()
        }

    }

    private fun saveEvent(name: String, desc: String, estimated_cost: Float){
        val eventId = userRef.push().key!!
        val eventData = Event(name, desc, estimated_cost)
        userRef.child(eventId).setValue(eventData).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Evento guardado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al guardar el evento", Toast.LENGTH_SHORT).show()
            }
        }

    }

}