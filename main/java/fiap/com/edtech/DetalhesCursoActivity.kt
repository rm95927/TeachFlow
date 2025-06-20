package fiap.com.edtech

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fiap.com.edtech.ui.theme.EdtechTheme

class DetalhesCursoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nome = intent.getStringExtra("nome") ?: "Sem nome"
        val descricao = intent.getStringExtra("descricao") ?: "Sem descrição"
        val imagemUrl = intent.getStringExtra("imagemUrl") ?: ""

        setContent {
            EdtechTheme {
                TelaDetalhesCurso(nome, descricao, imagemUrl)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDetalhesCurso(nome: String, descricao: String, imagemUrl: String) {

    val context = LocalContext.current

    // Cores da identidade visual EdTech
    val primaryBlue = Color(0xFF42A5F5) // Azul principal vibrante
    val darkBlue = Color(0xFF1976D2) // Azul mais escuro para destaque
    val lightGrey = Color(0xFFF0F4F8) // Cinza claro para fundos

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = nome, // Usando o nome do curso como título
                        color = Color.White, // Título branco
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Correção aqui: Chama finish() na Activity se o contexto for uma Activity
                        (context as? ComponentActivity)?.finish()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White // Ícone de voltar branco
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryBlue // TopAppBar na cor azul principal
                )
            )
        },
        containerColor = lightGrey // Fundo da tela em cinza claro
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (imagemUrl.isNotBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(imagemUrl),
                    contentDescription = nome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 16.dp), // Espaçamento maior após a imagem
                    alignment = Alignment.Center
                )
            }

            Text(
                text = nome, // Adicione o nome do curso como um título na tela principal
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = darkBlue // Nome do curso em azul escuro
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = descricao,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.DarkGray // Descrição em cinza escuro
                ),
                modifier = Modifier.padding(bottom = 24.dp) // Espaçamento antes dos botões
            )

            // Botão "Começar agora"
            Button(
                onClick = { /* Lógica para ir para a tela do curso */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Altura maior para o botão
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryBlue, // Botão na cor azul principal
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium // Borda arredondada
            ) {
                Text("Começar agora", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp)) // Espaçamento entre os botões

            // Botão "Salvar"
            OutlinedButton(
                onClick = {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        val db = FirebaseFirestore.getInstance()
                        val curso = mapOf(
                            "nome" to nome,
                            "descricao" to descricao,
                            "imagemUrl" to imagemUrl
                        )

                        db.collection("usuarios")
                            .document(user.uid)
                            .collection("cursosSalvos")
                            .add(curso)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Curso salvo com sucesso!", Toast.LENGTH_SHORT).show()
                                // Agora redireciona para PainelAlunoActivity e mostra aba "Salvos"
                                val intent = Intent(context, PainelAlunoActivity::class.java).apply {
                                    putExtra("abaSelecionada", 1) // 1 = cursos salvos
                                    // Comentei as flags para não resetar a pilha se não for o objetivo
                                    // flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                context.startActivity(intent)
                                (context as? ComponentActivity)?.finish() // Finaliza esta tela
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Falha ao salvar curso: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(context, "Você precisa estar logado para salvar cursos.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Altura maior para o botão
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent, // Fundo transparente
                    contentColor = primaryBlue // Texto na cor azul principal
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy( // Borda com a cor azul principal
                    width = 2.dp,
                    brush = SolidColor(primaryBlue) // Use SolidColor para o Brush
                ),
                shape = MaterialTheme.shapes.medium // Borda arredondada
            ) {
                Text("Salvar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Remova completamente esta função, ela é desnecessária e causou o erro.
// O finish() correto é chamado diretamente no IconButton.
// private fun Context.finish() {
//     TODO("Not yet implemented")
// }