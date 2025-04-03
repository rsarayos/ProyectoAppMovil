package equipo.cinco.planeaciondeeventos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
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

class MainActivity : AppCompatActivity() {

    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser?.uid
    private val userRef = FirebaseDatabase.getInstance("https://proyectoappmoviles-150be-default-rtdb.firebaseio.com")
        .getReference("Users").child(uid!!).child("eventos")
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private val eventos = mutableListOf<Event>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var button_addevent : ImageButton = findViewById(R.id.fab_add) as ImageButton

        recyclerView = findViewById(R.id.recyclerEventos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = EventAdapter(eventos) { evento ->

            val intent = Intent(this, eventScreen::class.java)
            intent.putExtra("id", evento.id)
            intent.putExtra("nombre", evento.name)
            intent.putExtra("descripcion", evento.desc)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        cargarEventosDesdeFirebase()


//        var button_event : Button = findViewById(R.id.bt_agregarEv) as Button
        var button_profil : ImageButton = findViewById(R.id.summary_icon) as ImageButton
//        var button_more_option : ImageButton = findViewById(R.id.btn_more_options) as ImageButton

        button_addevent.setOnClickListener{
            val intent = Intent(this@MainActivity,NewEventActivity::class.java)
            startActivity(intent)
        }

        button_profil.setOnClickListener{
            val intent = Intent(this@MainActivity,Configuration::class.java)
            startActivity(intent)
        }

    }

    private fun cargarEventosDesdeFirebase() {
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventos.clear()
                for (eventoSnapshot in snapshot.children) {
                    val evento = eventoSnapshot.getValue(Event::class.java)
                    evento?.id = eventoSnapshot.key
                    evento?.let { eventos.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error cargando eventos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    class EventAdapter(
        private val eventos: List<Event>,
        private val onClick: (Event) -> Unit
    ) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

        class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val botonEvento: Button = itemView.findViewById(R.id.bt_ir_evento)
            val botonOpciones: ImageButton = itemView.findViewById(R.id.btn_more_options)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.event_view, parent, false)
            return EventViewHolder(view)
        }

        override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
            val evento = eventos[position]
            holder.botonEvento.text = evento.name
            holder.botonEvento.setOnClickListener { onClick(evento) }

            holder.botonOpciones.setOnClickListener { view ->
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.menuInflater.inflate(R.menu.event_menu_option, popupMenu.menu)

                try {
                    val popupField = PopupMenu::class.java.getDeclaredField("mPopup")
                    popupField.isAccessible = true
                    val menuPopupHelper = popupField.get(popupMenu)
                    val setForceShowIconMethod = menuPopupHelper.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    setForceShowIconMethod.isAccessible = true
                    setForceShowIconMethod.invoke(menuPopupHelper, true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.menu_editar -> {
                            val context = view.context
                            val intent = Intent(context, ActivityEditEvent::class.java)

                            intent.putExtra("id", evento.id)
                            intent.putExtra("nombre", evento.name)
                            intent.putExtra("descripcion", evento.desc)
                            intent.putExtra("costo", evento.estimated_cost)
                            context.startActivity(intent)
                            true
                        }
                        R.id.menu_borrar -> {

                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            val dbRef = FirebaseDatabase.getInstance("https://proyectoappmoviles-150be-default-rtdb.firebaseio.com")
                                .getReference("Users").child(userId!!).child("eventos")

                            dbRef.child(evento.id!!).removeValue()
                            true
                        }
                        else -> false
                    }
                }

                popupMenu.show()
            }

        }

        override fun getItemCount(): Int = eventos.size
    }

}