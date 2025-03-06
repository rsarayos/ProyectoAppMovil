package equipo.cinco.planeaciondeeventos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SubTasks : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sub_tasks)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var button_regresar : ImageButton = findViewById(R.id.bt_regresar) as ImageButton

        val itemTaskView = findViewById<View>(R.id.item)

        val ivExpand = itemTaskView.findViewById<ImageView>(R.id.ivExpand)
        val layoutTareaHeader = itemTaskView.findViewById<LinearLayout>(R.id.layoutTareaHeader)

        ivExpand.setOnClickListener {
            if (layoutTareaHeader.visibility == View.VISIBLE) {
                layoutTareaHeader.visibility = View.GONE
            } else {
                layoutTareaHeader.visibility = View.VISIBLE
            }
        }

        button_regresar.setOnClickListener{
            val intent = Intent(this@SubTasks,eventScreen::class.java)
            startActivity(intent)
        }
    }
}