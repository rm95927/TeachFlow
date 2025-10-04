package fiap.com.edtech

import com.google.firebase.Timestamp

data class Avaliacao(
    val id: String = "",
    val uid: String = "",
    val texto: String = "",
    val timestamp: Timestamp? = null,
    val autorEmail: String = ""   // útil para mostrar quem avaliou
)
