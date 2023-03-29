package com.example.coordinadoruca




import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.coordinadoruca.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.http.GET


class MainActivity : AppCompatActivity() {

    private lateinit var Binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        Binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(Binding.root)

        Binding.button.setOnClickListener {procesarDatos()}

    }


    interface ApiService {
        @GET("phpcrud/mostracoor.php")
        suspend fun procesarDatos(): ResponseBody
    }

    object RetrofitClient {
        private const val BASE_URL = "http://10.0.2.2:80/"

        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)
    }


    fun procesarDatos() {
        CoroutineScope(Dispatchers.IO).launch {
            var response: ResponseBody? = null
            try {
                response = RetrofitClient.apiService.procesarDatos()
                val jsonResponse = response.string()
                val jsonArray = JSONArray(jsonResponse)
                val datos = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    val objeto = jsonArray.getJSONObject(i)
                    val idC = objeto.getString("idC")
                    val nombres = objeto.getString("nombres")
                    val apellidos = objeto.getString("apellidos")
                    val fechaNac = objeto.getString("fechaNac")
                    val titulo = objeto.getString("titulo")
                    val email = objeto.getString("email")
                    val facultad = objeto.getString("facultad")
                    if (titulo != "MSc") {
                        val mostrar = "Id: $idC\nNombre: $nombres $apellidos\nFecha de Nacimiento: $fechaNac\nTítulo: $titulo\nEmail: $email\nFacultad: $facultad"
                        datos.add(mostrar)
                    }
                }
                val datosFormatted = datos.joinToString("\n\n")
                withContext(Dispatchers.Main) {
                    Binding.textView.text = datosFormatted
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error al realizar la solicitud HTTP", e)

            } finally {
                response?.close()
            }
        }
    }

}