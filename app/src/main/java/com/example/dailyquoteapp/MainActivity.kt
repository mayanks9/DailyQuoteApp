package com.example.dailyquoteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dailyquoteapp.ui.theme.DailyQuoteAppTheme
import kotlinx.coroutines.*
import org.json.JSONArray

data class Quote(val english: String, val hindi: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DailyQuoteAppTheme {
                var quotes by remember { mutableStateOf<List<Quote>>(emptyList()) }
                var currentQuote by remember { mutableStateOf<Quote?>(null) }

                // Load quotes only once
                LaunchedEffect(Unit) {
                    val jsonStr = withContext(Dispatchers.IO) {
                        assets.open("quotes.json").bufferedReader().use { it.readText() }
                    }
                    val jsonArray = JSONArray(jsonStr)
                    val loaded = mutableListOf<Quote>()
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        loaded.add(
                            Quote(
                                obj.getString("english"),
                                obj.getString("hindi")
                            )
                        )
                    }
                    quotes = loaded
                    currentQuote = loaded.random()
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    if (currentQuote != null) {
                        QuoteScreen(
                            quote = currentQuote!!,
                            onRefresh = { currentQuote = quotes.random() }
                        )
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Loading...")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuoteScreen(quote: Quote, onRefresh: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = quote.english, modifier = Modifier.weight(1f))
            Text(text = quote.hindi, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRefresh) {
            Text("Refresh")
        }
    }
}
