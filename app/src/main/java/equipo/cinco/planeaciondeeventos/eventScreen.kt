package equipo.cinco.planeaciondeeventos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class eventScreen : AppCompatActivity() {

    lateinit var tv_nombre_evento: TextView
    lateinit var tv_descripcion_evento: TextView
    var eventId: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private val tareas = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_event_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tv_nombre_evento = findViewById(R.id.tv_event_name)
        tv_descripcion_evento = findViewById(R.id.tv_event_desc)

        var bundle = intent.extras

        if (bundle != null) {
            eventId = bundle.getString("id")
            tv_nombre_evento.setText(bundle.getString("nombre"))
            tv_descripcion_evento.setText(bundle.getString("descripcion"))
        }

        recyclerView = findViewById(R.id.recycler_tareas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(tareas,eventId!!, this)
        recyclerView.adapter = adapter

        cargarTareas()

        var button_resumen : ImageButton = findViewById(R.id.summary_icon) as ImageButton
        var button_regresar : ImageButton = findViewById(R.id.bt_regresar) as ImageButton
        var button_agregar : ImageButton = findViewById(R.id.fab_add) as ImageButton

        button_agregar.setOnClickListener {
            val intent = Intent(this, createTask::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }

    }

    private fun cargarTareas() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val tareasRef = FirebaseDatabase.getInstance("https://proyectoappmoviles-150be-default-rtdb.firebaseio.com")
            .getReference("Users").child(uid).child("eventos").child(eventId!!).child("tareas")

        tareasRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tareas.clear()
                for (tareaSnapshot in snapshot.children) {
                    val tarea = tareaSnapshot.getValue(Task::class.java)
                    tarea?.id = tareaSnapshot.key
                    tarea?.let { tareas.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@eventScreen, "Error cargando tareas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    class TaskAdapter(private val tareas: List<Task>,
                      private val eventId: String,
                      private val context: Context
    ) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

        class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvNombre: TextView = itemView.findViewById(R.id.tv_nombreTarea)
            val tvDescripcion: TextView = itemView.findViewById(R.id.tv_descripcionTarea)
            val layoutHeader: LinearLayout = itemView.findViewById(R.id.layoutTareaHeader)
            val expandButton: ImageButton = itemView.findViewById(R.id.ivExpand)
            val recyclerSubtareas: RecyclerView = itemView.findViewById(R.id.recycler_SubTareas)
            val tvTotal: TextView = itemView.findViewById(R.id.tv_totalCantidad)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
            return TaskViewHolder(view)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            val tarea = tareas[position]
            holder.tvNombre.text = tarea.name
            holder.tvDescripcion.text = tarea.desc

            holder.tvNombre.setOnClickListener {
                val visible = holder.layoutHeader.visibility == View.VISIBLE
                holder.layoutHeader.visibility = if (visible) View.GONE else View.VISIBLE
            }

            holder.expandButton.setOnClickListener {
                val intent = Intent(context, SubTasks::class.java)
                intent.putExtra("taskId", tarea.id)
                intent.putExtra("taskName", tarea.name)
                intent.putExtra("taskDesc", tarea.desc)
                intent.putExtra("eventId", eventId)
                context.startActivity(intent)
            }

            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val subtareaRef = FirebaseDatabase.getInstance("https://proyectoappmoviles-150be-default-rtdb.firebaseio.com")
                .getReference("Users").child(uid)
                .child("eventos").child(eventId)
                .child("tareas").child(tarea.id!!)
                .child("subtareas")

            val listaSubtareas = mutableListOf<SubTaskOb>()
            val subAdapter = SubTaskAdapter(listaSubtareas)
            holder.recyclerSubtareas.layoutManager = LinearLayoutManager(context)
            holder.recyclerSubtareas.adapter = subAdapter

            subtareaRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaSubtareas.clear()
                    var total = 0f
                    for (snap in snapshot.children) {
                        val sub = snap.getValue(SubTaskOb::class.java)
                        sub?.let {
                            listaSubtareas.add(it)
                            total += it.estimated_cost ?: 0f
                        }
                    }
                    holder.tvTotal.text = "$${String.format("%.2f", total)}"
                    subAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error al cargar subtareas", Toast.LENGTH_SHORT).show()
                }
            })
        }

        override fun getItemCount(): Int = tareas.size
    }

    class SubTaskAdapter(private val subtareas: List<SubTaskOb>) : RecyclerView.Adapter<SubTaskAdapter.SubTaskViewHolder>() {

        class SubTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val checkBox: CheckBox = itemView.findViewById(R.id.cbSubtarea)
            val precio: TextView = itemView.findViewById(R.id.tvCostoSubtarea)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.sub_task, parent, false)
            return SubTaskViewHolder(view)
        }

        override fun onBindViewHolder(holder: SubTaskViewHolder, position: Int) {
            val subtask = subtareas[position]
            holder.checkBox.text = subtask.name
            holder.precio.text = "$${String.format("%.2f", subtask.estimated_cost ?: 0f)}"
        }

        override fun getItemCount(): Int = subtareas.size
    }
}