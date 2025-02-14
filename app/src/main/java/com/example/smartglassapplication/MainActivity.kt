package com.example.smartglassapplication

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartglassapplication.ui.theme.SmartglassApplicationTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val button: Button = findViewById(R.id.button)
        button.setOnClickListener{ pointGuardStats(this)}

        val button2: Button = findViewById(R.id.button2)
        button2.setOnClickListener{ shootingGuardStats(this)}

        val button3: Button = findViewById(R.id.button3)
        button3.setOnClickListener{ smallForwardStats(this)}

        val button4: Button = findViewById(R.id.button4)
        button4.setOnClickListener{ powerForwardStats(this)}

        val button5: Button = findViewById(R.id.button5)
        button5.setOnClickListener{ centerStats(this)}

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Surface(color=Color.Red) {
        Text(
            text = "Hello $name!",
            modifier = modifier.padding(24.dp)
        )
    }
}

fun pointGuardStats(view: MainActivity){
    Toast.makeText(view, "5 Pts, 4 Ast, 1 Reb", Toast.LENGTH_SHORT).show()
}

fun shootingGuardStats(view: MainActivity){
    Toast.makeText(view, "10 Pts, 1 Ast, 2 Reb", Toast.LENGTH_SHORT).show()
}

fun smallForwardStats(view: MainActivity){
    Toast.makeText(view, "8 Pts, 0 Ast, 4 Reb", Toast.LENGTH_SHORT).show()
}

fun powerForwardStats(view: MainActivity){
    Toast.makeText(view, "2 Pts, 0 Ast, 6 Reb", Toast.LENGTH_SHORT).show()
}

fun centerStats(view: MainActivity){
    Toast.makeText(view, "3 Pts, 1 Ast, 11 Reb", Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmartglassApplicationTheme {
        Greeting("Basketball Smartglass")
    }
}