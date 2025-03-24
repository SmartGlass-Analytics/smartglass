import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource // 👈 this import
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartglassapplication.data.Player

@Composable
fun PlayerCard(player: Player, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("profile/${player.name}/${player.position}/${player.stats}/${player.imageRes}")
            }
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = player.imageRes),
                contentDescription = player.name,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(player.name, style = MaterialTheme.typography.titleMedium)
                Text(player.position, style = MaterialTheme.typography.bodyMedium)
                Text(player.stats, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
