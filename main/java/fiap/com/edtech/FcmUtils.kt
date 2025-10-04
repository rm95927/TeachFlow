package fiap.com.edtech

import android.content.Context
import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

private const val PREFS = "prefs"
private const val KEY_PENDING_FCM = "pending_fcm_token"

/**
 * Chame isto quando tiver um token válido E o usuário tiver aceitado receber notificações.
 * - Se tiver usuário logado: grava em usuarios/{uid} os campos fcmToken e receberNotificacoes=true (merge).
 * - Se NÃO tiver usuário logado: salva o token em SharedPreferences para sincronizar após login.
 */
fun saveFcmTokenForCurrentUser(context: Context, token: String) {
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid

    if (uid == null) {
        // salva como pendente (será enviado ao logar)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PENDING_FCM, token)
            .apply()
        return
    }

    val db = FirebaseFirestore.getInstance()
    val data = mapOf(
        "fcmToken" to token,
        "receberNotificacoes" to true,
        "deviceInfo" to mapOf(
            "sdkInt" to Build.VERSION.SDK_INT,
            "manufacturer" to Build.MANUFACTURER,
            "model" to Build.MODEL
        )
    )

    db.collection("usuarios")
        .document(uid)
        .set(data, SetOptions.merge())
}

/**
 * Use após o login para subir um token pendente (se houver).
 * Ex.: após descobrir o tipo do usuário no LoginActivity, chame maybeUploadPendingFcmToken(this).
 */
fun maybeUploadPendingFcmToken(context: Context) {
    val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    val pending = prefs.getString(KEY_PENDING_FCM, null) ?: return
    saveFcmTokenForCurrentUser(context, pending)
    // limpa o pendente depois que subiu
    prefs.edit().remove(KEY_PENDING_FCM).apply()
}

/**
 * Opcional: se o usuário recusar notificações, você pode marcar receberNotificacoes=false.
 * (Não apagamos o token para facilitar um “opt-in” futuro, mas pode apagar se preferir.)
 */
fun marcarNaoReceberNotificacoes() {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    FirebaseFirestore.getInstance()
        .collection("usuarios")
        .document(uid)
        .set(mapOf("receberNotificacoes" to false), SetOptions.merge())
}
