package equipo.cinco.planeaciondeeventos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ChangePassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var button_regresar : ImageButton = findViewById(R.id.b_mock_back) as ImageButton
        var button_actualizar : Button = findViewById(R.id.b_change_password) as Button

        button_actualizar.setOnClickListener{
            val intent = Intent(this@ChangePassword,Configuration::class.java)
            startActivity(intent)
        }

        button_regresar.setOnClickListener{
            val intent = Intent(this@ChangePassword,Configuration::class.java)
            startActivity(intent)
        }

    }
}