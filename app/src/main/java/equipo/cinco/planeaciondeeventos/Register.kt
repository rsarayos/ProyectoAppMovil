package equipo.cinco.planeaciondeeventos

import android.os.Bundle
import android.widget.Button
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        

        var button_register : Button = findViewById(R.id.b_register2) as Button
        var button_login : Button = findViewById(R.id.b_login2) as Button

        button_register.setOnClickListener{
            val intent = Intent(this@Register,Login::class.java)
            startActivity(intent)
        }

        button_login.setOnClickListener{
            val intent = Intent(this@Register,Login::class.java)
            startActivity(intent)
        }

    }
}