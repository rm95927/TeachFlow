package fiap.com.edtech

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fiap.com.edtech.ui.theme.EdtechTheme

class TermoAceiteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tipoUsuario = intent.getStringExtra("tipoUsuario") ?: "aluno"

        setContent {
            EdtechTheme {
                TelaTermoDeAceite(
                    onAceitar = {
                        salvarAceite(this, tipoUsuario)
                        val intent = when (tipoUsuario) {
                            "professor" -> Intent(this, PainelProfessorActivity::class.java)
                            else -> Intent(this, PainelAlunoActivity::class.java)
                        }
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    },
                    onRecusar = {


                    },
                    onBackClick = { finish() }
                )
            }
        }
    }

    fun salvarAceite(context: Context, tipoUsuario: String) {
        val prefs: SharedPreferences = context.getSharedPreferences("termo_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("aceite_$tipoUsuario", true)
        editor.apply()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaTermoDeAceite(
    onAceitar: () -> Unit,
    onRecusar: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Termo de Aceite") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // SCROLLABLE TEXT
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = """
                       Termo de Aceite e Consentimento de Uso de Dados – TEACHFLOW
                       Código do Documento: NSI-TF-001
                       Versão: 1.0
                       Data de Emissão: 18/06/2025

                       Ao prosseguir com o uso da plataforma TEACHFLOW, você declara que leu, compreendeu e concorda com os termos deste documento, autorizando o acesso e tratamento dos seus dados conforme descrito abaixo:

                       1. Finalidade da Coleta de Dados
                       A TEACHFLOW coleta e trata seus dados pessoais para:

                       Personalizar sua experiência educacional;
                       Sugerir cursos com base no seu perfil e interesse;
                       Monitorar seu desempenho e engajamento;
                       Melhorar continuamente a plataforma;
                       Garantir a segurança e integridade das informações.

                       2. Dados Coletados
                       A plataforma poderá coletar:

                       Informações de cadastro (nome, e-mail, data de nascimento, etc.);
                       Dados de uso da aplicação (atividades realizadas, tempo de permanência, cursos acessados);
                       Localização geográfica (caso habilitada);
                       Informações sobre o dispositivo de acesso.

                       3. Compartilhamento
                       Seus dados não serão compartilhados com terceiros sem sua autorização, exceto quando exigido por lei ou para garantir o bom funcionamento da plataforma (ex.: serviços de autenticação, armazenamento em nuvem).

                       4. Armazenamento e Segurança
                       As informações são armazenadas em ambiente seguro, com medidas técnicas e administrativas adequadas para proteger contra acessos não autorizados, vazamentos ou destruição.

                       5. Seus Direitos
                       Você pode, a qualquer momento:

                       Acessar e corrigir seus dados;
                       Solicitar a exclusão da conta e dos dados associados;
                       Revogar este consentimento, ciente de que isso poderá limitar funcionalidades da plataforma.

                       6. Consentimento
                       Ao clicar em "Aceito", você autoriza o uso dos seus dados nos termos acima descritos, de acordo com a Lei Geral de Proteção de Dados (Lei nº 13.709/2018).
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // FIXED BUTTONS AT BOTTOM
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onRecusar,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Recusar")
                }
                Button(onClick = onAceitar) {
                    Text("Aceitar")
                }
            }
        }
    }
}

