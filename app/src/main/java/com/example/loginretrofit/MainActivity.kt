package com.example.loginretrofit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.loginretrofit.databinding.ActivityMainBinding
import com.example.loginretrofit.retrofit.LoginResponse
import com.example.loginretrofit.retrofit.LoginService
import com.example.loginretrofit.retrofit.UserInfo
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
            login()
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
        //llamada a metodos// enqueue() manda a la cola los metodos enviados
        service.login(UserInfo(email, password)).enqueue(

            /*manipular la respuesta// Callback debe coincidir con el metodo de servicio*/
            object  : Callback<LoginResponse>{ //importar funciones ctrl + intro
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    //metodo sobre escrito para la respuesta exitosa
                    //Actualizar ui
                    val result = response.body() //asi extraemos la rta TOKEN


                    //Listener
                    updateUi("${Constants.TOKEN_PROPERTY}: ${result?.token}")
                }

                override fun onFailure(call: Call<LoginResponse>, response: Throwable) {
                    //metodo sobre escrito para la respuesta falla
                    Log.e("Retrofit", "Problemas con el servidor.")
                }
            }
        )

        //peticion con volley
        /*       val typeMethod = if (mBinding.swType.isChecked) Constants.LOGIN_PATH
                                       else Constants.REGISTER_PATH
               //construir URL
               val url = Constants.BASE_URL + Constants.API_PATH + typeMethod

               //CONSTRUCCION DEL jsonParams
               val jsonParams = JSONObject()
               if (email.isNotEmpty()){
                   jsonParams.put(Constants.EMAIL_PARAM, email)
               }
               if (password.isNotEmpty()){
                   jsonParams.put(Constants.PASSWORD_PARAM, password)
               }

               val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonParams,{response ->
                   //PROCESAR RESPUESTA
                   Log.i("response", response.toString())
                   //Extraccion de valores
                   val id = response.optString(Constants.ID_PROPERTY, Constants.ERROR_VALUES)
                   val token = response.optString(Constants.TOKEN_PROPERTY, Constants.ERROR_VALUES)

                   val result = if (id.equals(Constants.ERROR_VALUES)) "${Constants.TOKEN_PROPERTY}: $token"
                       else "${Constants.ID_PROPERTY}: $id, ${Constants.TOKEN_PROPERTY}: $token"

                   //Listener
                   updateUi(result)
               }, {
                   //configuracion del error para que se pueda ver en la consola
                   it.printStackTrace()
                   if (it.networkResponse.statusCode == 400){
                       updateUi(getString(R.string.main_error_server))
                   }
               }){
                   //Body
                   *//*override fun getBodyContentType(): String {
                return super.getBodyContentType()
            }
*//*
            //Apartado para configurar Body y Headers varia segun el backend
            //Headers
            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()

                //formato
                params["Content-Type"] = "application/json"

                return params
            }
        }
        LoginApplication.reqResAPI.addToRequestQueue(jsonObjectRequest)*/
    }

    //configurar respuesta de la ui si la peticion es exitosa la interfaz cambia
    private fun updateUi(result: String) {
        mBinding.tvResult.visibility = View.VISIBLE
        mBinding.tvResult.text = result
    }
}