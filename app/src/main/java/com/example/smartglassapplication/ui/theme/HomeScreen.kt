package com.example.smartglassapplication.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.smartglassapplication.R


@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.alabamalogo), // 👈 update with your logo name
            contentDescription = "Alabama Logo",
            modifier = Modifier
                .size(160.dp)
                .padding(bottom = 24.dp)
        )

        Text(
            text = "Welcome",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 28.sp,
                color = Crimson
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Analyze player stats and Insights.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { navController.navigate("stats") },
            colors = ButtonDefaults.buttonColors(containerColor = Crimson),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("View Player Stats", color = Color.White, fontSize = 18.sp)
        }
    }
}
