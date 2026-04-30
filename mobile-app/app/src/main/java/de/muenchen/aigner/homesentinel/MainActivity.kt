package de.muenchen.aigner.homesentinel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.delay
import de.muenchen.aigner.homesentinel.BuildConfig

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       // val retrofit = Retrofit.Builder().baseUrl("http://10.0.2.2:8080/").addConverterFactory(
        val retrofit = Retrofit.Builder().baseUrl(BuildConfig.BACKEND_URL).addConverterFactory(
            GsonConverterFactory.create()).build()

        val apiService = retrofit.create(ApiService::class.java)
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background){
                TelemetryScreen(apiService)
            }
        }
    }
}

@Composable
fun TelemetryScreen(apiService: ApiService){
    var sensorData by remember { mutableStateOf<SensorData?>(null) }

    LaunchedEffect(Unit) {
        while(true){
            try{
                val data = apiService.getLatestData("ESP32-SIM-01")
                sensorData = data
            }catch (e: Exception){
                e.printStackTrace()
            }
            delay(5000)
        }
    }


    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
        Text(text = "HomeSentinel Dashboard", fontSize = 20.sp, modifier = Modifier.padding(bottom = 20.dp))
        if (sensorData != null) {
            Text(text = "Gerät: ${sensorData?.deviceID}", fontSize = 16.sp)
            Text(text = "${sensorData?.value} ${sensorData?.unit}", fontSize = 48.sp)
            Text(text = "Typ: ${sensorData?.sensorType}", color = MaterialTheme.colorScheme.secondary)
        } else {
            CircularProgressIndicator() // Ladekreis, wenn noch keine Daten da sind
            Text(text = "Warte auf Daten...")
        }

    }
}