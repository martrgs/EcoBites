package com.example.ecobites.api

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.example.ecobites.R
import com.example.ecobites.model.UserSession
import java.util.concurrent.Executor


class GoogleAuthManager(
    private val context: Context,
    private val executor: Executor,
    private val onSuccess: (UserSession) -> Unit,
    private val onFailureMessage: (String) -> Unit
) {
    private val credentialManager = CredentialManager.create(context)

    fun signIn() {
        val clientId = context.getString(R.string.google_server_client_id).trim()
        if (clientId.isBlank() || clientId.startsWith("REEMPLAZA")) {
            onFailureMessage("Falta configurar el Client ID web de Google.")
            return
        }

        // Muestra todas las cuentas del dispositivo, incluidas las no usadas antes,
        // y evita el inicio automático para que el usuario pueda elegir o agregar una.
        val googleOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .setServerClientId(clientId)
            .build()
        val request = GetCredentialRequest.Builder().addCredentialOption(googleOption).build()
        credentialManager.getCredentialAsync(
            context,
            request,
            null,
            executor,
            CredentialManagerCallback()
        )
    }

    private inner class CredentialManagerCallback : androidx.credentials.CredentialManagerCallback<GetCredentialResponse, GetCredentialException> {
        override fun onResult(result: GetCredentialResponse) {
            val credential = result.credential
            if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                onFailureMessage("No se recibió una cuenta de Google válida.")
                return
            }
            runCatching { GoogleIdTokenCredential.createFrom(credential.data) }
                .onSuccess { google ->
                    val email = google.id.trim()
                    if (email.isBlank()) onFailureMessage("Google no proporcionó un correo para esta cuenta.")
                    else onSuccess(UserSession(google.displayName?.ifBlank { email.substringBefore('@') } ?: email.substringBefore('@'), email, "google"))
                }
                .onFailure { onFailureMessage("No fue posible leer la cuenta de Google.") }
        }

        override fun onError(e: GetCredentialException) {
            onFailureMessage("No se pudo iniciar sesión con Google. ${e.type}")
        }
    }
}
