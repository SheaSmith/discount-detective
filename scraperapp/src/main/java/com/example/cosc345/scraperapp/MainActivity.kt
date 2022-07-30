@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cosc345.scraperapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.*
import com.example.cosc345.scraperapp.ui.theme.DiscountDetectiveTheme
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        viewModel.handleLogin(res)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiscountDetectiveTheme {
                MainScreen(signInLauncher = signInLauncher)
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    signInLauncher: ActivityResultLauncher<Intent>?
) {
    val context = LocalContext.current
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
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Text(
                    text = "Example",
                    style = MaterialTheme.typography.bodyLarge
                )
                LinearProgressIndicator(
                    modifier = Modifier.padding(
                        horizontal = 0.dp,
                        vertical = 8.dp
                    )
                )
                // https://stackoverflow.com/questions/64362801/how-to-handle-visibility-of-a-text-in-jetpack-compose
                AnimatedVisibility(visible = viewModel.showLogin) {
                    Button(onClick = {
                        if (signInLauncher != null)
                            viewModel.launchLogin(signInLauncher)
                    }) {
                        Text(text = "Login")
                    }
                }
                Button(onClick = {
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        //.setRequiresCharging(true)
                        .build()

                    val workRequest = OneTimeWorkRequestBuilder<ScraperWorker>()
                        .setConstraints(constraints)
                        .setBackoffCriteria(
                            BackoffPolicy.LINEAR,
                            Duration.ofMinutes(30)
                        )
                        .build()

                    WorkManager
                        .getInstance(context)
                        .enqueueUniqueWork("scraper", ExistingWorkPolicy.REPLACE, workRequest)
                }) {
                    Text(text = "Run scraper now")
                }
                Button(onClick = {
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .setRequiresCharging(true)
                        .build()

                    val workRequest = PeriodicWorkRequestBuilder<ScraperWorker>(Duration.ofDays(1))
                        .setConstraints(constraints)
                        .setBackoffCriteria(
                            BackoffPolicy.LINEAR,
                            Duration.ofMinutes(30)
                        )
                        .build()

                    val workManager = WorkManager.getInstance(context)

                    workManager.cancelAllWork()

                    workManager.enqueueUniquePeriodicWork(
                        "scraper",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        workRequest
                    )
                }) {
                    Text(text = "Schedule scraper")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DiscountDetectiveTheme {
        MainScreen(signInLauncher = null)
    }
}