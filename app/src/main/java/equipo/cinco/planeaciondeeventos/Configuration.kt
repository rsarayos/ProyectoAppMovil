package equipo.cinco.planeaciondeeventos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Configuration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_configuration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var button_editar : Button = findViewById(R.id.bt_editarPerfil) as Button
        var button_cambiar : Button = findViewById(R.id.bt_cambiarContraseña) as Button
        var button_regresar : ImageButton = findViewById(R.id.bt_regresar) as ImageButton
        var button_cerrar : Button = findViewById(R.id.bt_cerrarSesion) as Button

        button_cambiar.setOnClickListener{
            val intent = Intent(this@Configuration,ChangePassword::class.java)
            startActivity(intent)
        }

        button_editar.setOnClickListener{
            val intent = Intent(this@Configuration,EditProfile::class.java)
            startActivity(intent)
        }

        button_cerrar.setOnClickListener{
            val intent = Intent(this@Configuration,Login::class.java)
            startActivity(intent)
        }

        button_regresar.setOnClickListener{
            val intent = Intent(this@Configuration,MainActivity::class.java)
            startActivity(intent)
        }

    }
}