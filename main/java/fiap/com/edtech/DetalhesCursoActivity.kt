package fiap.com.edtech

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import fiap.com.edtech.ui.theme.EdtechTheme

class DetalhesCursoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cursoId = intent.getStringExtra("cursoId") ?: ""         // ← novo
        val nomeIntent = intent.getStringExtra("nome") ?: "Sem nome"
        val descIntent = intent.getStringExtra("descricao") ?: "Sem descrição"
        val imgIntent = intent.getStringExtra("imagemUrl") ?: ""

        setContent {
            EdtechTheme {
                TelaDetalhesCurso(
                    cursoId = cursoId,
                    nomeInitial = nomeIntent,
                    descricaoInitial = descIntent,
                    imagemUrlInitial = imgIntent
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDetalhesCurso(
    cursoId: String,
    nomeInitial: String,
    descricaoInitial: String,
    imagemUrlInitial: String
) {
    val context = LocalContext.current
    val db = remember { FirebaseFirestore.getInstance() }

    // Estados locais que podem ser atualizados ao buscar o doc (p. ex. se veio desatualizado do Intent)
    var nome by remember { mutableStateOf(nomeInitial) }
    var descricao by remember { mutableStateOf(descricaoInitial) }
    var imagemUrl by remember { mutableStateOf(imagemUrlInitial) }

    // Carrega/atualiza os dados do curso pelo id (se veio)
    LaunchedEffect(cursoId) {
        if (cursoId.isNotBlank()) {
            db.collection("cursos").document(cursoId).get()
                .addOnSuccessListener { doc ->
                    doc.getString("nome")?.let { nome = it }
                    doc.getString("descricao")?.let { descricao = it }
                    doc.getString("imagemUrl")?.let { imagemUrl = it }
                }
        }
    }

    // Cores da identidade visual EdTech
    val primaryBlue = Color(0xFF42A5F5)
    val darkBlue = Color(0xFF1976D2)
    val lightGrey = Color(0xFFF0F4F8)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = nome,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryBlue)
            )
        },
        containerColor = lightGrey
    ) { innerPadding ->

        // LISTA de avaliações (stream em tempo real)
        val avaliacoes = remember { mutableStateListOf<Avaliacao>() }

        LaunchedEffect(cursoId) {
            if (cursoId.isNotBlank()) {
                db.collection("cursos").document(cursoId)
                    .collection("avaliacoes")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) return@addSnapshotListener
                        avaliacoes.clear()
                        snapshot?.documents?.forEach { doc ->
                            avaliacoes += Avaliacao(
                                id = doc.id,
                                uid = doc.getString("uid") ?: "",
                                texto = doc.getString("texto") ?: "",
                                autorEmail = doc.getString("autorEmail") ?: "",
                                timestamp = doc.getTimestamp("timestamp")
                            )
                        }
                    }
            }
        }

        // Campo de avaliação
        var textoAvaliacao by remember { mutableStateOf("") }

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
                        .padding(bottom = 16.dp),
                    alignment = Alignment.Center
                )
            }

            Text(
                text = nome,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = darkBlue
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = descricao,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.DarkGray),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Botão "Começar agora"
            Button(
                onClick = { /* TODO: abrir o conteúdo do curso */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue, contentColor = Color.White),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Começar agora", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão "Salvar"
            OutlinedButton(
                onClick = {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        val curso = mapOf("nome" to nome, "descricao" to descricao, "imagemUrl" to imagemUrl)
                        db.collection("usuarios").document(user.uid)
                            .collection("cursosSalvos")
                            .document(cursoId.ifBlank { null ?: "" }) // se tiver id, usa o id como doc
                            .set(curso)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Curso salvo com sucesso!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Falha ao salvar: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(context, "Você precisa estar logado para salvar cursos.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = primaryBlue
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp, brush = SolidColor(primaryBlue)),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Salvar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            // ======= Avaliações =======
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Avaliações dos alunos",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Lista simples (pode trocar por LazyColumn se preferir)
            if (avaliacoes.isEmpty()) {
                Text("Ainda não há avaliações.", color = Color.Gray, modifier = Modifier.fillMaxWidth())
            } else {
                // Use LazyColumn se esperar muitas avaliações
                Column(modifier = Modifier.fillMaxWidth()) {
                    avaliacoes.forEach { av ->
                        Text(
                            text = if (av.autorEmail.isNotBlank()) av.autorEmail else "Anônimo",
                            style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
                        )
                        Text(
                            text = av.texto,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de texto para nova avaliação
            OutlinedTextField(
                value = textoAvaliacao,
                onValueChange = { textoAvaliacao = it },
                label = { Text("Escreva aqui sua avaliação") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                minLines = 4
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user == null) {
                        Toast.makeText(context, "Você precisa estar logado para avaliar.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (cursoId.isBlank()) {
                        Toast.makeText(context, "Id do curso não encontrado.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (textoAvaliacao.isBlank()) {
                        Toast.makeText(context, "Digite sua avaliação.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val avaliacao = mapOf(
                        "uid" to user.uid,
                        "autorEmail" to (user.email ?: ""),
                        "texto" to textoAvaliacao.trim(),
                        "timestamp" to FieldValue.serverTimestamp()
                    )

                    db.collection("cursos").document(cursoId)
                        .collection("avaliacoes")
                        .add(avaliacao)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Avaliação enviada! 🎉", Toast.LENGTH_SHORT).show()
                            textoAvaliacao = ""
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Erro ao enviar avaliação: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue, contentColor = Color.White)
            ) {
                Text("Enviar avaliação", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
