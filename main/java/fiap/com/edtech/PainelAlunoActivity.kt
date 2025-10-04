package fiap.com.edtech

import CursosViewModel
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.google.firebase.firestore.FirebaseFirestore
import fiap.com.edtech.ui.theme.EdtechTheme


class PainelAlunoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EdtechTheme {
                TelaPainelAluno()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPainelAluno() {
    val context = LocalContext.current
    var selectedItem by remember { mutableIntStateOf(0) }

    // Cores da identidade visual EdTech
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
                            0 -> "Buscar Cursos"
                            1 -> "Cursos Salvos"
                            2 -> "Perfil"
                            else -> ""
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    // Ícone de "Sair" apenas se não for a tela de busca
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
            NavigationBar(
                containerColor = Color.White, // Fundo branco para a barra de navegação
                contentColor = primaryBlue // Cor dos itens não selecionados
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    label = { Text("Search") },
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryBlue,
                        selectedTextColor = primaryBlue,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = lightGrey // Cor de fundo do item selecionado
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = "Saved") },
                    label = { Text("Saved") },
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryBlue,
                        selectedTextColor = primaryBlue,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = lightGrey
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Settings") },
                    label = { Text("Perfil") },
                    selected = selectedItem == 2,
                    onClick = { selectedItem = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryBlue,
                        selectedTextColor = primaryBlue,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = lightGrey
                    )
                )
            }
        },
        containerColor = lightGrey // Cor de fundo da tela principal
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                0 -> TelaBuscaCurso()
                1 -> TelaCursosSalvos()
                2 -> TelaPerfilAluno(

                    onInformacoesPessoais = {
                        context.startActivity(
                            Intent(context, InformacoesActivity::class.java))

                    },

                    onNotificacoes = {
                        val intent = Intent(context, NotificacaoActivity::class.java)
                        intent.putExtra("tipoUsuario", "aluno") // manda o tipo de usuário
                        context.startActivity(intent)
                    },

                    onTermoAceite = {
                        val intent = Intent(context, TermoAceiteActivity::class.java)
                        context.startActivity(intent)
                    }

                )
            }
        }
    }
}

@Composable
fun TelaBuscaCurso(viewModel: CursosViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    var busca by remember { mutableStateOf("") }
    val cursos by viewModel.cursos.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = busca,
            onValueChange = { busca = it },
            label = { Text("Buscar curso") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        val cursosFiltrados = cursos.filter {
            it.nome.contains(busca, ignoreCase = true)
        }

        if (cursosFiltrados.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhum curso encontrado.")
            }
        } else {
            val context = LocalContext.current
            LazyColumn {
                items(cursosFiltrados) { curso ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                val intent = Intent(context, DetalhesCursoActivity::class.java).apply {
                                    putExtra("cursoId", curso.id)
                                    putExtra("nome", curso.nome)
                                    putExtra("descricao", curso.descricao)
                                    putExtra("imagemUrl", curso.imagemUrl)
                                }
                                context.startActivity(intent)
                            },
                        elevation = cardElevation()
                    ) {
                        Row(modifier = Modifier.padding(8.dp)) {
                            if (curso.imagemUrl.isNotBlank()) {
                                Image(
                                    painter = rememberAsyncImagePainter(curso.imagemUrl),
                                    contentDescription = curso.nome,
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(curso.nome, style = MaterialTheme.typography.titleMedium)
                                Text(curso.descricao, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCursosSalvos(modifier: Modifier = Modifier) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    var cursosSalvos by remember { mutableStateOf<List<Curso>>(emptyList()) }
    val context = LocalContext.current // Adicionado para exibir Toasts

    // Cores da identidade visual EdTech
    val primaryBlue = Color(0xFF42A5F5) // Azul principal vibrante
    val darkBlue = Color(0xFF1976D2) // Azul mais escuro para destaque
    val lightGrey = Color(0xFFF0F4F8) // Cinza claro para fundos
    val mediumGrey = Color(0xFFE0E0E0) // Um cinza médio para cards ou placeholders
    val darkRed = Color(0xFF941A1A)

    // Refatorei a lógica de carregamento em uma lambda para reuso
    val loadCursos = {
        if (user != null) {
            db.collection("usuarios")
                .document(user.uid)
                .collection("cursosSalvos")
                .get()
                .addOnSuccessListener { snapshot ->
                    val cursos = snapshot.documents.mapNotNull { doc ->
                        val id = doc.id // <--- Captura o ID do documento AQUI
                        val nome = doc.getString("nome")
                        val descricao = doc.getString("descricao")
                        val imagemUrl = doc.getString("imagemUrl")

                        if (nome != null) {
                            // Cria o objeto Curso com o ID
                            Curso(id, nome, descricao ?: "", imagemUrl?:"")
                        } else {
                            null
                        }
                    }
                    cursosSalvos = cursos
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Erro ao carregar cursos: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            cursosSalvos = emptyList() // Limpa a lista se o usuário deslogar
        }
    }

    LaunchedEffect(Unit) {
        loadCursos() // Chama a função para carregar os cursos
    }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            "Cursos Salvos",
            style = MaterialTheme.typography.headlineMedium.copy( // Usar headlineMedium para um título maior
                fontWeight = FontWeight.Bold,
                color = darkBlue // Cor do título em azul escuro
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Você salvou ${cursosSalvos.size} curso(s)",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Gray // Cor da subdescrição em cinza
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (cursosSalvos.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Nenhum curso salvo ainda.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Salve seus cursos favoritos para vê-los aqui!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cursosSalvos) { curso ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) { // Usamos Box para posicionar os ícones sobrepostos
                            Column(modifier = Modifier.fillMaxSize()) {
                                // Imagem do Curso
                                Image(
                                    painter = rememberAsyncImagePainter(curso.imagemUrl),
                                    contentDescription = curso.nome,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                                        .background(mediumGrey),
                                    contentScale = ContentScale.Crop
                                )

                                // Espaçamento e Conteúdo do Card (Nome e Descrição)
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .weight(1f)
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
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = curso.descricao,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.Gray
                                        ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Ícones de Estrela e Lixeira no canto superior direito
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopEnd) // Alinha a Row ao TopEnd do Box
                                    .padding(8.dp), // Padding para a Row
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Ícone de Estrela (Salvo)
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Salvo",
                                    tint = primaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp)) // Espaçamento entre ícones

                                // Ícone de Lixeira (Excluir)
                                IconButton(
                                    onClick = {
                                        // Lógica de exclusão do curso
                                        if (user != null) {
                                            db.collection("usuarios")
                                                .document(user.uid)
                                                .collection("cursosSalvos")
                                                .document(curso.id) // Usa o ID do curso para deletar
                                                .delete()
                                                .addOnSuccessListener {
                                                    Toast.makeText(context, "Curso removido!", Toast.LENGTH_SHORT).show()
                                                    loadCursos() // Recarrega os cursos após a exclusão
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(context, "Erro ao remover: ${e.message}", Toast.LENGTH_LONG).show()
                                                }
                                        } else {
                                            Toast.makeText(context, "Usuário não logado.", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Excluir curso",
                                        tint = darkRed // Lixeira em vermelho
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPerfilAluno(
    onInformacoesPessoais: () -> Unit = {},
    onNotificacoes: () -> Unit = {},
    onTermoAceite: () -> Unit = {},
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    val darkBlue = Color(0xFF1976D2)
    val lightGrey = Color(0xFFF0F4F8)
    val mediumGrey = Color(0xFFE0E0E0)
    val textColor = Color(0xFF333333)

    var nomeExibicao by remember {
        mutableStateOf(user?.displayName.orEmpty())
    }

    // tenta montar "Nome Sobrenome" vindos do Firestore (usuarios/{uid})
    LaunchedEffect(user?.uid) {
        val uid = user?.uid ?: return@LaunchedEffect
        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                val n = doc.getString("nome").orEmpty()
                val s = doc.getString("sobrenome").orEmpty()
                val junto = listOf(n, s).filter { it.isNotBlank() }.joinToString(" ")
                if (junto.isNotBlank()) nomeExibicao = junto
            }
    }

    val nome = nomeExibicao.ifBlank { "Aluno(a)" }
    val email = user?.email.orEmpty().ifBlank { "email@exemplo.com" }

    Scaffold(
        containerColor = lightGrey,
        // ⬇️ sem TopAppBar/actions → nenhum botão de configurações aqui
        topBar = {}
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(lightGrey)
        ) {
            // Cabeçalho com avatar, nome (maior/destaque) e e-mail abaixo
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
                    contentDescription = "Avatar do Aluno",
                    tint = darkBlue,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(mediumGrey)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Nome com mais destaque (maior + bold)
                Text(
                    text = nome,
                    // headlineMedium > headlineSmall; também dá pra usar titleLarge
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // E-mail menor, secundário
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Menu (mantive os itens que você já tinha; sem "Configurações")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                ProfileMenuItem(
                    text = "Informações Pessoais",
                    icon = Icons.Default.Person,
                    onClick = onInformacoesPessoais
                )
                ProfileMenuItem(
                    text = "Notificações",
                    icon = Icons.Default.Notifications,
                    onClick = onNotificacoes
                )
                ProfileMenuItem(
                    text = "Termo de Aceite",
                    icon = Icons.Default.Info,
                    onClick = onTermoAceite
                )
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit // ← TIPO CORRETO, sem @Composable
) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        ListItem(
            headlineContent = { Text(text) },
            leadingContent = {
                Icon(
                    imageVector = icon,
                    contentDescription = text
                )
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clickable { onClick() } // ← chamada correta
        )
        Spacer(modifier = Modifier.height(8.dp))
        Divider()
    }
}

