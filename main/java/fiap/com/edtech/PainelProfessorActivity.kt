package fiap.com.edtech

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
                    // Ícone de "Sair" apenas se não for a tela de busca
                    if (selectedItem != 0) {
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
                    label = { Text("Tela de Perfil") },
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
                    if (titulo.isNotBlank() && descricao.isNotBlank()) {
                        val curso = mapOf(
                            "nome" to titulo,
                            "descricao" to descricao,
                            "imagemUrl" to imagemUrl
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

@Composable
fun TelaAvaliacoes() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Você ainda não possui avaliações dos cursos")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPerfilProfessor() {
    val context = LocalContext.current


    val primaryBlue = Color(0xFF42A5F5)
    val darkBlue = Color(0xFF1976D2)
    val lightGrey = Color(0xFFF0F4F8)
    val mediumGrey = Color(0xFFE0E0E0)
    val textColor = Color(0xFF333333)

    Scaffold(

        containerColor = lightGrey
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(lightGrey)
        ) {

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
                    text = "", // Substitua pelo nome real do professor
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                )
                Spacer(modifier = Modifier.height(0.dp))
                Text(
                    text = "", // Substitua pelo email real
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Itens do Menu de Perfil
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                ProfileMenuItem(
                    text = "Informações Pessoais",
                    icon = Icons.Default.Person,
                    onClick = {

                    },
                    textColor = textColor,
                    iconTint = primaryBlue
                )
                ProfileMenuItem(
                    text = "Notificações",
                    icon = Icons.Default.Notifications,
                    onClick = {

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
                ProfileMenuItem(
                    text = "Configurações",
                    icon = Icons.Default.Settings,
                    onClick = {

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


