package equipo.cinco.planeaciondeeventos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var button_addevent : Button = findViewById(R.id.fab_add) as Button
        var button_event : Button = findViewById(R.id.bt_agregarEv) as Button
        var button_profil : ImageButton = findViewById(R.id.summary_icon) as ImageButton

        button_addevent.setOnClickListener{
            val intent = Intent(this@MainActivity,NewEventActivity::class.java)
            startActivity(intent)
        }

        button_event.setOnClickListener{
            val intent = Intent(this@MainActivity,eventScreen::class.java)
            startActivity(intent)
        }

        button_profil.setOnClickListener{
            val intent = Intent(this@MainActivity,Configuration::class.java)
            startActivity(intent)
        }

    }
}