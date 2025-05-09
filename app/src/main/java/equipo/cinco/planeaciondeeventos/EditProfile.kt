package equipo.cinco.planeaciondeeventos

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EditProfile : AppCompatActivity() {

    // Constantes
    private val REQUEST_IMAGE_GET = 1
    private val CLOUD_NAME = "dq1hkmqeg"
    private val UPLOAD_PRESET = "profile-preset"

    // Uri temporal
    private var imageUri: Uri? = null

    private lateinit var profileImage: ImageView
    private lateinit var overlay: View
    private lateinit var cameraIcon: ImageView
    private lateinit var editName: EditText
    private lateinit var btnActualizar: Button
    private lateinit var btnRegresar: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa Cloudinary y carga perfil del usuario
        initCloudinary()
        loadUserProfile()


        profileImage = findViewById(R.id.iv_profile_image)
        overlay = findViewById(R.id.view_overlay)
        cameraIcon = findViewById(R.id.iv_camera_icon)
        editName = findViewById(R.id.et_name_form)
        btnActualizar = findViewById(R.id.b_update)
        btnRegresar = findViewById(R.id.bt_regresar)


        profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }

        btnActualizar.setOnClickListener {
            if (imageUri != null) {
                uploadImageToCloudinary()
            } else {
                saveUserProfileToFirebase(null)
            }
        }

        btnRegresar.setOnClickListener {
            finish()
        }
    }

    private fun initCloudinary() {
        val config = mutableMapOf<String, String>()
        config["cloud_name"] = CLOUD_NAME
        MediaManager.init(this, config)
    }

    //Recibe la imagen seleccionada del usuario.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            imageUri?.let {
                profileImage.setImageURI(it)
                overlay.visibility = View.VISIBLE
                cameraIcon.visibility = View.VISIBLE
            }
        }
    }

    // Sube la imagen seleccionada a Cloudinary
    private fun uploadImageToCloudinary() {
        MediaManager.get().upload(imageUri)
            .unsigned(UPLOAD_PRESET)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val imageUrl = resultData["secure_url"] as String
                    saveUserProfileToFirebase(imageUrl)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    Toast.makeText(applicationContext, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            }).dispatch()
    }

    private fun saveUserProfileToFirebase(imageUrl: String?) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseDatabase.getInstance().getReference("Users")
        val name = editName.text.toString()

        val userData = mutableMapOf<String, Any>()
        userData["name"] = name
        if (imageUrl != null) {
            userData["profileImage"] = imageUrl
        }

        db.child(userId).updateChildren(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar en Firebase", Toast.LENGTH_SHORT).show()
            }
    }

    //Carga el nombre y foto actual del usuario desde Firebase
    private fun loadUserProfile() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseDatabase.getInstance().getReference("Users").child(userId)

        db.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val name = snapshot.child("name").getValue(String::class.java)
                val imageUrl = snapshot.child("profileImage").getValue(String::class.java)

                editName.setText(name)

                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(imageUrl)
                        .circleCrop()
                        .placeholder(R.drawable.bc_user_photo)
                        .into(profileImage)

                    overlay.visibility = View.VISIBLE
                    cameraIcon.visibility = View.VISIBLE
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "No se pudo cargar el perfil", Toast.LENGTH_SHORT).show()
        }
    }
}

