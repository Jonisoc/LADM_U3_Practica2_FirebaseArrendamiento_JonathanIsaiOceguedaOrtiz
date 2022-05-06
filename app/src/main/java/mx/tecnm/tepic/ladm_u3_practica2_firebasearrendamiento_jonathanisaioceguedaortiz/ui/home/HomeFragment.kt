package mx.tecnm.tepic.ladm_u3_practica2_firebasearrendamiento_jonathanisaioceguedaortiz.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_practica2_firebasearrendamiento_jonathanisaioceguedaortiz.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var listaIDs = ArrayList<String>()
    val baseRemota=FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        FirebaseFirestore.getInstance().collection("automovil")
            .addSnapshotListener { query, error ->
                if (error!=null){
                    //SI HUBO ERROR
                    AlertDialog.Builder(requireContext())
                        .setMessage(error.message)
                        .show()
                    return@addSnapshotListener
                }

                listaIDs.clear()
                val arreglo = ArrayList<String>()
                for (documento in query!!){
                    var cadena = "${documento.getString("marca")} ${documento.getString("modelo")} Kilometraje: ${documento.getLong("kilometraje")}"
                    arreglo.add(cadena)
                    listaIDs.add(documento.id)
                }

                binding.lista.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, arreglo)

                binding.lista.setOnItemClickListener { adapterView, view, posicion, l ->
                    val idSeleccionado = listaIDs.get(posicion)

                    AlertDialog.Builder(requireContext())
                        .setTitle("ATENCION")
                        .setMessage("¿Qué deseas hacer con ID: ${idSeleccionado}?")
                        .setNeutralButton("ELIMINAR"){d,i->
                            eliminar(idSeleccionado)
                        }
                        .setPositiveButton("ACTUALIZAR"){d,i->
                            actualizar(idSeleccionado)
                        }
                        .setNegativeButton("CERRAR"){d,i->}
                        .show()
                }
            }

        binding.insertar.setOnClickListener {
            val datos = hashMapOf(
                "modelo" to binding.modelo.text.toString(),
                "marca" to binding.marca.text.toString(),
                "kilometraje" to binding.kilometraje.text.toString().toInt()
            )

            baseRemota.collection("automovil").add(datos)
                .addOnSuccessListener{
                    // SÍ SE PUDO
                    Toast.makeText(requireContext(), "Exito! Sí se pudo", Toast.LENGTH_LONG).show()
                    binding.modelo.setText("")
                    binding.marca.setText("")
                    binding.kilometraje.setText("")
                }
                .addOnFailureListener {
                    // NO SE PUDO
                    AlertDialog.Builder(requireContext())
                        .setMessage(it.message)
                        .show()
                }
        }

        binding.buscar.setOnClickListener {
            var consulta = baseRemota.collection("automovil").whereLessThan("kilometraje", binding.kilometraje.text.toString().toInt())
            var mensaje = ""
            if (!binding.marca.text.isEmpty()){
                consulta = baseRemota.collection("automovil").whereEqualTo("marca", binding.marca.text.toString())
            }else if (!binding.modelo.text.isEmpty()){
                consulta = baseRemota.collection("automovil").whereEqualTo("modelo", binding.modelo.text.toString())
            }else if (!binding.kilometraje.text.isEmpty()){
                consulta = baseRemota.collection("automovil").whereLessThan("kilometraje", binding.kilometraje.text.toString().toInt())
            }
            consulta.get()
                .addOnSuccessListener {
                    for (documento in it){
                        mensaje += "- ${documento.getString("marca")} ${documento.getString("modelo")} ${documento.getLong("kilometraje")} km\n"
                    }
                    if (mensaje == ""){
                        AlertDialog.Builder(requireContext())
                            .setTitle("ERROR")
                            .setMessage("No hay datos que cumplan esas caracteristicas!")
                            .setNeutralButton("Cerrar"){d,i->}
                            .show()
                        return@addOnSuccessListener
                    }
                    AlertDialog.Builder(requireContext())
                        .setTitle("Datos recuperados")
                        .setMessage(mensaje)
                        .setNeutralButton("Cerrar"){d,i->}
                        .show()
                }
                .addOnFailureListener {
                    AlertDialog.Builder(requireContext())
                        .setMessage(it.message)
                        .show()
                }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun actualizar(idSeleccionado: String) {
        var otraVentana = Intent(requireContext(), ActualizarAuto::class.java)

        otraVentana.putExtra("idseleccionado", idSeleccionado)

        startActivity(otraVentana)
    }

    private fun eliminar(idSeleccionado: String) {
        val baseRemota = FirebaseFirestore.getInstance()
        val baseRemota2 = FirebaseFirestore.getInstance()
        var bool = false
        baseRemota.collection("arrendamiento").whereEqualTo("idauto", idSeleccionado).get()
            .addOnSuccessListener {
                for (documento in it){
                    bool = true
                }
                if (bool) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("ERROR")
                        .setMessage("No se puede borrar este automovil ya que hay un arrendamiento que lo está usando!")
                        .setNeutralButton("Cerrar") { d, i -> }
                        .show()
                    return@addOnSuccessListener
                }else{
                    baseRemota.collection("automovil")
                        .document(idSeleccionado)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Se eliminó el registro correctamente", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener {
                            AlertDialog.Builder(requireContext())
                                .setMessage(it.message)
                                .show()
                        }
                }
            }
            .addOnFailureListener {
                AlertDialog.Builder(requireContext())
                    .setMessage(it.message)
                    .show()
            }

    }
}