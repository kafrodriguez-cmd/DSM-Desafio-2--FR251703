package com.example.viajesapp.ui.destino

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.viajesapp.R
import com.example.viajesapp.data.model.Destino
import com.example.viajesapp.data.repository.DestinoRepository
import com.example.viajesapp.databinding.ActivityFormDestinoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class FormDestinoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormDestinoBinding
    private lateinit var destinoRepository: DestinoRepository
    private var selectedImageUri: Uri? = null
    private var localImagePath: String? = null
    private var destinoId: String? = null
    private var destinoActual: Destino? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this).load(it).centerCrop().into(binding.ivPreview)

            // Copiar la imagen de inmediato (evita el cuelgue al guardar)
            try {
                val dir = File(filesDir, "destinos_images")
                if (!dir.exists()) dir.mkdirs()
                val dest = File(dir, "img_${System.currentTimeMillis()}.jpg")
                contentResolver.openInputStream(it)?.use { input ->
                    FileOutputStream(dest).use { output -> input.copyTo(output) }
                }
                localImagePath = dest.absolutePath
            } catch (e: Exception) {
                Toast.makeText(this, "Error al copiar imagen: ${e.message}", Toast.LENGTH_LONG).show()
                localImagePath = null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormDestinoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        destinoRepository = DestinoRepository(applicationContext)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val paises = resources.getStringArray(R.array.paises)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paises)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPais.adapter = adapter

        destinoId = intent.getStringExtra("DESTINO_ID")
        if (destinoId != null) {
            supportActionBar?.title = getString(R.string.edit_destino)
            binding.btnGuardar.text = getString(R.string.actualizar)
            cargarDestino(destinoId!!)
        } else {
            supportActionBar?.title = getString(R.string.add_destino)
        }

        binding.btnSeleccionarImagen.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnGuardar.setOnClickListener { guardar() }
    }

    private fun cargarDestino(id: String) {
        showLoading(true)
        lifecycleScope.launch {
            val result = destinoRepository.getDestinoById(id)
            showLoading(false)
            result.onSuccess { destino ->
                destinoActual = destino
                binding.etNombre.setText(destino.nombre)
                binding.etPrecio.setText(destino.precio.toString())
                binding.etDescripcion.setText(destino.descripcion)

                val paises = resources.getStringArray(R.array.paises)
                val index = paises.indexOf(destino.pais)
                if (index >= 0) binding.spinnerPais.setSelection(index)

                if (destino.imagenUrl.isNotEmpty()) {
                    val file = File(destino.imagenUrl)
                    if (file.exists()) {
                        Glide.with(this@FormDestinoActivity)
                            .load(file)
                            .centerCrop()
                            .into(binding.ivPreview)
                    } else {
                        Glide.with(this@FormDestinoActivity)
                            .load(destino.imagenUrl)
                            .centerCrop()
                            .into(binding.ivPreview)
                    }
                }
            }.onFailure {
                Toast.makeText(this@FormDestinoActivity, R.string.error_general, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun guardar() {
        val nombre = binding.etNombre.text.toString().trim()
        val precioStr = binding.etPrecio.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()
        val paisSeleccionado = binding.spinnerPais.selectedItemPosition

        if (nombre.isEmpty() || precioStr.isEmpty() || descripcion.isEmpty() || paisSeleccionado == 0) {
            Toast.makeText(this, R.string.campos_obligatorios, Toast.LENGTH_SHORT).show()
            return
        }

        val precio = precioStr.toDoubleOrNull()
        if (precio == null || precio <= 0) {
            Toast.makeText(this, R.string.precio_invalido, Toast.LENGTH_SHORT).show()
            return
        }

        if (descripcion.length < 20) {
            Toast.makeText(this, R.string.descripcion_minima, Toast.LENGTH_SHORT).show()
            return
        }

        // Imagen obligatoria solo al crear
        if (destinoId == null && localImagePath == null && selectedImageUri == null) {
            Toast.makeText(this, R.string.imagen_requerida, Toast.LENGTH_SHORT).show()
            return
        }

        val pais = resources.getStringArray(R.array.paises)[paisSeleccionado]

        val destino = Destino(
            id = destinoId ?: "",
            nombre = nombre,
            pais = pais,
            precio = precio,
            descripcion = descripcion,
            imagenUrl = localImagePath ?: destinoActual?.imagenUrl ?: ""
        )

        showLoading(true)
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                if (destinoId == null) {
                    // Ya tenemos la ruta local; no volvemos a copiar
                    destinoRepository.addDestino(destino, null)
                } else {
                    if (localImagePath != null) {
                        destino.imagenUrl = localImagePath!!
                        destinoRepository.updateDestino(destino, null)
                    } else {
                        destinoRepository.updateDestino(destino, selectedImageUri)
                    }
                }
            }
            showLoading(false)

            result.onSuccess {
                val msg = if (destinoId == null) R.string.exito_guardar else R.string.exito_actualizar
                Toast.makeText(this@FormDestinoActivity, msg, Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure { e ->
                Toast.makeText(
                    this@FormDestinoActivity,
                    e.message ?: getString(R.string.error_general),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnGuardar.isEnabled = !show
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}