package mx.tecnm.tepic.ladm_u3_practica2_firebasearrendamiento_jonathanisaioceguedaortiz.ui.dashboard

import android.R
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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_practica2_firebasearrendamiento_jonathanisaioceguedaortiz.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var listaIDs = ArrayList<String>()
    val baseRemota= FirebaseFirestore.getInstance()
    val baseRemota2= FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        FirebaseFirestore.getInstance().collection("arrendamiento")
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
                    var cadena = "-----------------\nNombre: ${documento.getString("nombre")} \nDomicilio: ${documento.getString("domicilio")} \nLicencia: ${documento.getString("licencia")} " +
                            "\nFecha: ${documento.getDate("fecha")} \nIdAuto: ${documento.getString("idauto")}\n" +
                            "Marca: ${documento.getString("marca")}\nModelo: ${documento.getString("modelo")}"
                    arreglo.add(cadena)
                    listaIDs.add(documento.id)
                }

                try {
                    binding.lista.adapter = ArrayAdapter<String>(requireContext(), R.layout.simple_list_item_1, arreglo)
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
                }catch (err: NullPointerException){

                }

            }

        binding.insertar.setOnClickListener {
            baseRemota2.collection("automovil")
                .whereEqualTo("marca", binding.marca.text.toString())
                .whereEqualTo("modelo", binding.modelo.text.toString())
                .get()
                .addOnSuccessListener {
                    for (documento in it){
                        val datos = hashMapOf(
                            "nombre" to binding.nombre.text.toString(),
                            "domicilio" to binding.domicilio.text.toString(),
                            "licencia" to binding.licencia.text.toString(),
                            "marca" to binding.marca.text.toString(),
                            "modelo" to binding.modelo.text.toString(),
                            "idauto" to documento.id,
                            "fecha" to Timestamp.now()
                        )
                        baseRemota.collection("arrendamiento").add(datos)
                            .addOnSuccessListener{
                                // SÍ SE PUDO
                                Toast.makeText(requireContext(), "Exito! Sí se pudo", Toast.LENGTH_LONG).show()
                                binding.nombre.setText("")
                                binding.domicilio.setText("")
                                binding.licencia.setText("")
                                binding.modelo.setText("")
                                binding.marca.setText("")
                            }
                            .addOnFailureListener {
                                // NO SE PUDO
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

        binding.buscar.setOnClickListener {
            var mensaje = ""
            var consulta = baseRemota.collection("arrendamiento").whereEqualTo("nombre", binding.nombre.text.toString())
            if (!binding.nombre.text.isEmpty()){
                consulta = baseRemota.collection("arrendamiento").whereEqualTo("nombre", binding.nombre.text.toString())
            }else if (!binding.licencia.text.isEmpty()){
                consulta = baseRemota.collection("arrendamiento").whereEqualTo("licencia", binding.licencia.text.toString())
            }else if (!binding.domicilio.text.isEmpty()){
                consulta = baseRemota.collection("arrendamiento").whereEqualTo("domicilio", binding.domicilio.text.toString())
            }else if (!binding.marca.text.isEmpty()){
                consulta = baseRemota.collection("arrendamiento").whereEqualTo("marca", binding.marca.text.toString())
            }else if (!binding.modelo.text.isEmpty()){
                consulta = baseRemota.collection("arrendamiento").whereEqualTo("modelo", binding.modelo.text.toString())
            }
            consulta.get()
                .addOnSuccessListener {
                    for (documento in it){
                        mensaje += "|| Nombre: ${documento.getString("nombre")} \nDomicilio: ${documento.getString("domicilio")} \n" +
                                "Licencia: ${documento.getString("licencia")} " +
                                "\nFecha: ${documento.getDate("fecha")} \nIdAuto: ${documento.getString("idauto")}\n" +
                                "Marca: ${documento.getString("marca")}\nModelo: ${documento.getString("modelo")} ||\n\n"
                    }
                    if (mensaje == ""){
                        AlertDialog.Builder(requireContext())
                            .setTitle("ERROR")
                            .setMessage("No hay datos que cumplan esas caracteristicas!")
                            .setNeutralButton("Cerrar"){d,i->}
                            .show()
                        binding.nombre.setText("")
                        binding.domicilio.setText("")
                        binding.licencia.setText("")
                        binding.modelo.setText("")
                        binding.marca.setText("")
                        return@addOnSuccessListener
                    }
                    AlertDialog.Builder(requireContext())
                        .setTitle("Datos recuperados")
                        .setMessage(mensaje)
                        .setNeutralButton("Cerrar"){d,i->}
                        .show()
                    binding.nombre.setText("")
                    binding.domicilio.setText("")
                    binding.licencia.setText("")
                    binding.modelo.setText("")
                    binding.marca.setText("")
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

    private fun getIdAuto(){

    }

    private fun actualizar(idSeleccionado: String) {
        var otraVentana = Intent(requireContext(), ActualizarArrendamiento::class.java)

        otraVentana.putExtra("idseleccionado", idSeleccionado)

        startActivity(otraVentana)
    }

    private fun eliminar(idSeleccionado: String) {
        val baseRemota=FirebaseFirestore.getInstance()
        baseRemota.collection("arrendamiento")
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