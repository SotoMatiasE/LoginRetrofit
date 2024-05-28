package com.example.loginretrofit

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.loginretrofit.databinding.ActivityMainBinding
import com.example.loginretrofit.retrofit.LoginService
import com.example.loginretrofit.retrofit.UserInfo
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(mBinding.root)

        //configuracion del switch
        mBinding.swType.setOnCheckedChangeListener { button, isChecked ->
            button.text = if (isChecked) getString(R.string.main_type_login)
                          else getString(R.string.main_type_register)

            mBinding.btnLogin.text = button.text
        }
        //listenner al boton
        mBinding.btnLogin.setOnClickListener {
            if (mBinding.swType.isChecked){
                login()
            }else{
                register()
            }
        }

        mBinding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun login() { //si presiono el btnLogin muestra la actualizacion de la ui
        //EXTRAER VALORES Y PASAR VALORES A jsonObjectRequest
        val email = mBinding.etEmail.text.toString().trim()
        val password = mBinding.etPassword.text.toString().trim()

        val retrofit = Retrofit.Builder()
        //configuracion del retrofit
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        //ejecutar servicio
        val service = retrofit.create(LoginService::class.java)
        //invocar servicio de login desde corrutinas
        lifecycleScope.launch {
            try {
                val result = service.loginUser(UserInfo(email, password))
                updateUi("${Constants.TOKEN_PROPERTY}: ${result.token}")
            } catch (e: Exception) {
                //saveCast
                (e as? HttpException)?.let {
                    when (it.code()){ //MANEJO DE ERRORES
                        400 -> {//Error
                            updateUi(getString(R.string.main_error_server)) //muestra en la pantalla
                        }
                        else -> {
                            updateUi(getString(R.string.main_error_response))
                        }
                    }
                }
            }
        }
    }

    private fun register() {
        //EXTRAER VALORES Y PASAR VALORES A jsonObjectRequest
        val email = mBinding.etEmail.text.toString().trim()
        val password = mBinding.etPassword.text.toString().trim()

        val retrofit = Retrofit.Builder()
            //configuracion del retrofit
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        //ejecutar servicio
        val service = retrofit.create(LoginService::class.java)
        //invocar servicio de login desde corrutinas
        lifecycleScope.launch {
            try {
                val result = service.registerUser(UserInfo(email, password))
                updateUi("${Constants.ID_PROPERTY}: {$result.id}, " +
                        "${Constants.TOKEN_PROPERTY}: ${result.token}")
            } catch (e: Exception) {
                //saveCast
                (e as? HttpException)?.let {
                    when (it.code()){ //MANEJO DE ERRORES
                        400 -> {//Error
                            updateUi(getString(R.string.main_error_server)) //muestra en la pantalla
                        }
                        else -> {
                            updateUi(getString(R.string.main_error_response))
                        }
                    }
                }
            }
        }
    }

    //configurar respuesta de la ui si la peticion es exitosa la interfaz cambia
    private fun updateUi(result: String) {
        mBinding.tvResult.visibility = View.VISIBLE
        mBinding.tvResult.text = result
    }
}