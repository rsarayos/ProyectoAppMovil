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

class eventScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_event_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var button_resumen : ImageButton = findViewById(R.id.summary_icon) as ImageButton
        var button_regresar : ImageButton = findViewById(R.id.bt_regresar) as ImageButton
        var button_agregar : ImageButton = findViewById(R.id.fabAdd) as ImageButton


        val itemTaskView = findViewById<View>(R.id.itemBebidas)

        val ivExpand = itemTaskView.findViewById<ImageView>(R.id.ivExpand)
        val ivVisibility = itemTaskView.findViewById<ImageView>(R.id.ivVisibilidad)
        val layoutTareaHeader = itemTaskView.findViewById<LinearLayout>(R.id.layoutTareaHeader)

        ivExpand.setOnClickListener {
            val intent = Intent(this@eventScreen,SubTasks::class.java)
            startActivity(intent)

        }

        ivVisibility.setOnClickListener {
            if (layoutTareaHeader.visibility == View.VISIBLE) {
                layoutTareaHeader.visibility = View.GONE
            } else {
                layoutTareaHeader.visibility = View.VISIBLE
            }
        }

        button_regresar.setOnClickListener{
            val intent = Intent(this@eventScreen,MainActivity::class.java)
            startActivity(intent)
        }

        button_agregar.setOnClickListener{
            val intent = Intent(this@eventScreen,createTask::class.java)
            startActivity(intent)
        }

        button_resumen.setOnClickListener{
            val intent = Intent(this@eventScreen,SummaryScreen::class.java)
            startActivity(intent)
        }


    }
}