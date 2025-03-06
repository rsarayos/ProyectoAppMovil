package equipo.cinco.planeaciondeeventos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActivityEditEvent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_event)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var button_regresar : ImageButton = findViewById(R.id.bt_regresar) as ImageButton
        var button_actualizar : Button = findViewById(R.id.btn_edit)

        button_actualizar.setOnClickListener{
            val intent = Intent(this@ActivityEditEvent, MainActivity::class.java)
            startActivity(intent)
        }

        button_regresar.setOnClickListener{
            val intent = Intent(this@ActivityEditEvent, MainActivity::class.java)
            startActivity(intent)
        }

    }
}