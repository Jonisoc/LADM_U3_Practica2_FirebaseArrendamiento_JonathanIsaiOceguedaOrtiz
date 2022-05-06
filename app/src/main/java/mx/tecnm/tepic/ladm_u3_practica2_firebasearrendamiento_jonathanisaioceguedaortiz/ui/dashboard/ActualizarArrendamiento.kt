package mx.tecnm.tepic.ladm_u3_practica2_firebasearrendamiento_jonathanisaioceguedaortiz.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_practica2_firebasearrendamiento_jonathanisaioceguedaortiz.databinding.ActivityActualizarArrendamientoBinding

class ActualizarArrendamiento : AppCompatActivity() {
    lateinit var binding: ActivityActualizarArrendamientoBinding
    var idSeleccionado = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarArrendamientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idSeleccionado = intent.extras!!.getString("idseleccionado")!!
        val baseRemota= FirebaseFirestore.getInstance()
        baseRemota.collection("arrendamiento")
            .document(idSeleccionado)
            .get()
            .addOnSuccessListener {
                binding.nombre.setText(it.getString("nombre"))
                binding.domicilio.setText(it.getString("domicilio"))
                binding.licencia.setText(it.getString("licencia"))
                binding.marca.setText(it.getString("marca"))
                binding.modelo.setText(it.getString("modelo"))
                binding.fecha.setText(it.getDate("fecha").toString())
            }
            .addOnFailureListener {
                AlertDialog.Builder(this)
                    .setMessage(it.message)
                    .show()
            }

        binding.fecha.isEnabled = false

        binding.actualizar.setOnClickListener{
            val baseRemota= FirebaseFirestore.getInstance()
            val baseRemota2= FirebaseFirestore.getInstance()
            baseRemota2.collection("automovil")
                .whereEqualTo("marca", binding.marca.text.toString())
                .whereEqualTo("modelo", binding.modelo.text.toString())
                .get()
                .addOnSuccessListener {
                    var bool = false
                    for (documento in it){
                        bool = true
                    }
                    if (!bool){
                        AlertDialog.Builder(this)
                            .setTitle("ERROR")
                            .setMessage("Ingresa un automovil que exista!")
                            .setNeutralButton("Cerrar"){d,i->}
                            .show()
                        return@addOnSuccessListener
                    }
                    baseRemota.collection("arrendamiento")
                        .document(idSeleccionado)
                        .update("nombre", binding.nombre.text.toString(),
                            "domicilio", binding.domicilio.text.toString(),
                            "licencia", binding.licencia.text.toString(),
                            "modelo", binding.modelo.text.toString(),
                            "idauto", it.documents[0].id,
                            "marca", binding.marca.text.toString())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Se actualizó con exito", Toast.LENGTH_LONG).show()
                            binding.marca.text.clear()
                            binding.modelo.text.clear()
                            binding.nombre.text.clear()
                            binding.domicilio.text.clear()
                            binding.licencia.text.clear()
                        }
                        .addOnFailureListener {
                            AlertDialog.Builder(this)
                                .setMessage(it.message)
                                .show()
                        }

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