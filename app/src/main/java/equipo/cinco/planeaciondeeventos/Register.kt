package equipo.cinco.planeaciondeeventos

import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        val email: EditText = findViewById(R.id.et_email_form)
        val password: EditText = findViewById(R.id.et_create_password_form)
        val confirmPassword: EditText = findViewById(R.id.et_confirm_password_form)
        val error: TextView = findViewById(R.id.textError)
        val usuario: EditText = findViewById(R.id.et_name_form)
        var button_register : Button = findViewById(R.id.b_register2) as Button
        var button_login : Button = findViewById(R.id.b_login2) as Button

        button_register.setOnClickListener{
            if (email.text.isEmpty()||password.text.isEmpty()||confirmPassword.text.isEmpty()||usuario.text.isEmpty()){
                error.text = "Todos los campos deben ser llenados"
                error.visibility = View.VISIBLE
                return@setOnClickListener
            }
            if (!password.text.toString().equals(confirmPassword.text.toString())){
                error.text = "Las contraseñas no coinciden"
                error.visibility = View.VISIBLE
                return@setOnClickListener
            }
            error.visibility = View.INVISIBLE
            signIn(email.text.toString(),password.text.toString())
        }

        button_login.setOnClickListener{
            val intent = Intent(this@Register,Login::class.java)
            startActivity(intent)
        }

    }
    fun signIn(email: String, password: String){
        Log.d("INFO","Email: $email , Password: $password")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("INFO", "createUserWithEmail:success")
                    val user = auth.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    Log.w("INFO", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "El registro falló.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }
}