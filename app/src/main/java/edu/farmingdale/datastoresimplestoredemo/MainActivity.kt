package edu.farmingdale.datastoresimplestoredemo
import android.content.Context
import java.io.PrintWriter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import edu.farmingdale.datastoresimplestoredemo.data.AppPreferences
import edu.farmingdale.datastoresimplestoredemo.ui.theme.DataStoreSimpleStoreDemoTheme
import kotlinx.coroutines.launch
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DataStoreSimpleStoreDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DataStoreDemo(modifier = Modifier.padding(innerPadding))
                }
            }
        }
        writeToInternalFile()
        val fileContents = readFromInternalFile()
        Log.d("MainActivity", fileContents)
    }
    private fun writeToInternalFile() {
        val outputStream: FileOutputStream = openFileOutput("edufilename", Context.MODE_PRIVATE)
        val writer = PrintWriter(outputStream)

        // Write three lines
        writer.println("This world of fun")
        writer.println("and yet, and yet.")

        writer.close()
    }

    private fun readFromInternalFile(): String {
        val inputStream = openFileInput("edufilename")
        val reader = inputStream.bufferedReader()
        val stringBuilder = StringBuilder()

        // Append each line and newline character to stringBuilder
        reader.forEachLine {
            stringBuilder.append(it).append("\n CSC 371 \n").append(System.lineSeparator())
        }

        return stringBuilder.toString()
    }
}

@Composable
fun DataStoreDemo(modifier: Modifier) {
    val store = AppStorage(LocalContext.current)
    val appPrefs = store.appPreferenceFlow.collectAsState(AppPreferences())
    val coroutineScope = rememberCoroutineScope()
    var usernameInput by remember { mutableStateOf("") }

    Column (modifier = Modifier.padding(50.dp)) {
        // ToDo 4: Display DataStore Values
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Stored DataStore Values",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Username: ${appPrefs.value.userName.ifEmpty { "(not set)" }}")
                Text("High Score: ${appPrefs.value.highScore}")
                Text("Dark Mode: ${if (appPrefs.value.darkMode) "Enabled" else "Disabled"}")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Username: ${appPrefs.value.userName}")
        Spacer(modifier = Modifier.height(8.dp))
        //ToDo 2
        TextField(
            value = usernameInput,
            onValueChange = { usernameInput = it },
            label = { Text("Enter Username") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        //ToDo 3
        Button(onClick = {
            coroutineScope.launch {
                store.saveUsername(usernameInput)
            }
        }) {
            Text("Save Username")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // High Score Controls
        Text("High Score: ${appPrefs.value.highScore}")
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Button(onClick = {
                coroutineScope.launch {
                    store.saveHighScore(appPrefs.value.highScore + 10)
                }
            }) {
                Text("+ 10")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                coroutineScope.launch {
                    store.saveHighScore(appPrefs.value.highScore - 10)
                }
            }) {
                Text("- 10")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                coroutineScope.launch {
                    store.saveHighScore(0)
                }
            }) {
                Text("Reset")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dark Mode Toggle
        Row {
            Text("Dark Mode: ${if (appPrefs.value.darkMode) "ON" else "OFF"}")
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = appPrefs.value.darkMode,
                onCheckedChange = { isChecked ->
                    coroutineScope.launch {
                        store.saveDarkMode(isChecked)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ToDo 4
        Button(onClick = {
            coroutineScope.launch {
                usernameInput = ""
                store.saveUsername("")
                store.saveHighScore(0)
                store.saveDarkMode(false)
            }
        }) {
            Text("Clear All Values")
        }
    }
}

// ToDo 1: Modify the App to store a high score and a dark mode preference
// ToDo 2: Modify the APP to store the username through a text field
// ToDo 3: Modify the App to save the username when the button is clicked
// ToDo 4: Modify the App to display the values stored in the DataStore


