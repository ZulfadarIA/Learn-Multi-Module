package android.template.core.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject

interface TokenRepository {

    fun init(context: Context)

    fun getToken(): String?

    fun setToken(token: String)

    fun removeToken()
}

class TokensRepository @Inject constructor() : TokenRepository {
    private val KEY_TOKEN = "TOKEN"

    private lateinit var preferences: SharedPreferences

    override fun init(context: Context) {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        preferences = EncryptedSharedPreferences.create(
            context,
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    override fun getToken(): String? {
        return preferences.getString(KEY_TOKEN, null)
    }

    override fun setToken(token: String) {
        preferences.edit().apply {
            putString(KEY_TOKEN, token)
            apply()
        }
    }

    override fun removeToken() {
        preferences.edit().apply {
            remove(KEY_TOKEN)
            apply()
        }
    }

}

