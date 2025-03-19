package equipo.cinco.planeaciondeeventos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        val email: EditText = findViewById(R.id.et_email_form)
        val password: EditText = findViewById(R.id.et_password_form)
        var login_button: Button = findViewById(R.id.b_login) as Button
        var button_register: Button = findViewById(R.id.b_register) as Button
        val error: TextView = findViewById(R.id.textError)

        login_button.setOnClickListener{
            if(email.text.isEmpty() || password.text.isEmpty()){
                showError("Por favor, rellene todos los campos",true)
            }else{
                login(email.text.toString(),password.text.toString())
            }
        }

        button_register.setOnClickListener{
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)

        }

    }

    fun goToMain(user: FirebaseUser){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("user", user.email)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }

    fun showError(text:String = "",visible:Boolean){
        val error: TextView = findViewById(R.id.textError)
        error.text = text
        error.visibility = if(visible) TextView.VISIBLE else TextView.INVISIBLE
    }


    fun login(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    goToMain(user!!)
                } else {
                    showError("Error al iniciar sesión",true)
                }
            }
    }


}