package mx.tecnm.tepic.ladm_u3_practica2_firebasearrendamiento_jonathanisaioceguedaortiz.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_practica2_firebasearrendamiento_jonathanisaioceguedaortiz.databinding.ActivityActualizarAutoBinding

class ActualizarAuto : AppCompatActivity() {
    lateinit var binding:ActivityActualizarAutoBinding
    var idSeleccionado = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarAutoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idSeleccionado = intent.extras!!.getString("idseleccionado")!!
        val baseRemota= FirebaseFirestore.getInstance()
        baseRemota.collection("automovil")
            .document(idSeleccionado)
            .get()
            .addOnSuccessListener {
                binding.marca.setText(it.getString("marca"))
                binding.modelo.setText(it.getString("modelo"))
                binding.kilometraje.setText(it.getLong("kilometraje").toString())
            }
            .addOnFailureListener {
                AlertDialog.Builder(this)
                    .setMessage(it.message)
                    .show()
            }

        binding.actualizar.setOnClickListener{
            val baseRemota= FirebaseFirestore.getInstance()
            baseRemota.collection("automovil")
                .document(idSeleccionado)
                .update("marca", binding.marca.text.toString(), "modelo", binding.modelo.text.toString(),
                    "kilometraje", binding.kilometraje.text.toString().toInt())
                .addOnSuccessListener {
                    Toast.makeText(this, "Se actualizó con exito", Toast.LENGTH_LONG).show()
                    binding.marca.text.clear()
                    binding.modelo.text.clear()
                    binding.kilometraje.text.clear()
                }
                .addOnFailureListener {
                    AlertDialog.Builder(this)
                        .setMessage(it.message)
                        .show()
                }
        }

        binding.regresar.setOnClickListener {
            finish()
        }
    }
}