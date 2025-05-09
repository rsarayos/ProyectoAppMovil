package equipo.cinco.planeaciondeeventos

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class SubTasks : AppCompatActivity() {

    lateinit var tvNombre: TextView
    lateinit var tvDescripcion: TextView
    lateinit var btnCrearSubTask: ImageButton
    var taskId: String? = null
    var eventId: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SubTaskDetailAdapter
    private val subtareas = mutableListOf<SubTaskOb>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sub_tasks)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvNombre = findViewById(R.id.tv_task_name)
        tvDescripcion = findViewById(R.id.tv_task_description)
        btnCrearSubTask = findViewById(R.id.fab_add)

        val bundle = intent.extras
        taskId = bundle?.getString("taskId")
        eventId = bundle?.getString("eventId")
        val name = bundle?.getString("taskName")
        val desc = bundle?.getString("taskDesc")

        tvNombre.text = name
        tvDescripcion.text = desc

        recyclerView = findViewById(R.id.recycler_subtareas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SubTaskDetailAdapter(subtareas, eventId!!, taskId!!)
        recyclerView.adapter = adapter

        cargarSubtareas()

        val btnBack = findViewById<ImageButton>(R.id.bt_regresar)
        btnBack.setOnClickListener {
            finish()
        }

        btnCrearSubTask.setOnClickListener {
            val intent = Intent(this, CreateSubtask::class.java)
            intent.putExtra("taskId", taskId)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }

    }

    private fun cargarSubtareas() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ref = FirebaseDatabase.getInstance("https://proyectoappmoviles-150be-default-rtdb.firebaseio.com")
            .getReference("Users").child(uid)
            .child("eventos").child(eventId!!)
            .child("tareas").child(taskId!!)
            .child("subtareas")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                subtareas.clear()
                for (snap in snapshot.children) {
                    val sub = snap.getValue(SubTaskOb::class.java)
                    sub?.let { subtareas.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SubTasks, "Error al cargar subtareas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    class SubTaskDetailAdapter(
        private val subtareas: List<SubTaskOb>,
        private val eventId: String,
        private val taskId: String
    ) : RecyclerView.Adapter<SubTaskDetailAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val checkBox: CheckBox = itemView.findViewById(R.id.cbTarea)
            val tvNombre: TextView = itemView.findViewById(R.id.tv_nombre_Subtarea)
            val tvDescripcion: TextView = itemView.findViewById(R.id.tv_subTarea_descripcion)
            val tvPrecio: TextView = itemView.findViewById(R.id.tv_totalCantidad)
            val layoutHeader: LinearLayout = itemView.findViewById(R.id.layoutTareaHeader)
            val btnIrDetalle: ImageButton = itemView.findViewById(R.id.ib_ir_detalle_subtarea)
            val btnEliminar: TextView = itemView.findViewById(R.id.eliminarSubTarea)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.sub_task_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val subtarea = subtareas[position]
            holder.tvNombre.text = subtarea.name
            holder.tvDescripcion.text = subtarea.desc
            holder.tvPrecio.text = "$${String.format("%.2f", subtarea.estimated_cost ?: 0f)}"

            val toggle = {
                val visible = holder.layoutHeader.visibility == View.VISIBLE
                holder.layoutHeader.visibility = if (visible) View.GONE else View.VISIBLE
            }

            holder.tvNombre.setOnClickListener { toggle() }
            holder.checkBox.setOnClickListener { toggle() }

            holder.btnIrDetalle.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, CreateSubtask::class.java).apply {
                    putExtra("eventId", eventId)
                    putExtra("taskId", taskId)
                    putExtra("subtaskId", subtarea.id)
                    putExtra("name", subtarea.name)
                    putExtra("desc", subtarea.desc)
                    putExtra("cost", subtarea.estimated_cost)
                    putExtra("esLectura", true)
                }
                context.startActivity(intent)
            }

            holder.btnEliminar.setOnClickListener {
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

                val ref = FirebaseDatabase.getInstance("https://proyectoappmoviles-150be-default-rtdb.firebaseio.com")
                    .getReference("Users")
                    .child(uid)
                    .child("eventos")
                    .child(eventId)
                    .child("tareas")
                    .child(taskId)
                    .child("subtareas")
                    .child(subtarea.id!!)

                ref.removeValue().addOnSuccessListener {
                    Toast.makeText(holder.itemView.context, "Subtarea eliminada", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(holder.itemView.context, "Error al eliminar subtarea", Toast.LENGTH_SHORT).show()
                }
            }

        }

        override fun getItemCount(): Int = subtareas.size
    }


}