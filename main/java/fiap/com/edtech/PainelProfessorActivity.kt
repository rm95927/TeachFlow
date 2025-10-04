package fiap.com.edtech

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import fiap.com.edtech.ui.theme.EdtechTheme


class PainelProfessorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EdtechTheme {
                TelaPainelProfessor()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPainelProfessor() {
    val context = LocalContext.current
    var selectedItem by remember { mutableStateOf(0) }

    val primaryBlue = Color(0xFF42A5F5)
    val darkBlue = Color(0xFF1976D2)
    val lightGrey = Color(0xFFF0F4F8)
    val mediumGrey = Color(0xFFE0E0E0)
    val textColor = Color(0xFF333333)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        // O título da TopAppBar vai mudar conforme a aba selecionada
                        when (selectedItem) {
                            0 -> "Inserir Cursos"
                            1 -> "Avaliações"
                            2 -> "Perfil"
                            else -> ""
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {

                    if (selectedItem != -1) {
                        IconButton(onClick = {
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(context, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Sair",
                                tint = Color.White // Cor do ícone de sair
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryBlue // Cor de fundo da TopAppBar
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Inserir Curso") },
                    label = { Text("Inserir") },
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = "Avaliações") },
                    label = { Text("Avaliações") },
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Configurações") },
                    label = { Text("Configurações") },
                    selected = selectedItem == 2,
                    onClick = { selectedItem = 2 }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                0 -> TelaCadastroCurso()
                1 -> TelaAvaliacoes()
                2 -> TelaPerfilProfessor()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class) // Adicionado para usar TopAppBar e OutlinedTextFieldDefaults.colors
@Composable
fun TelaCadastroCurso() {
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var imagemUrl by remember { mutableStateOf("") }
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    // Cores da identidade visual EdTech (reutilizando as que você já tem ou definiu)
    val primaryBlue = Color(0xFF42A5F5)
    val darkBlue = Color(0xFF1976D2)
    val lightGrey = Color(0xFFF0F4F8)
    val mediumGrey = Color(0xFFE0E0E0)
    val textColor = Color(0xFF333333) // Um cinza escuro para textos

    Scaffold(
        topBar = {

        },
        containerColor = lightGrey // Cor de fundo da tela
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplica o padding do Scaffold
                .background(lightGrey) // Garante que o fundo da coluna também tenha a cor
                .padding(horizontal = 16.dp, vertical = 20.dp), // Padding geral da tela
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                "Preencha os dados do curso:", // Subtítulo
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold, // Um pouco mais pesado
                    color = darkBlue // Cor do título
                )
            )
            Spacer(modifier = Modifier.height(24.dp)) // Aumenta o espaçamento

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título do curso", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Título", tint = darkBlue) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryBlue,
                    unfocusedBorderColor = mediumGrey,
                    cursorColor = primaryBlue,
                    focusedLabelColor = primaryBlue,
                    unfocusedLabelColor = Color.Gray,
                    focusedLeadingIconColor = darkBlue,
                    unfocusedLeadingIconColor = Color.Gray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição do curso", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = "Descrição", tint = darkBlue) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryBlue,
                    unfocusedBorderColor = mediumGrey,
                    cursorColor = primaryBlue,
                    focusedLabelColor = primaryBlue,
                    unfocusedLabelColor = Color.Gray,
                    focusedLeadingIconColor = darkBlue,
                    unfocusedLeadingIconColor = Color.Gray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                minLines = 3,
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = imagemUrl,
                onValueChange = { imagemUrl = it },
                label = { Text("URL da imagem do curso", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Done, contentDescription = "URL da Imagem", tint = darkBlue) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryBlue,
                    unfocusedBorderColor = mediumGrey,
                    cursorColor = primaryBlue,
                    focusedLabelColor = primaryBlue,
                    unfocusedLabelColor = Color.Gray,
                    focusedLeadingIconColor = darkBlue,
                    unfocusedLeadingIconColor = Color.Gray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp)) // Espaçamento maior antes do botão

            Button(
                onClick = {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user == null) {
                        Toast.makeText(context, "Você precisa estar logado.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (titulo.isNotBlank() && descricao.isNotBlank()) {
                        val curso = mapOf(
                            "nome" to titulo,
                            "descricao" to descricao,
                            "imagemUrl" to imagemUrl,
                            "autorUid" to user.uid,                 // <-- importante
                            "autorEmail" to (user.email ?: "")      // <-- opcional
                        )
                        db.collection("cursos").add(curso)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Curso cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                                titulo = ""
                                descricao = ""
                                imagemUrl = ""
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Erro ao cadastrar curso: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Por favor, preencha o título e a descrição.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Altura maior para o botão
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue), // Cor do botão
                shape = RoundedCornerShape(12.dp), // Bordas arredondadas para o botão
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp) // Sombra no botão
            ) {
                Icon(Icons.Default.Add, contentDescription = "Cadastrar", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Cadastrar Curso", // Texto mais formal
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAvaliacoes() {
    val context = LocalContext.current
    val db = remember { FirebaseFirestore.getInstance() }
    val user = FirebaseAuth.getInstance().currentUser

    // Paleta (igual às outras telas)
    val primaryBlue = Color(0xFF42A5F5)
    val darkBlue = Color(0xFF1976D2)
    val lightGrey = Color(0xFFF0F4F8)
    val mediumGrey = Color(0xFFE0E0E0)

    // Estado: cursos do professor
    var cursos by remember { mutableStateOf<List<Curso>>(emptyList()) }

    // Diálogos/estados de interação
    var cursoSelecionado by remember { mutableStateOf<Curso?>(null) }
    var showDialogAvaliacoes by remember { mutableStateOf(false) }
    var showDialogEditar by remember { mutableStateOf(false) }

    // Carrega cursos deste professor
    LaunchedEffect(user?.uid) {
        if (user == null) {
            cursos = emptyList()
            return@LaunchedEffect
        }
        db.collection("cursos")
            .whereEqualTo("autorUid", user.uid)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                cursos = snap.documents.mapNotNull { doc ->
                    val nome = doc.getString("nome") ?: return@mapNotNull null
                    val descricao = doc.getString("descricao") ?: ""
                    val imagemUrl = doc.getString("imagemUrl") ?: ""
                    val autorUid = doc.getString("autorUid") ?: ""
                    Curso(
                        id = doc.id,
                        nome = nome,
                        descricao = descricao,
                        imagemUrl = imagemUrl,
                        autorUid = autorUid
                    )
                }
            }
    }

    Scaffold(
        containerColor = lightGrey,
        topBar = {
            TopAppBar(
                title = { Text("Meus cursos e avaliações") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryBlue)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
        ) {
            if (user == null) {
                Text("Faça login para ver seus cursos.", color = Color.Gray)
                return@Column
            }

            if (cursos.isEmpty()) {
                Text("Você ainda não cadastrou cursos.", color = Color.Gray)
                return@Column
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(cursos) { curso ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = cardColors(containerColor = Color.White),
                        elevation = cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Imagem
                            Image(
                                painter = rememberAsyncImagePainter(model = curso.imagemUrl),
                                contentDescription = curso.nome,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .background(mediumGrey)
                            )

                            // Conteúdo
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = curso.nome,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = darkBlue
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = curso.descricao,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Ações
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        cursoSelecionado = curso
                                        showDialogAvaliacoes = true
                                    },
                                    modifier = Modifier.weight(1f)
                                ) { Text("Notas", fontSize = 13.sp) }

                                Button(
                                    onClick = {
                                        cursoSelecionado = curso
                                        showDialogEditar = true
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                                ) { Text("Editar", color = Color.White) }
                            }
                        }
                    }
                }
            }
        }
    }

    // ===== Dialog: Avaliações =====
    if (showDialogAvaliacoes && cursoSelecionado != null) {
        DialogAvaliacoesCurso(
            curso = cursoSelecionado!!,
            onDismiss = { showDialogAvaliacoes = false }
        )
    }

    // ===== Dialog: Editar Curso =====
    if (showDialogEditar && cursoSelecionado != null) {
        val context = LocalContext.current

        DialogEditarCurso(
            curso = cursoSelecionado!!,
            onSaved = {
                showDialogEditar = false
                Toast.makeText(context, "Curso atualizado!", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showDialogEditar = false }
        )
    }
}

// Apaga documentos de uma coleção em lotes (≤ 500 ops). Usa 450 por segurança e recursão.
private fun deleteCollectionInChunks(
    collRef: CollectionReference,
    batchSize: Int = 450,
    onComplete: (Boolean, String?) -> Unit
) {
    collRef.limit(batchSize.toLong()).get()
        .addOnSuccessListener { snap ->
            if (snap.isEmpty) {
                onComplete(true, null) // terminou
                return@addOnSuccessListener
            }
            val db = collRef.firestore
            val batch = db.batch()
            for (doc in snap.documents) {
                batch.delete(doc.reference)
            }
            batch.commit()
                .addOnSuccessListener {
                    // chama de novo até esvaziar
                    deleteCollectionInChunks(collRef, batchSize, onComplete)
                }
                .addOnFailureListener { e -> onComplete(false, e.message) }
        }
        .addOnFailureListener { e -> onComplete(false, e.message) }
}

// Apaga TODAS as avaliações e depois o documento do curso
private fun deleteCursoComAvaliacoes(
    cursoId: String,
    onComplete: (Boolean, String?) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val avaliacoesRef = db.collection("cursos").document(cursoId).collection("avaliacoes")

    deleteCollectionInChunks(avaliacoesRef) { ok, err ->
        if (!ok) {
            onComplete(false, err)
            return@deleteCollectionInChunks
        }
        db.collection("cursos").document(cursoId)
            .delete()
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }
}


@Composable
private fun DialogEditarCurso(
    curso: Curso,
    onSaved: () -> Unit,      // <-- sem @Composable
    onDismiss: () -> Unit
) {
    val db = remember { FirebaseFirestore.getInstance() }
    val context = LocalContext.current

    var nome by remember { mutableStateOf(curso.nome) }
    var descricao by remember { mutableStateOf(curso.descricao) }
    var imagemUrl by remember { mutableStateOf(curso.imagemUrl) }

    var saving by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!saving) onDismiss() },
        title = { Text("Editar curso") },
        text = {
            Column {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do curso") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = imagemUrl,
                    onValueChange = { imagemUrl = it },
                    label = { Text("URL da imagem") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Botão Excluir (abre confirmação)
                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFB00020) // vermelho
                    ),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Text("Excluir curso")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nome.isBlank() || descricao.isBlank()) {
                        Toast.makeText(context, "Preencha nome e descrição.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    saving = true
                    db.collection("cursos").document(curso.id)
                        .update(
                            mapOf(
                                "nome" to nome,
                                "descricao" to descricao,
                                "imagemUrl" to imagemUrl
                            )
                        )
                        .addOnSuccessListener {
                            saving = false
                            Toast.makeText(context, "Curso atualizado!", Toast.LENGTH_SHORT).show()
                            onSaved()
                        }
                        .addOnFailureListener { e ->
                            saving = false
                            Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                },
                enabled = !saving
            ) {
                Text(if (saving) "Salvando..." else "Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!saving) onDismiss() }) {
                Text("Cancelar")
            }
        }
    )

    if (showDeleteConfirm) {
        var deleting by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!deleting) showDeleteConfirm = false },
            title = { Text("Excluir curso") },
            text = {
                Text(
                    "Tem certeza que deseja excluir o curso \"${curso.nome}\"?\n" +
                            "As avaliações deste curso também serão removidas."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        deleting = true
                        deleteCursoComAvaliacoes(curso.id) { ok, err ->
                            deleting = false
                            if (ok) {
                                Toast.makeText(context, "Curso e avaliações excluídos.", Toast.LENGTH_SHORT).show()
                                showDeleteConfirm = false
                                onSaved() // fecha o diálogo pai (e a lista se atualiza pelo snapshot)
                            } else {
                                Toast.makeText(context, "Erro ao excluir: $err", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    enabled = !deleting,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB00020))
                ) {
                    Text(if (deleting) "Excluindo..." else "Excluir", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { if (!deleting) showDeleteConfirm = false },
                    enabled = !deleting
                ) { Text("Cancelar") }
            }
        )
    }

}


@Composable
private fun DialogAvaliacoesCurso(
    curso: Curso,
    onDismiss: () -> Unit
) {
    val db = remember { FirebaseFirestore.getInstance() }
    var avaliacoes by remember { mutableStateOf<List<Avaliacao>>(emptyList()) }

    // Use DisposableEffect para registrar e remover o listener
    DisposableEffect(curso.id) {
        val registration = db.collection("cursos").document(curso.id)
            .collection("avaliacoes")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                avaliacoes = snap.documents.map { doc ->
                    Avaliacao(
                        id = doc.id,
                        uid = doc.getString("uid") ?: "",
                        texto = doc.getString("texto") ?: "",
                        autorEmail = doc.getString("autorEmail") ?: "",
                        timestamp = doc.getTimestamp("timestamp")
                    )
                }
            }

        onDispose {
            registration.remove() // ← remove o snapshot listener aqui
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Fechar") } },
        title = { Text("Avaliações — ${curso.nome}") },
        text = {
            if (avaliacoes.isEmpty()) {
                Text("Ainda não há avaliações para este curso.", color = Color.Gray)
            } else {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 0.dp, max = 400.dp)
                ) {
                    items(avaliacoes.size) { i ->
                        val av = avaliacoes[i]
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = av.autorEmail.ifBlank { "Anônimo" },
                                style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(av.texto, style = MaterialTheme.typography.bodyMedium)
                            av.timestamp?.let { ts ->
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    ts.toDate().toString(),
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                                )
                            }
                        }
                        Divider()
                    }
                }
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPerfilProfessor() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    val primaryBlue = Color(0xFF42A5F5)
    val darkBlue = Color(0xFF1976D2)
    val lightGrey = Color(0xFFF0F4F8)
    val mediumGrey = Color(0xFFE0E0E0)
    val textColor = Color(0xFF333333)

    // nome exibido (displayName) → tenta melhorar com Firestore (nome + sobrenome)
    var nomeExibicao by remember { mutableStateOf(user?.displayName.orEmpty()) }
    LaunchedEffect(user?.uid) {
        val uid = user?.uid ?: return@LaunchedEffect
        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                val n = doc.getString("nome").orEmpty()
                val s = doc.getString("sobrenome").orEmpty()
                val completo = listOf(n, s).filter { it.isNotBlank() }.joinToString(" ")
                if (completo.isNotBlank()) nomeExibicao = completo
            }
    }

    val nome = nomeExibicao.ifBlank { "Professor" }
    val email = user?.email.orEmpty().ifBlank { "email@exemplo.com" }

    Scaffold(containerColor = lightGrey) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(lightGrey)
        ) {
            // Cabeçalho (avatar, nome em destaque e e-mail abaixo)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar do Professor",
                    tint = darkBlue,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(mediumGrey)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = nome,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Menu de perfil (sem tela de configurações)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                ProfileMenuItem(
                    text = "Informações Pessoais",
                    icon = Icons.Default.Person,
                    onClick = {
                        context.startActivity(
                            Intent(context, InformacoesActivity::class.java))
                    },
                    textColor = textColor,
                    iconTint = primaryBlue
                )
                ProfileMenuItem(
                    text = "Notificações",
                    icon = Icons.Default.Notifications,
                    onClick = {
                        val intent = Intent(context, NotificacaoActivity::class.java)
                        intent.putExtra("tipoUsuario", "professor")
                        context.startActivity(intent)
                    },
                    textColor = textColor,
                    iconTint = primaryBlue
                )
                ProfileMenuItem(
                    text = "Termo de Aceite",
                    icon = Icons.Default.Info,
                    onClick = {
                        val intent = Intent(context, TermoAceiteActivity::class.java)
                        intent.putExtra("tipoUsuario", "professor")
                        context.startActivity(intent)
                    },
                    textColor = textColor,
                    iconTint = primaryBlue
                )
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    textColor: Color = Color.Unspecified,
    iconTint: Color = Color.Unspecified
) {
    Column {
        ListItem(
            headlineContent = {
                Text(
                    text,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
            },
            leadingContent = {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = iconTint
                )
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Avançar",
                    tint = Color.Gray.copy(alpha = 0.7f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Divider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 1.dp,
            color = Color(0xFFE0E0E0)
        )
    }
}


