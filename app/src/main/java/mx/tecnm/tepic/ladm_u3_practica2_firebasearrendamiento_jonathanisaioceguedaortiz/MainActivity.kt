package mx.tecnm.tepic.ladm_u3_practica2_firebasearrendamiento_jonathanisaioceguedaortiz

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import mx.tecnm.tepic.ladm_u3_practica2_firebasearrendamiento_jonathanisaioceguedaortiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        AlertDialog.Builder(this)
            .setTitle("FUNCIONAMIENTO GENERAL")
            .setMessage("En el caso del AUTOMOVIL es INGRESAR los datos de manera normal, para ELIMINAR/ACTUALIZAR hacer click en" +
                    " el item del listview, luego seleccionar la opción deseada y todo lo demás se hace como siempre lo hemos hecho en clase.\n"+
                    "Para BUSCAR automoviles ingresar una marca, un modelo o un kilometraje maximo a buscar, después dar click en el debido botón de"+
                    " buscar para que se realice la busqueda y muestre los datos. Es importante que se distingan mayusculas de minusculas"+
                    " y que para los kilometros solo se ingrese un número maximo a encontrar ya que la busqueda va entre 0 y el valor ingresado.\n"+
                    "Para ARRENDAMIENTO los datos se INGRESAN de forma normal solo que en marca y modelo es necesario que los valores existan en la tabla automovil"+
                    ", ya que se inserten los valores la aplicación por si sola va a obtener el id del 1ER automovil que cumpla ambas caracteristicas."+
                    "Para ELIMINAR y ACTUALIZAR este funciona igual que en el caso de automovil, para BUSCAR es igual que en automovil"+
                    " ingresar nombre, licencia, domicilio, marca o modelo del automovil y darle click a buscar para recuperar todos los valores coincidentes.\n" +
                    "Cuando se desee eliminar un automovil este no debe tener un arrendamiento vinculado así que primero deberán eliminarse los arrendamientos, la " +
                    "fecha del arrendamiento toma por defecto la hora actual del sistema así que ese dato no puede modificarse para mantener la integridad de la BDD")
            .setNeutralButton("Cerrar"){d,i->}
            .show()
    }
}