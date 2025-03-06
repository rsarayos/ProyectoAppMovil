package equipo.cinco.planeaciondeeventos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var login_button: Button = findViewById(R.id.b_login) as Button
        var button_register: Button = findViewById(R.id.b_register) as Button

        login_button.setOnClickListener{
            val intent = Intent(this@Login,MainActivity::class.java)
            startActivity(intent)
        }

        button_register.setOnClickListener{
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)

        }

    }


}