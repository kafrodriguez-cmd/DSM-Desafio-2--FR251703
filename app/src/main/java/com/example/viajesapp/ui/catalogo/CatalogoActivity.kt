package com.example.viajesapp.ui.catalogo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.viajesapp.R
import com.example.viajesapp.data.model.Destino
import com.example.viajesapp.data.repository.AuthRepository
import com.example.viajesapp.data.repository.DestinoRepository
import com.example.viajesapp.databinding.ActivityCatalogoBinding
import com.example.viajesapp.ui.auth.LoginActivity
import com.example.viajesapp.ui.destino.FormDestinoActivity
import kotlinx.coroutines.launch

class CatalogoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogoBinding
    private lateinit var destinoRepository: DestinoRepository
    private val authRepository = AuthRepository()
    private lateinit var adapter: DestinoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        destinoRepository = DestinoRepository(applicationContext)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        adapter = DestinoAdapter(
            onEdit = { destino ->
                val intent = Intent(this, FormDestinoActivity::class.java)
                intent.putExtra("DESTINO_ID", destino.id)
                startActivity(intent)
            },
            onDelete = { destino -> confirmarEliminar(destino) }
        )

        binding.rvDestinos.layoutManager = LinearLayoutManager(this)
        binding.rvDestinos.adapter = adapter

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, FormDestinoActivity::class.java))
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_logout) {
                authRepository.logout()
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
                true
            } else false
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDestinos()
    }

    private fun cargarDestinos() {
        showLoading(true)
        lifecycleScope.launch {
            val result = destinoRepository.getAllDestinos()
            showLoading(false)
            result.onSuccess { lista ->
                adapter.submitList(lista)
                binding.tvEmpty.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
            }.onFailure { e ->
                Toast.makeText(
                    this@CatalogoActivity,
                    e.message ?: getString(R.string.error_general),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun confirmarEliminar(destino: Destino) {
        AlertDialog.Builder(this)
            .setTitle(R.string.eliminar)
            .setMessage(R.string.confirmar_eliminar)
            .setPositiveButton(R.string.si) { _, _ -> eliminarDestino(destino) }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun eliminarDestino(destino: Destino) {
        showLoading(true)
        lifecycleScope.launch {
            val result = destinoRepository.deleteDestino(destino.id, destino.imagenUrl)
            showLoading(false)
            result.onSuccess {
                Toast.makeText(this@CatalogoActivity, R.string.exito_eliminar, Toast.LENGTH_SHORT).show()
                cargarDestinos()
            }.onFailure { e ->
                Toast.makeText(
                    this@CatalogoActivity,
                    e.message ?: getString(R.string.error_general),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}
