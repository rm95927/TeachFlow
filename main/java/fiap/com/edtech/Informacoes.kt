package fiap.com.edtech

import android.app.Activity
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import fiap.com.edtech.ui.theme.EdtechTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class InformacoesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EdtechTheme {
                InformacoesScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformacoesScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db = remember { FirebaseFirestore.getInstance() }

    val lightGrey = Color(0xFFF0F4F8)
    val darkBlue = Color(0xFF1976D2)

    var loading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Campos do perfil
    var nome by remember { mutableStateOf("") }
    var sobrenome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(user?.email.orEmpty()) }
    var tipo by remember { mutableStateOf<String?>(null) }
    var fotoUrl by remember { mutableStateOf(user?.photoUrl?.toString()) }
    var dataNascimentoMillis by remember { mutableStateOf<Long?>(null) }

    // DatePicker
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dataNascimentoMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) =
                utcTimeMillis <= System.currentTimeMillis()
            override fun isSelectableYear(year: Int) =
                year in 1900..Calendar.getInstance().get(Calendar.YEAR)
        }
    )

    // Dialogs (segurança)
    var showChangePassword by remember { mutableStateOf(false) }
    var showDeleteAccount by remember { mutableStateOf(false) }

    // Carrega snapshot do Firestore
    LaunchedEffect(user?.uid) {
        if (user == null) {
            errorMsg = "Nenhum usuário logado."
            loading = false
            return@LaunchedEffect
        }
        db.collection("usuarios").document(user.uid)
            .addSnapshotListener { snap, err ->
                loading = false
                if (err != null) {
                    errorMsg = err.message ?: "Erro ao carregar dados."
                    return@addSnapshotListener
                }
                if (snap != null && snap.exists()) {
                    nome = snap.getString("nome").orEmpty()
                    sobrenome = snap.getString("sobrenome").orEmpty()
                    email = user.email.orEmpty() // e-mail vem do Auth
                    tipo = snap.getString("tipo")
                    fotoUrl = snap.getString("fotoUrl") ?: fotoUrl
                    dataNascimentoMillis = (snap.getTimestamp("dataNascimento")?.toDate()?.time)
                } else {
                    // Fallback (Auth)
                    nome = nome.ifBlank { user.displayName.orEmpty() }
                    email = user.email.orEmpty()
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Informações Pessoais") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        containerColor = lightGrey
    ) { padding ->
        when {
            loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            errorMsg != null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { Text(errorMsg!!, color = MaterialTheme.colorScheme.error) }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header (avatar + nome + e-mail)
                    if (!fotoUrl.isNullOrBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(fotoUrl),
                            contentDescription = "Foto do usuário",
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = darkBlue,
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0))
                                .padding(12.dp)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = listOf(nome, sobrenome).filter { it.isNotBlank() }.joinToString(" ")
                            .ifBlank { "Usuário" },
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = email.ifBlank { "email@exemplo.com" },
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(16.dp))

                    // --- DADOS DO CADASTRO ---  (SEM o campo "Tipo de usuário")
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Dados do cadastro", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(12.dp))

                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = nome,
                                    onValueChange = { nome = it },
                                    label = { Text("Nome") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = sobrenome,
                                    onValueChange = { sobrenome = it },
                                    label = { Text("Sobrenome") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = {},
                                label = { Text("E-mail") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = false,
                                singleLine = true
                            )

                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = dataNascimentoMillis?.let { dateFormat.format(Date(it)) } ?: "",
                                onValueChange = {},
                                label = { Text("Data de nascimento") },
                                placeholder = { Text("dd/mm/aaaa") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDatePicker = true },
                                enabled = false,
                                trailingIcon = {
                                    IconButton(onClick = { showDatePicker = true }) {
                                        Icon(Icons.Filled.DateRange, contentDescription = "Selecionar data")
                                    }
                                },
                                singleLine = true
                            )

                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val u = user ?: return@Button
                                    if (nome.isBlank()) {
                                        Toast.makeText(
                                            context,
                                            "Informe o nome.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@Button
                                    }
                                    saving = true
                                    val dados = hashMapOf<String, Any>(
                                        "nome" to nome.trim(),
                                        "sobrenome" to sobrenome.trim(),
                                        "email" to u.email.orEmpty(),
                                    )
                                    dataNascimentoMillis?.let {
                                        dados["dataNascimento"] = Timestamp(Date(it))
                                    }
                                    tipo?.let { dados["tipo"] = it }

                                    db.collection("usuarios").document(u.uid)
                                        .set(dados, SetOptions.merge())
                                        .addOnSuccessListener {
                                            // opcional: sincroniza displayName
                                            val display =
                                                listOf(nome, sobrenome).filter { it.isNotBlank() }
                                                    .joinToString(" ")
                                            val updates =
                                                userProfileChangeRequest { displayName = display }
                                            u.updateProfile(updates)
                                                .addOnCompleteListener {
                                                    saving = false
                                                    Toast.makeText(
                                                        context,
                                                        "Dados atualizados.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        }
                                        .addOnFailureListener {
                                            saving = false
                                            Toast.makeText(
                                                context,
                                                "Falha ao salvar dados.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                },
                                enabled = !saving
                            ) {
                                if (saving) CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                                else Text("Salvar alterações")
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // --- SEGURANÇA ---
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Segurança", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(10.dp))

                            Button(
                                onClick = { showChangePassword = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Alterar senha")
                            }
                        }
                    }

                    Spacer(Modifier.height(1.dp))

                    // --- ZONA DE RISCO --- (cores explícitas para não “sumir”)
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color(0xFFFFEBEE) // vermelho bem claro
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Zona de risco",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFFD32F2F) // vermelho do título
                            )
                            Spacer(Modifier.height(12.dp))

                            Button(
                                onClick = { showDeleteAccount = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD32F2F), // botão vermelho forte
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Excluir conta")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val sel = datePickerState.selectedDateMillis
                    if (sel != null) dataNascimentoMillis = sel
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
    }

    if (showChangePassword) {
        ChangePasswordDialog(
            email = email,
            onDismiss = { showChangePassword = false },
            onChange = { current, newPass ->
                val u = user ?: return@ChangePasswordDialog
                val cred = EmailAuthProvider.getCredential(email, current)
                u.reauthenticate(cred)
                    .addOnSuccessListener {
                        u.updatePassword(newPass)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Senha alterada com sucesso.", Toast.LENGTH_SHORT).show()
                                showChangePassword = false
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Falha ao alterar senha.", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Reautenticação falhou. Verifique a senha atual.", Toast.LENGTH_SHORT).show()
                    }
            }
        )
    }

    if (showDeleteAccount) {
        DeleteAccountDialog(
            email = email,
            onDismiss = { showDeleteAccount = false },
            onConfirmDelete = { currentPassword ->
                val u = user ?: return@DeleteAccountDialog
                val cred = EmailAuthProvider.getCredential(email, currentPassword)
                u.reauthenticate(cred)
                    .addOnSuccessListener {
                        u.delete()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Conta excluída.", Toast.LENGTH_SHORT).show()
                                FirebaseAuth.getInstance().signOut()
                                // vá para a tela de login
                                context.startActivity(Intent(context, LoginActivity::class.java))
                                (context as? Activity)?.finishAffinity()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Falha ao excluir conta.", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Reautenticação falhou. Verifique a senha.", Toast.LENGTH_SHORT).show()
                    }
            }
        )
    }
}

@Composable
private fun ChangePasswordDialog(
    email: String,
    onDismiss: () -> Unit,
    onChange: (currentPassword: String, newPassword: String) -> Unit
) {
    var current by remember { mutableStateOf("") }
    var newP by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    val valid = current.isNotBlank() && newP.length >= 6 && newP == confirm

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Alterar senha") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = {},
                    label = { Text("E-mail") },
                    enabled = false,
                    singleLine = true
                )
                OutlinedTextField(
                    value = current,
                    onValueChange = { current = it },
                    label = { Text("Senha atual") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = newP,
                    onValueChange = { newP = it },
                    label = { Text("Nova senha (≥ 6 caracteres)") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    label = { Text("Confirmar nova senha") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onChange(current, newP) }, enabled = valid) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun DeleteAccountDialog(
    email: String,
    onDismiss: () -> Unit,
    onConfirmDelete: (currentPassword: String) -> Unit
) {
    var current by remember { mutableStateOf("") }
    var typedConfirm by remember { mutableStateOf("") }
    val canDelete = current.isNotBlank() && typedConfirm == "EXCLUIR"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Excluir conta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Esta ação é irreversível. Para confirmar, digite sua senha e escreva EXCLUIR.")
                OutlinedTextField(
                    value = email,
                    onValueChange = {},
                    label = { Text("E-mail") },
                    enabled = false,
                    singleLine = true
                )
                OutlinedTextField(
                    value = current,
                    onValueChange = { current = it },
                    label = { Text("Senha atual") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = typedConfirm,
                    onValueChange = { typedConfirm = it },
                    label = { Text("Digite: EXCLUIR") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirmDelete(current) },
                enabled = canDelete,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text("Excluir") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}