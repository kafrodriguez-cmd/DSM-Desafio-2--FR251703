package com.example.viajesapp.data.repository

import android.content.Context
import android.net.Uri
import com.example.viajesapp.data.model.Destino
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class DestinoRepository(private val context: Context? = null) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val collection = db.collection("destinos")

    suspend fun getAllDestinos(): Result<List<Destino>> {
        return try {
            val snapshot = collection.orderBy("nombre").get().await()
            val lista = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Destino::class.java)?.apply { id = doc.id }
            }
            Result.success(lista)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDestinoById(id: String): Result<Destino> {
        return try {
            val doc = collection.document(id).get().await()
            val destino = doc.toObject(Destino::class.java)?.apply { this.id = doc.id }
            if (destino != null) Result.success(destino)
            else Result.failure(Exception("Destino no encontrado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addDestino(destino: Destino, imageUri: Uri?): Result<String> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))
            destino.userId = userId

            if (imageUri != null && context != null) {
                destino.imagenUrl = saveImageLocally(imageUri)
            }

            val docRef = collection.add(destino).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateDestino(destino: Destino, imageUri: Uri?): Result<Unit> {
        return try {
            if (imageUri != null && context != null) {
                deleteLocalImage(destino.imagenUrl)
                destino.imagenUrl = saveImageLocally(imageUri)
            }
            collection.document(destino.id).set(destino).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteDestino(id: String, imageUrl: String): Result<Unit> {
        return try {
            deleteLocalImage(imageUrl)
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveImageLocally(uri: Uri): String {
        val ctx = context ?: throw Exception("Context no disponible")
        val dir = File(ctx.filesDir, "destinos_images")
        if (!dir.exists()) dir.mkdirs()

        val destFile = File(dir, "img_${UUID.randomUUID()}.jpg")
        ctx.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output -> input.copyTo(output) }
        } ?: throw Exception("No se pudo leer la imagen")

        return destFile.absolutePath
    }

    private fun deleteLocalImage(path: String) {
        if (path.startsWith("/") && path.contains("destinos_images")) {
            try { File(path).delete() } catch (_: Exception) { }
        }
    }
}