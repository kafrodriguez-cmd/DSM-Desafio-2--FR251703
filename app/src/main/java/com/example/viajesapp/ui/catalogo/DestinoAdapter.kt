package com.example.viajesapp.ui.catalogo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.viajesapp.data.model.Destino
import com.example.viajesapp.databinding.ItemDestinoBinding
import java.text.NumberFormat
import java.util.Locale

class DestinoAdapter(
    private val onEdit: (Destino) -> Unit,
    private val onDelete: (Destino) -> Unit
) : ListAdapter<Destino, DestinoAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDestinoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemDestinoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(destino: Destino) {
            binding.tvNombre.text = destino.nombre
            binding.tvPais.text = destino.pais
            binding.tvPrecio.text = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                .format(destino.precio)
            binding.tvDescripcion.text = destino.descripcion
            val imageSource: Any = if (destino.imagenUrl.startsWith("/")) {
                java.io.File(destino.imagenUrl)
            } else {
                destino.imagenUrl
            }

            Glide.with(binding.root.context)
                .load(imageSource)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .into(binding.ivImagen)

            binding.btnEditar.setOnClickListener { onEdit(destino) }
            binding.btnEliminar.setOnClickListener { onDelete(destino) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Destino>() {
        override fun areItemsTheSame(oldItem: Destino, newItem: Destino) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Destino, newItem: Destino) = oldItem == newItem
    }
}
