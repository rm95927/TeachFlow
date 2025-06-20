package fiap.com.edtech

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("usuarios").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val tipo = document.getString("tipo")
                    if (tipo == "aluno") {
                        startActivity(Intent(this, PainelAlunoActivity::class.java))
                    } else {
                        startActivity(Intent(this, PainelProfessorActivity::class.java))
                    }
                    finish()
                }
                .addOnFailureListener {
                    // Caso dê erro no Firestore, manda para o login
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
        } else {
            // Usuário não logado
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
