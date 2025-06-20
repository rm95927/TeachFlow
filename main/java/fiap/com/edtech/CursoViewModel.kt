import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import fiap.com.edtech.Curso

class CursosViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _cursos = MutableStateFlow<List<Curso>>(emptyList())
    val cursos: StateFlow<List<Curso>> = _cursos

    init {
        fetchCursos()
    }

    private fun fetchCursos() {
        viewModelScope.launch {
            db.collection("cursos")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener

                    val lista = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Curso::class.java)
                    }
                    _cursos.value = lista
                }
        }
    }
}
