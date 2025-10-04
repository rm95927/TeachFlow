
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import fiap.com.edtech.Curso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CursosViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _cursos = MutableStateFlow<List<Curso>>(emptyList())
    val cursos: StateFlow<List<Curso>> = _cursos

    private var registration: ListenerRegistration? = null

    init {
        fetchCursos()
    }

    private fun fetchCursos() {
        // addSnapshotListener já é assíncrono; não precisa de viewModelScope.launch
        registration = db.collection("cursos")
            // .orderBy("nome") // <- opcional, se tiver índice/campo "nome"
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    // você pode logar o erro se quiser
                    return@addSnapshotListener
                }

                val lista = snapshot.documents.mapNotNull { doc ->
                    // Lê campos com segurança e injeta o doc.id
                    val nome = doc.getString("nome") ?: return@mapNotNull null
                    val descricao = doc.getString("descricao") ?: ""
                    val imagemUrl = doc.getString("imagemUrl") ?: ""
                    Curso(
                        id = doc.id,          // <- AQUI pegamos o id do documento
                        nome = nome,
                        descricao = descricao,
                        imagemUrl = imagemUrl
                    )
                }

                _cursos.value = lista
            }
    }

    override fun onCleared() {
        super.onCleared()
        registration?.remove()
        registration = null
    }
}
