package com.example.viajesapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.viajesapp.data.repository.AuthRepository
import com.example.viajesapp.databinding.ActivityLoginBinding
import com.example.viajesapp.ui.catalogo.CatalogoActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Si ya hay sesión, ir directo al catálogo
        if (authRepository.currentUser != null) {
            goToCatalogo()
            return
        }

        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(com.example.viajesapp.R.string.campos_obligatorios), Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)
        lifecycleScope.launch {
            val result = authRepository.login(email, password)
            showLoading(false)
            result.onSuccess {
                goToCatalogo()
            }.onFailure { e ->
                Toast.makeText(this@LoginActivity, e.message ?: getString(com.example.viajesapp.R.string.error_general), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goToCatalogo() {
        startActivity(Intent(this, CatalogoActivity::class.java))
        finish()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !show
    }
}
