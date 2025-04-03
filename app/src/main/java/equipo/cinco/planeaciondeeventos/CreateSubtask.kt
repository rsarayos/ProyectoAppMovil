package equipo.cinco.planeaciondeeventos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CreateSubtask : AppCompatActivity() {

    private lateinit var layoutProductos: LinearLayout
    private lateinit var btnAgregarProducto: ImageButton
    private lateinit var recyclerProductos: RecyclerView
    private lateinit var adapter: ProductAdapter
    private val productos = mutableListOf<Product>()

    private lateinit var nameInput: EditText
    private lateinit var descInput: EditText
    private lateinit var costInput: EditText
    private lateinit var btnGuardar: Button

    private var subtaskId: String? = null
    private var eventId: String? = null
    private var taskId: String? = null
    private var esLectura: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_subtask)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        eventId = intent.getStringExtra("eventId")
        taskId = intent.getStringExtra("taskId")
        subtaskId = intent.getStringExtra("subtaskId")
        esLectura = intent.getBooleanExtra("esLectura", false)

        nameInput = findViewById(R.id.et_name_subtask_form)
        descInput = findViewById(R.id.et_description_form)
        costInput = findViewById(R.id.et_amount_subtask_form)
        btnGuardar = findViewById(R.id.b_create_subtask)
        layoutProductos = findViewById(R.id.layout_productos)
        btnAgregarProducto = findViewById(R.id.bt_agregar_producto)
        recyclerProductos = findViewById(R.id.recyclerProductos)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        if (subtaskId != null) {
            adapter = ProductAdapter(productos, uid, eventId!!, taskId!!, subtaskId!!)
            recyclerProductos.layoutManager = LinearLayoutManager(this)
            recyclerProductos.adapter = adapter
            cargarProductos(uid)
        }

        if (esLectura) {
            nameInput.setText(intent.getStringExtra("name"))
            descInput.setText(intent.getStringExtra("desc"))
            costInput.setText(intent.getFloatExtra("cost", 0f).toString())

            nameInput.isEnabled = false
            descInput.isEnabled = false
            costInput.isEnabled = false
            btnGuardar.visibility = View.GONE
            layoutProductos.visibility = View.VISIBLE
        }

        btnGuardar.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val name = nameInput.text.toString().trim()
            val desc = descInput.text.toString().trim()
            val cost = costInput.text.toString().toFloatOrNull() ?: 0f

            if (name.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (desc.isEmpty()) {
                Toast.makeText(this, "La descripcion es obligatoria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (cost <= 0) {
                Toast.makeText(this, "El costo debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (subtaskId == null) {
                val subtaskRef = FirebaseDatabase.getInstance("https://proyectoappmoviles-150be-default-rtdb.firebaseio.com")
                    .getReference("Users").child(uid)
                    .child("eventos").child(eventId!!)
                    .child("tareas").child(taskId!!)
                    .child("subtareas")

                subtaskId = subtaskRef.push().key!!
                val subtask = SubTaskOb(id = subtaskId, name = name, desc = desc, estimated_cost = cost)

                subtaskRef.child(subtaskId!!).setValue(subtask).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Subtarea guardada. Ahora puedes cotizar productos.", Toast.LENGTH_SHORT).show()
                        layoutProductos.visibility = View.VISIBLE
                        btnGuardar.visibility = View.GONE
                        nameInput.isEnabled = false
                        descInput.isEnabled = false
                        costInput.isEnabled = false

                        adapter = ProductAdapter(productos, uid, eventId!!, taskId!!, subtaskId!!)
                        recyclerProductos.layoutManager = LinearLayoutManager(this)
                        recyclerProductos.adapter = adapter

                        cargarProductos(uid)
                    } else {
                        Toast.makeText(this, "Error al guardar la subtarea", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnAgregarProducto.setOnClickListener {
            val intent = Intent(this, CreateProduct::class.java).apply {
                putExtra("eventId", eventId)
                putExtra("taskId", taskId)
                putExtra("subtaskId", subtaskId)
            }
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (subtaskId != null) {
            cargarProductos(uid)
        }
    }

    private fun cargarProductos(uid: String) {
        val ref = FirebaseDatabase.getInstance("https://proyectoappmoviles-150be-default-rtdb.firebaseio.com")
            .getReference("Users").child(uid)
            .child("eventos").child(eventId!!)
            .child("tareas").child(taskId!!)
            .child("subtareas").child(subtaskId!!)
            .child("productos")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productos.clear()
                for (snap in snapshot.children) {
                    val p = snap.getValue(Product::class.java)
                    p?.let { productos.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CreateSubtask, "Error cargando productos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    class ProductAdapter(private val productos: List<Product>,
                         private val uid: String,
                         private val eventId: String,
                         private val taskId: String,
                         private val subtaskId: String) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

        class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val card: MaterialCardView = itemView.findViewById(R.id.producto_card)
            val tvNombre = itemView.findViewById<TextView>(R.id.tv_nombre_producto)
            val tvLugar = itemView.findViewById<TextView>(R.id.tv_lugar_producto)
            val tvPrecio = itemView.findViewById<TextView>(R.id.tv_precio_producto)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.producto_view, parent, false)
            return ProductViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            val producto = productos[position]

            holder.tvNombre.text = producto.nombre
            holder.tvLugar.text = producto.lugar
            holder.tvPrecio.text = "$${String.format("%.2f", producto.precio ?: 0f)}"

            val color = if (producto.seleccionado) R.color.backgroundButton else R.color.background
            holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, color))

            holder.itemView.setOnClickListener {
                producto.seleccionado = !producto.seleccionado

                val nuevoColor = if (producto.seleccionado) R.color.backgroundButton else R.color.background
                holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, nuevoColor))

                val ref = FirebaseDatabase.getInstance("https://proyectoappmoviles-150be-default-rtdb.firebaseio.com")
                    .getReference("Users").child(uid)
                    .child("eventos").child(eventId)
                    .child("tareas").child(taskId)
                    .child("subtareas").child(subtaskId)
                    .child("productos").child(producto.id!!)

                ref.child("seleccionado").setValue(producto.seleccionado)
            }
        }

        override fun getItemCount(): Int = productos.size
    }

}