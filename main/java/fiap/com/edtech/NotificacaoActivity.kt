package fiap.com.edtech

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging

class NotificacaoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // tipo de usuário pode vir das SharedPrefs (definido no login)
        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val tipoUsuario = prefs.getString("tipoUsuario", "aluno") ?: "aluno"

        setContent {
            var processing by remember { mutableStateOf(false) }

            // Launcher para pedir a permissão de notificação (Android 13+)
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                // Após a resposta do SO
                if (granted) {
                    // 1) Token do FCM
                    FirebaseMessaging.getInstance().token
                        .addOnSuccessListener { token ->
                            // 2) salva o token no Firestore vinculado ao usuário atual
                            saveFcmTokenForCurrentUser(this, token)
                            // (opcional) assina um tópico geral
                            FirebaseMessaging.getInstance().subscribeToTopic("notificacoes")
                            salvarPreferenciaNotificacao(this, true)
                            navegarParaPainel(tipoUsuario)
                        }
                        .addOnFailureListener {
                            // Se falhar, segue o fluxo mesmo assim
                            salvarPreferenciaNotificacao(this, true)
                            navegarParaPainel(tipoUsuario)
                        }
                } else {
                    // Usuário negou a permissão do SO
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("notificacoes")
                    marcarNaoReceberNotificacoes()
                    salvarPreferenciaNotificacao(this, false)
                    navegarParaPainel(tipoUsuario)
                }
            }

            NotificacaoScreen(
                onAceitar = {
                    if (processing) return@NotificacaoScreen
                    processing = true

                    // Se Android 13+ e ainda não habilitado, pedir permissão
                    val precisaPedirPermissao = Build.VERSION.SDK_INT >= 33 &&
                            !NotificationManagerCompat.from(this).areNotificationsEnabled()

                    if (precisaPedirPermissao) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        // Já tem permissão (ou versão < 33): pega o token agora
                        FirebaseMessaging.getInstance().token
                            .addOnSuccessListener { token ->
                                saveFcmTokenForCurrentUser(this, token)
                                FirebaseMessaging.getInstance().subscribeToTopic("notificacoes")
                                salvarPreferenciaNotificacao(this, true)
                                navegarParaPainel(tipoUsuario)
                            }
                            .addOnFailureListener {
                                salvarPreferenciaNotificacao(this, true)
                                navegarParaPainel(tipoUsuario)
                            }
                    }
                },
                onRecusar = {
                    if (processing) return@NotificacaoScreen
                    processing = true

                    // Não recebe notificações
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("notificacoes")
                    marcarNaoReceberNotificacoes()
                    salvarPreferenciaNotificacao(this, false)
                    navegarParaPainel(tipoUsuario)
                }
            )
        }
    }

    private fun navegarParaPainel(tipoUsuario: String) {
        val intent = when (tipoUsuario) {
            "professor" -> Intent(this, PainelProfessorActivity::class.java)
            else -> Intent(this, PainelAlunoActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

/** UI */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacaoScreen(
    onAceitar: () -> Unit,
    onRecusar: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Notificações") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                Text(
                    "Gostaria de receber notificações do app no seu celular?",
                    style = MaterialTheme.typography.titleMedium
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    "Você pode mudar isso depois em Perfil → Notificações.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = onRecusar) { Text("Recusar") }
                Button(onClick = onAceitar) { Text("Aceitar") }
            }
        }
    }
}

/** Preferência simples local */
fun salvarPreferenciaNotificacao(context: Context, aceitou: Boolean) {
    context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        .edit()
        .putBoolean("receber_notificacoes", aceitou)
        .apply()
}
