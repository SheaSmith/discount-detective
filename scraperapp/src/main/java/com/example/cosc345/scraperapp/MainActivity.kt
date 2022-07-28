@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cosc345.scraperapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cosc345.scraperapp.ui.theme.DiscountDetectiveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiscountDetectiveTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(
                    topBar = {
                        LargeTopAppBar(
                            title = { Text("Discount Detective Scraper") }
                        )
                    },
                    content = { innerPadding ->
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            Button(onClick = { /*TODO*/ }) {
                                Text(text = "Login")
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Page() {
    Text(text = "Test")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DiscountDetectiveTheme {
        Page()
    }
}